package controllers;

import play.mvc.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import play.*;
import play.db.DB;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.*;
import play.mvc.Http.RequestBody;
public class GCMHandler extends Controller {
		/*
		 * 		Register User
		 *			
		 */
		public static Result RegisterUser()
		{
			JsonNode request = request().body().asJson();
			String UserId = request.get("fb_user_id").textValue();
			String Gcm_Id = request.get("gcm_id").textValue();
			// check if user does not exist, if exists, update the gcm_id for user
			try(Connection conn = DB.getConnection()) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User`  WHERE fb_user_id=?");
				stmt.setString(1, UserId);
				ResultSet s =stmt.executeQuery();
				if(s.next())
				{
					// user does exist
					// so update user gcm id
					PreparedStatement stmtupdate = conn.prepareStatement("UPDATE User SET gcm_id = ? WHERE fb_user_id=?");
					stmtupdate.setString(1,Gcm_Id);
					stmtupdate.setString(2, UserId);
					stmtupdate.executeUpdate();
				}
				else{
						// user does not exist
					return badRequest(JsonNodeFactory.instance.objectNode().put("error", "user does not exist"));	
				}
				s.close();
				return ok(JsonNodeFactory.instance.objectNode().put("success", "updated gcm id"));
			}catch(Exception e)
			{
				return badRequest(JsonNodeFactory.instance.objectNode().put("error", "error in request:"+e.toString()));	
			}
		}
		
		/*
		 * Messenger 
		 * 
		 */
		public static int sendMessage(ArrayList<String> gcm_ids, String message)
		{
		
			// api key
			String api_key ="AIzaSyDBVU6IDgiH0Asp8FT98y4_TTJTz5sLNWs";
			
			// now that we have the user gcm id, we will send the message
			JSONObject obj = new JSONObject();
		
			JSONArray gcm_ids_json_array = new JSONArray();
			for(int x=0;x<gcm_ids.size();x++)
			{
				gcm_ids_json_array.put((gcm_ids).get(x));
			}
			
			String registration_ids = gcm_ids_json_array.toString();
			System.out.println(registration_ids+"\n\n");
			JSONObject msg = new JSONObject();
	
			try {
				msg.put("response", message);
				obj.put("data",msg);
				obj.put("registration_ids", registration_ids);
			
			} catch (JSONException e) {

				e.printStackTrace();
				return -1;
			}
			
			
			System.out.println(obj.toString());
			WS.url("https://android.googleapis.com/gcm/send")
				.setContentType("application/json")
				.setHeader("Authorization","key="+api_key)
    			.post(obj.toString());
				return 0;
		
		}
		
		
}
