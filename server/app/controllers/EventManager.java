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
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
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
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_user (event_id,user_id,is_admin) VALUES (?,?,?)");
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
		return ok("{\"response\":\"success\"}");
	}
	
	
	public static int createFromScrapedPage(JsonNode request)
	{

		
		String title = request.get("title").textValue();
		String desc = request.get("desc").textValue();
		String location = request.get("location").textValue();
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
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
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
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_user (event_id,user_id,is_admin) VALUES (?,?,?)");
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
		PreparedStatement stmt = conn.prepareStatement("SELECT rsvp FROM CampusFeed.Event_has_user WHERE user_id=? AND event_id=?");
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
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_user (user_id,event_id,rsvp,is_admin) VALUES (?,?,1,0)");
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

}