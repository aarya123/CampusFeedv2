package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.db.DB;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.*;
import play.mvc.Http.RequestBody;
import views.html.*;

public class EventManager extends Controller{
	
	public static Result create()
	{
		/* OAuth*/
		JsonNode request = request().body().asJson();
		if(request==null)
		{
			return internalServerError();
		}
		try {
    		Application.checkReqValid(request);
    	}
    	catch(AuthorizationException e) {
    		return unauthorized(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		return internalServerError();
    	}
		
		
		String title = request.get("title").textValue();
		String desc = request.get("desc").textValue();
		String location = request.get("location").textValue();
		String time_string = request.get("date_time").textValue();
		int visibility = request.get("visibility").intValue();
		String category = request.get("category").textValue();
		
		// convert time to date
		Date datetime=null;
		try {
			datetime = new SimpleDateFormat("M-d-yyyy k:m").parse(time_string);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
			response().setContentType("application/json");
			return ok("{\"response\":\"error, bad date\"}");
			
		}
		long t = datetime.getTime();
				 
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);

		// insert into database
		ResultSet return_info;

		int event_id=-1;
		try(Connection conn = DB.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility,category) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
			stmt.setString(6, category);
			stmt.setString(4, desc);
			stmt.setInt(5, visibility);
			
			stmt.executeUpdate();
			return_info = stmt.getGeneratedKeys();
			// get the generated primary key
			if(return_info.next())
			{
				event_id = return_info.getInt(1);
				
			}
			
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			//return -1;
			response().setContentType("application/json");
			return ok("{\"response\":\"error, sql exception\"}");
		}
		// now insert into event has users
		// get the user id of current user
	
		long user_id =Application.getUserId(request);
		try(Connection conn = DB.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_User (event_id,user_id,is_admin) VALUES (?,?,?)");
			stmt.setInt(1,  event_id);
			stmt.setLong(2, user_id);
			stmt.setInt(3, 1);
			
			
			stmt.executeUpdate();
		
		
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			response().setContentType("application/json");
			return ok("{\"response\":\"error, sql exception\"}");
		}
		
		
		response().setContentType("application/json");
		return ok("{\"response\":\"success\"}+ category="+category);
	}
	
	
	public static int createFromScrapedPage(JsonNode request)
	{

		
		String title = request.get("title").textValue();
		String desc = request.get("desc").textValue();
		String location = request.get("location").textValue();
		String category = request.get("category").textValue();
		String time_string = request.get("date_time").textValue();
		// visibility =1 auto
		
		// convert time to date
		Date datetime=null;
		try {
			datetime = new SimpleDateFormat("M-d-yyyy k:m").parse(time_string);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
			return -1;
			
		}
		long t = datetime.getTime();
				 
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);

		// insert into database
		ResultSet return_info;

		int event_id=-1;
		try(Connection conn = DB.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility,category) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
			stmt.setString(6, category);
			stmt.setString(4, desc);
			stmt.setInt(5, 1);
			
			stmt.executeUpdate();
			return_info = stmt.getGeneratedKeys();
			// get the generated primary key
			if(return_info.next())
			{
				event_id = return_info.getInt(1);
				
			}
			
			
		}
		catch(SQLException e) {
			e.printStackTrace();

			return -1;
		}
		
		
		long user_id = ScraperHandler.SCRAPER_ID;
		try(Connection conn = DB.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_User (event_id,user_id,is_admin) VALUES (?,?,?)");
			stmt.setInt(1,  event_id);
			stmt.setLong(2, user_id);
			stmt.setInt(3, 1);
			
			
			stmt.executeUpdate();
		
		
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		
		
		
	
		return event_id;
	}
	
