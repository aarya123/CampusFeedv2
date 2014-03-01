package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.db.DB;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok("hello world");
    }
    
    private static final String CREATE_USER_SQL = "INSERT INTO users (name) VALUES (?)";
    public static Result create_user() {
    	RequestBody request = request().body();
    	JsonNode obj = request.asJson();
    	String name = null;
    	if(obj != null) {
    		name = obj.get("name").textValue();
    	}
    	if(name == null) {
    		return badRequest("usage: json object with name");
    	}
    	try(Connection connection = DB.getConnection()) {
    		PreparedStatement stmt = connection.prepareStatement(CREATE_USER_SQL);
    		stmt.setString(1, name);
    		stmt.execute();
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return ok("ok");
    }

}
