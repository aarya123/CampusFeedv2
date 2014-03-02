package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import play.*;
import play.db.DB;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.*;

public class Application extends Controller {

	private static final String APP_ID = "582917605129839";
    private static final String APP_SECRET = "86dcd6d4ad418ac5fcd84806eaeebfd3";
    private static final long FB_TIMEOUT = 10000L;
    
    public static Result index() {
        return ok("hello world");
    }
    
    /**
     * Helper function to convert query string to map
     * @param stream stream of query
     * @return the map
     */
    public static Map<String, String> queryToMap(InputStream stream) {
    	Scanner in = new Scanner(stream);
    	Map<String, String> query = new HashMap<>();
    	in.useDelimiter("[=&]");
    	while(in.hasNext()) {
    		String key = in.next();
    		String val = in.next();
    		query.put(key, val);
    	}
    	return query;
    }
    
    private static final String LOGIN_USER_SQL = "INSERT INTO user (fb_user_id, access_token, expires) VALUES (?, ?, FROM_UNIXTIME(?)) ON DUPLICATE KEY UPDATE access_token=?, expires=FROM_UNIXTIME(?)";
    public static Result login() {
    	RequestBody request = request().body();
    	//retrieve access token param
    	JsonNode obj = request.asJson();
    	String access_token = null;
    	if(obj != null) {
    		access_token = obj.get("access_token").textValue();
    	}
    	if(access_token == null) {
    		return badRequest("usage: json object with access_token");
    	}
    	//retrieve user id
    	long userId;
    	try {
    		JsonNode userInfo = WS.url("https://graph.facebook.com/me")
    				.setQueryParameter("fields", "id")
        			.setQueryParameter("access_token", access_token)
        			.get()
        			.get(FB_TIMEOUT)
        			.asJson();
    		if(userInfo.has("id")) {
    			userId = userInfo.get("id").asLong();
    		}
    		else {
    			return badRequest(JsonNodeFactory.instance.objectNode().put("error", "invalid access token"));
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return internalServerError();
    	}
    	//token exchange - get long lived access token and time till it expires in seconds
    	Response accessTokenResp = WS.url("https://graph.facebook.com/oauth/access_token")
		    	.setQueryParameter("grant_type", "fb_exchange_token")
		    	.setQueryParameter("client_id", APP_ID)
		    	.setQueryParameter("client_secret", APP_SECRET)
		    	.setQueryParameter("fb_exchange_token", access_token)
		    	.get()
		    	.get(FB_TIMEOUT);
    	long expires = 0;
    	try(InputStream in = accessTokenResp.getBodyAsStream()) {
    		Map<String, String >respVals = queryToMap(in);
    		if(!respVals.containsKey("access_token") || !respVals.containsKey("expires")) {
    			System.err.println("Access token or expires not present!");
    			return internalServerError();
    		}
    		else {
    			access_token = respVals.get("access_token");
    			expires = Long.parseLong(respVals.get("expires"));
    		}
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    		return internalServerError();
    	}
    	//write new info or updated info to db
    	try(Connection connection = DB.getConnection()) {
    		PreparedStatement stmt = connection.prepareStatement(LOGIN_USER_SQL);
    		stmt.setLong(1, userId);
    		stmt.setString(2, access_token);
    		long expiresTime = expires + (System.currentTimeMillis() / 1000L);
    		stmt.setLong(3, expiresTime);
    		stmt.setString(4,  access_token);
    		stmt.setLong(5, expiresTime);
    		stmt.execute();
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		return internalServerError();
    	}
    	//return long lived access token to use as auth
    	JsonNode resp = JsonNodeFactory.instance.objectNode().put("access_token", access_token);
    	return Application.ok(resp);
    }

}