public static Result rsvp_to_event()
{
	/* OAuth*/
	JsonNode request = request().body().asJson();
	if(request==null)
	{
		return internalServerError();
	}
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return unauthorized(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return internalServerError();
	}
	// add user to rsvp
	// get the user id
	long user_id =Application.getUserId(request);
	// main thing, only need event_id
	int event_id = Integer.parseInt(request.get("event_id").textValue());
	// check if user has already rsvp'd
	boolean should_add = false;
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT rsvp FROM CampusFeed.Event_has_User WHERE user_id=? AND event_id=?");
		stmt.setLong(1, user_id);
		stmt.setInt(2, event_id);
		stmt.execute();
		ResultSet rs = stmt.getResultSet();
		int rsvp=-1;
		if(rs.next()) {
			rsvp= rs.getInt("rsvp");
		}
		else
		{
			should_add=true;
		}
		if(rsvp==1){
			return ok("duplicate");
		}else
		if(rsvp!=1 && should_add!=true)
		{
			// then only need to update, incase if undo rsvp or something.
			try(Connection conn2 = DB.getConnection()) {
				PreparedStatement stmt2 = conn2.prepareStatement("UPDATE `CampusFeed`.`Event_has_User` SET `rsvp` = '1' WHERE `event_has_user`.`event_id` = ? AND `event_has_user`.`user_id` = ?");
				stmt2.setLong(2, user_id);
				stmt2.setInt(1, event_id);
				stmt2.executeUpdate();
				
				
			}
			catch(SQLException e) {
				e.printStackTrace();

				return internalServerError();
			}
			
		}
		
		
		
	}
	catch(SQLException e) {
		e.printStackTrace();

		return internalServerError();
	}
	if(should_add){
	// main thing, add to rsvp 
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_User (user_id,event_id,rsvp,is_admin) VALUES (?,?,1,0)");
		stmt.setLong(1, user_id);
		stmt.setInt(2, event_id);
		stmt.executeUpdate();
		
		
	}
	catch(SQLException e) {
		e.printStackTrace();

		return internalServerError();
	}
	
	}
	
	
	
	return ok("success");
}

/**
 * Helper function to create event json from results
 * @param rs result set from query on a valid event row
 * @return the json event
 * @throws SQLException
 */
private static ObjectNode createEventJson(ResultSet rs) throws SQLException {
	ObjectNode searchResult = JsonNodeFactory.instance.objectNode();
	searchResult.put("id", rs.getString("id"));
	searchResult.put("name", rs.getString("name"));
	searchResult.put("location", rs.getString("location"));
	searchResult.put("time", rs.getInt("time"));
	searchResult.put("description", rs.getString("description"));
	//searchResult.put("category", rs.getString("category"));
	searchResult.put("status", rs.getInt("status"));
	return searchResult;
}
public static Result search() {
	JsonNode request = request().body().asJson();
	//check params
	String query;
	if(!request.has("query")) {
		return badRequest(JsonNodeFactory.instance.objectNode().put("error", "usage: query (text)"));
	}
	query = request.get("query").textValue();
	try(Connection conn = DB.getConnection()) {
		try(PreparedStatement stmt = conn.prepareStatement("SELECT id, name, location, UNIX_TIMESTAMP(time) AS time, description, status FROM Event WHERE name LIKE ?")) {
    		stmt.setString(1, "%" + query + "%");
    		ResultSet rs = stmt.executeQuery();
    		ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
    		if(rs.next()) {
    			do {
    				searchResults.add(createEventJson(rs));
    			}
    			while(rs.next());
    		}
    		return ok(searchResults);
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
		return internalServerError();
	}
}

public static Result listEvent() {
	JsonNode request = request().body().asJson();
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return unauthorized(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return internalServerError();
	}
	//check params
	int page;
	if(!request.has("page")) {
		return badRequest(JsonNodeFactory.instance.objectNode().put("error", "usage: page (int)"));
	}
	page = request.get("page").intValue();
	try(Connection conn = DB.getConnection()) {
		try(PreparedStatement stmt = conn.prepareStatement("SELECT id, name, location, UNIX_TIMESTAMP(time) AS time, description, status, category FROM Event LIMIT 25 OFFSET ?")) {
    		stmt.setInt(1, page * 25);
    		ResultSet rs = stmt.executeQuery();
    		ArrayNode searchResults = JsonNodeFactory.instance.arrayNode();
    		if(rs.next()) {
    			do {
    				searchResults.add(createEventJson(rs));
    			}
    			while(rs.next());
    		}
    		return ok(searchResults);
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
		return internalServerError();
	}
}


public static Result popularByCategory()
{
	JsonNode request = request().body().asJson();
	String category = request.get("category").textValue();
	JSONArray list = new JSONArray();
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Event`  WHERE category=? LIMIT 0,3");
		stmt.setString(1, category);
		ResultSet s =stmt.executeQuery();
	
		while(s.next())
		{
			try {
				JSONObject event = new JSONObject();
				event.put("title", s.getString("name"));
				event.put("id", s.getString("id"));
				event.put("desc", s.getString("description"));
				event.put("date_time",s.getTimestamp("time"));
				event.put("location", s.getString("location"));
				event.put("view_count",s.getInt("view_count"));
				event.put("category", s.getString("category"));
				list.put(event);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		s.close();
		
		
	}
	catch(SQLException e) {
		e.printStackTrace();
		response().setContentType("application/json");
		return ok("{\"response\":\"error, sql exception\"}");
	}
	
	
	return ok(list.toString());
}
public static Result updateEvent()
{JsonNode request = request().body().asJson();
String id = request.get("id").textValue();
String title = request.get("title").textValue();
String desc = request.get("desc").textValue();
String location = request.get("location").textValue();

String time_string = request.get("date_time").textValue();
try(Connection conn2 = DB.getConnection()) {
	PreparedStatement stmt2 = conn2.prepareStatement("UPDATE `CampusFeed`.`Event` SET name=?, location=?,description=?,time=?   WHERE `Event`.`id` = ?");
	stmt2.setString(1, title);
	stmt2.setString(2, location);
	stmt2.setString(3, desc);
	Date datetime=null;
	try {
		datetime = new SimpleDateFormat("M-d-yyyy k:m").parse(time_string);
	} catch (ParseException e1) {
		// TODO Auto-generated catch block
		
		e1.printStackTrace();
		response().setContentType("application/json");
		return ok("{\"response\":\"error, bad date\"}");
		
	}
	long t = datetime.getTime();
			 
	java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);

	stmt2.setTimestamp(4, sqlTimestamp);
	stmt2.setString(5, id);
	stmt2.executeUpdate();
	
	
}
catch(SQLException e) {
	e.printStackTrace();

	return internalServerError();
}
	
	return ok("success");
}

public static Result all()
{
	
	JSONArray list = new JSONArray();
	try(Connection conn = DB.getConnection()) {
		try(PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Event`")) {
			try(ResultSet s =stmt.executeQuery()) {
				while(s.next())
				{
					list.put(createEventJson(s));
				}
			}
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
		response().setContentType("application/json");
		return ok("{\"response\":\"error, sql exception\"}");
	}
	
	
	return ok(list.toString());
}

public static Result top5() {
	JsonNode request = request().body().asJson();
	String category = request.get("category").textValue();
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Event` WHERE category = ? ORDER BY view_count DESC LIMIT 5");
		stmt.setString(1, category);
		ResultSet s = stmt.executeQuery();
		ArrayNode res = JsonNodeFactory.instance.arrayNode();
		JSONArray list = new JSONArray();
		while(s.next()) {
			JSONObject event = new JSONObject();
			try {
				event.put("title", s.getString("name"));
				event.put("id", s.getString("id"));
				event.put("desc", s.getString("description"));
				event.put("date_time",s.getTimestamp("time"));
				event.put("location", s.getString("location"));
				event.put("view_count",s.getInt("view_count"));
				event.put("category", s.getString("category"));
			}
			catch(JSONException e) {
				
			}
			list.put(event);
		}
		return ok(list.toString());
		
	}
	catch(SQLException e) {
		return internalServerError(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
}


}
