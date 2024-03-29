package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
	
	public static final String EVENT_GET_SQL_UNRESTRICTED = "select distinct Event.id as id, Event.name as name, Event.location as location, UNIX_TIMESTAMP(Event.time) as time, Event.description as description, Event.visibility as visibility, Event.view_count as view_count, Event_has_User.rsvp as rsvp from Event inner join Event_has_Tags on Event.id = Event_has_Tags.Event_id inner join Tags on Event_has_Tags.Tags_id = Tags.id inner join Event_has_User on Event.id = Event_has_User.event_id";
	public static final String EVENT_GET_SQL = "select distinct Event.id as id, Event.name as name, Event.location as location, UNIX_TIMESTAMP(Event.time) as time, Event.description as description, Event.visibility as visibility, Event.view_count as view_count, Event_has_User.rsvp as rsvp from Event inner join Event_has_Tags on Event.id = Event_has_Tags.Event_id inner join Tags on Event_has_Tags.Tags_id = Tags.id inner join Event_has_User on Event.id = Event_has_User.event_id WHERE (Event.visibility = 1 OR (Event_has_User.user_id = ? AND (Event_has_User.rsvp != -1 or Event_has_User.is_admin != 0)))";
	
	public static ArrayNode buildEventResults(Connection conn, ResultSet rs, long userId) throws SQLException {
		ArrayNode arr = JsonNodeFactory.instance.arrayNode();
		while(rs.next()) {
				ObjectNode eventRes = createEventJson(rs);
				try(PreparedStatement stmtTag = conn.prepareStatement("select Tags.tag from Tags INNER JOIN Event_has_Tags ON Tags.id = Event_has_Tags.Tags_id INNER JOIN Event ON Event.id = Event_has_Tags.Event_id WHERE Event.id = ?")) {
					stmtTag.setLong(1, rs.getLong("id"));
					stmtTag.execute();
					ResultSet rsTag = stmtTag.getResultSet();
					addCategoriesToEventJson(eventRes, rsTag);
				}
				try(PreparedStatement stmtAdmin = conn.prepareStatement("select is_admin, rsvp from Event_has_User where event_id = ? and user_id = ?")) {
					stmtAdmin.setLong(1, rs.getLong("id"));
					stmtAdmin.setLong(2, userId);
					stmtAdmin.execute();
					ResultSet rsAdmin = stmtAdmin.getResultSet();
					if(rsAdmin.next()) {
						eventRes.put("is_admin", rsAdmin.getInt("is_admin"));
						eventRes.put("rsvp", rsAdmin.getInt("rsvp"));
					}
					else {
						eventRes.put("is_admin", 0);
						eventRes.put("rsvp", "-1");
					}
					rsAdmin.close();
				}
				try(PreparedStatement stmtCreator = conn.prepareStatement("select user_id from Event_has_User where event_id = ? and is_admin != 0")) {
					stmtCreator.setLong(1, rs.getLong("id"));
					stmtCreator.execute();
					ResultSet rsCreator = stmtCreator.getResultSet();
					if(rsCreator.next()) {
						try(PreparedStatement stmtCreatorInfo = conn.prepareStatement("select first_name, last_name from User where id = ?")) {
							stmtCreatorInfo.setLong(1, rsCreator.getLong("user_id"));
							stmtCreatorInfo.execute();
							ResultSet rsCreatorInfo = stmtCreatorInfo.getResultSet();
							if(rsCreatorInfo.next()) {
								eventRes.put("creator", 
										JsonNodeFactory.instance.objectNode()
										.put("first_name", rsCreatorInfo.getString("first_name"))
										.put("last_name", rsCreatorInfo.getString("last_name"))
								);
							}
							rsCreatorInfo.close();
						}
					}
					rsCreator.close();
				}
				arr.add(eventRes);
		}
		return arr;
	}
	public static Result create()
	{
		/* OAuth*/
		JsonNode request = request().body().asJson();
		try {
    		Application.checkReqValid(request);
    	}
    	catch(AuthorizationException e) {
    		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		return ok();
    	}
		String title, desc, location;
		long timestamp;
		int visibility;
		ArrayNode categories;
		
		try {
			title = request.get("title").textValue();
			desc = request.get("desc").textValue();
			location = request.get("location").textValue();
			timestamp = request.get("date_time").asLong();
			visibility = request.get("visibility").intValue();
			if(request.has("categories")) {
			categories = (ArrayNode) request.get("categories");
			}
			else {
				throw new Exception("categories");
			}
		}
		catch(Exception e) {
			return ok(JsonNodeFactory.instance.objectNode()
					.put("error", "Parameters: title (string), desc(string), location(string), date_time(long), visibility(int), categories(array)"));
		}
		// convert time to date
				 
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(timestamp);

		Connection conn = DB.getConnection();
		try {
			conn.setAutoCommit(false);
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
			stmt.setString(4, desc);
			stmt.setInt(5, visibility);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			// get the generated primary key
			if(rs.next())
			{
				long event_id = rs.getLong(1);
				long user_id =Application.getUserId(request);
				stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_User (event_id,user_id,is_admin) VALUES (?,?,?)");
				stmt.setLong(1,  event_id);
				stmt.setLong(2, user_id);
				stmt.setInt(3, 1);
				stmt.executeUpdate();
				String[] categoriesStr = new String[categories.size()];
				for(int i = 0; i < categories.size(); ++i) {
					categoriesStr[i] = categories.get(i).textValue();
				}
				addTags(conn, event_id, categoriesStr);
				conn.commit();
				conn.close();
				return ok(JsonNodeFactory.instance.objectNode().put("event_id", event_id));
			}
			else {
				throw new SQLException();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return ok();
		}
		
	}
	
	
	public static int createFromScrapedPage(JsonNode request)
	{

		/* OAuth*/
		
		
		String title, desc, location;
		long timestamp;
		int visibility;
		ArrayNode categories;
		
		try {
			title = request.get("title").textValue();
			desc = request.get("desc").textValue();
			location = request.get("location").textValue();
			timestamp = request.get("date_time").asLong();
			visibility = request.get("visibility").intValue();
			if(request.has("categories")) {
			categories = (ArrayNode) request.get("categories");
			}
			else {
				throw new Exception("categories");
			}
		}
		catch(Exception e) {
			return -1;
		}
		// convert time to date
				 
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(timestamp);

		Connection conn = DB.getConnection();
		try {
			conn.setAutoCommit(false);
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event (name,location,time,description,visibility) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, title);
			stmt.setString(2, location);
			stmt.setTimestamp(3, sqlTimestamp);
			stmt.setString(4, desc);
			stmt.setInt(5, visibility);
			
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			// get the generated primary key
			if(rs.next())
			{
				long event_id = rs.getLong(1);
				long user_id =Application.getUserId(request);
				stmt = conn.prepareStatement("INSERT INTO CampusFeed.Event_has_User (event_id,user_id,is_admin) VALUES (?,?,?)");
				stmt.setLong(1,  event_id);
				stmt.setLong(2, ScraperHandler.SCRAPER_ID);
				stmt.setInt(3, 1);
				stmt.executeUpdate();
				String[] categoriesStr = new String[categories.size()];
				for(int i = 0; i < categories.size(); ++i) {
					categoriesStr[i] = categories.get(i).textValue();
				}
				addTags(conn, event_id, categoriesStr);
				conn.commit();
				conn.close();
				return 1;
			}
			else {
				throw new SQLException();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
				return 1;
			} catch (SQLException e1) {
				e1.printStackTrace();
				return -1;
			}
			
		}
		
	
	}
	
public static Result rsvp_to_event()
{
	/* OAuth*/
	JsonNode request = request().body().asJson();
	if(request==null)
	{
		return ok();
	}
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok();
	}
	// add user to rsvp
	// get the user id
	long user_id =Application.getUserId(request);
	// main thing, only need event_id
	long event_id;
	int rsvp_status;
	try {
		event_id = request.get("event_id").longValue();
		rsvp_status = request.get("rsvp_status").intValue();
	}
	catch(Exception ex) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", "usage: event_id, rsvp_status"));
	}
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT rsvp FROM CampusFeed.Event_has_User WHERE user_id=? AND event_id=?");
		stmt.setLong(1, user_id);
		stmt.setLong(2, event_id);
		stmt.execute();
		ResultSet rs = stmt.getResultSet();
		if(rs.next()) {
			PreparedStatement update = conn.prepareStatement("update Event_has_User set rsvp = ? where user_id = ? and event_id = ?");
			update.setInt(1, rsvp_status);
			update.setLong(2, user_id);
			update.setLong(3, event_id);
			update.execute();
		}
		else
		{
			PreparedStatement insert = conn.prepareStatement("insert into Event_has_User (rsvp, event_id, user_id) values (?, ?, ?)");
			insert.setInt(1, rsvp_status);
			insert.setLong(2, event_id);
			insert.setLong(3, user_id);
			insert.execute();
		}
	}
	catch(Exception e) {
		e.printStackTrace();
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.toString()));
	}
	return ok(JsonNodeFactory.instance.objectNode().put("ok", "ok"));
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
	searchResult.put("visibility", rs.getInt("visibility"));
	searchResult.put("view_count", rs.getInt("view_count"));
	searchResult.put("rsvp", rs.getInt("rsvp"));
	return searchResult;
}

/**
 * Helper function to add categories to event json
 * @param event
 * @param rs
 * @throws SQLException
 */
private static void addCategoriesToEventJson(ObjectNode event, ResultSet rs) throws SQLException {
	ArrayNode categories = JsonNodeFactory.instance.arrayNode();
	while(rs.next()) {
		categories.add(rs.getString("tag"));
	}
	event.put("categories", categories);
}

public static Result advSearch() {
	JsonNode request = request().body().asJson();
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok();
	}
	// get the user id
	long user_id =Application.getUserId(request);
	//check params
	if(!request.has("name") && !(request.has("start_date") && request.has("end_date")) &&
			!request.has("desc") && !request.has("tags")){
		return ok(JsonNodeFactory.instance.objectNode().put("error", "usage: name (text) or (start_date (date) and end_date (date)) or desc (text) or tags (array)"));
	}
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = null;
		List<Object> params = new ArrayList<Object>();
		params.add(user_id);
		String sql = EVENT_GET_SQL;
		if(request.has("name")) {
			if(params.size() != 0) {
				sql += " AND ";
			}
			sql += "name like ?";
			params.add("%" + request.get("name").textValue() + "%");
		}
		if(request.has("start_date") && request.has("end_date")) {
			if(params.size() != 0) {
				sql += " AND ";
			}
			sql += "UNIX_TIMESTAMP(time) BETWEEN ? AND ?";
			params.add(request.get("start_date").asLong());
			params.add(request.get("end_date").asLong());
		}
		if(request.has("desc")) {
			if(params.size() != 0) {
				sql += " AND ";
			}
			sql += "description like ?";
			params.add("%" + request.get("desc").textValue() + "%");
		}
		if(request.has("tags")) {
			ArrayNode tags = (ArrayNode) request.get("tags");
			if(params.size() != 0) {
				sql += " AND (";
			}
			for(int i = 0; i < tags.size(); ++i) {
				sql += "Tags.tag = ?";
				params.add(tags.get(i).textValue());
				if(i < tags.size() - 1) {
					sql += " OR ";
				}
			}
			sql += ")";
		}
		stmt = conn.prepareStatement(sql+" ORDER BY Event.view_count DESC");
		for(int i = 0; i < params.size(); ++i) {
			stmt.setObject(i + 1, params.get(i));
		}
		stmt.execute();
		ResultSet rs = stmt.executeQuery();
		return ok(buildEventResults(conn, rs, user_id));
	}
	catch(Exception e) {
		e.printStackTrace();
		return ok();
	}
}

public static Result listEvent() {
	JsonNode request = request().body().asJson();
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok();
	}
	// get the user id
	long user_id =Application.getUserId(request);
	//check params
	int page;
	try {
		page = request.get("page").intValue();
	}
	catch(Exception e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", "usage: page (int)"));
	}
	try(Connection conn = DB.getConnection()) {
		try(PreparedStatement stmt = conn.prepareStatement(EVENT_GET_SQL + "ORDER BY Event.view_count DESC LIMIT 25 OFFSET ?")) {
			stmt.setLong(1, user_id);
    		stmt.setInt(2, page * 25);
    		ResultSet rs = stmt.executeQuery();
    		return ok(buildEventResults(conn, rs, user_id));
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok();
	}
}

private static void addTags(Connection conn, long eventId, String[] tags) throws SQLException{
	PreparedStatement lookupTag = conn.prepareStatement("SELECT id FROM Tags WHERE tag = ?");
	PreparedStatement addTag = conn.prepareStatement("INSERT INTO Tags (tag) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
	PreparedStatement linkTag = conn.prepareStatement("INSERT INTO Event_has_Tags (Event_id, Tags_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
	for(String tag : tags) {
		lookupTag.setString(1, tag);
		lookupTag.execute();
		ResultSet rs;
		long tag_id = -1;
		if((rs = lookupTag.getResultSet()).next()) {
			tag_id = rs.getLong(1);
		}
		else {
			addTag.setString(1, tag);
			addTag.execute();
			if((rs = addTag.getGeneratedKeys()).next()) {
				tag_id = rs.getLong(1);
			}
		}
		linkTag.setLong(1, eventId);
		linkTag.setLong(2, tag_id);
		linkTag.execute();
	}
	lookupTag.close();
	addTag.close();
	linkTag.close();
}
public static Result updateEvent()
{
	/* OAuth*/
	JsonNode request = request().body().asJson();
	try {
		Application.checkReqValid(request);
	}
	catch(AuthorizationException e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok();
	}
	String title, desc, location;
	long timestamp, id;
	int visibility;
	ArrayNode categories;
	try {
		title = request.get("title").textValue();
		desc = request.get("desc").textValue();
		location = request.get("location").textValue();
		timestamp = request.get("date_time").asLong();
		visibility = request.get("visibility").intValue();
		id = request.get("id").longValue();
		categories = (ArrayNode) request.get("categories");
		if(categories == null) {
			throw new Exception("categories");
		}
		
	}
	catch(Exception e) {
		e.printStackTrace();
		return ok(JsonNodeFactory.instance.objectNode()
				.put("error", "Parameters: title (string), desc(string), location(string), date_time(long), visibility(int), categories(array)"));
	}
	long userId = Application.getUserId(request);
	Connection conn = DB.getConnection();
	try {
		conn.setAutoCommit(false);
		PreparedStatement isAdminQuery = conn.prepareStatement("select is_admin from CampusFeed.Event_has_User where event_id = ? and user_id = ?");
		isAdminQuery.setLong(1, id);
		isAdminQuery.setLong(2, userId);
		isAdminQuery.execute();
		boolean isAdmin = false;
		ResultSet rs = isAdminQuery.getResultSet();
		if(rs.next()) {
			isAdmin = rs.getInt("is_admin") != 0;
		}
		isAdminQuery.close();
		if(!isAdmin) {
			conn.close();
			return ok(JsonNodeFactory.instance.objectNode().put("no", "not admin"));
		}
		PreparedStatement stmt2 = conn.prepareStatement("UPDATE `CampusFeed`.`Event` SET name=?, location=?,description=?,time=?,visibility=?   WHERE `Event`.`id` = ?");
		stmt2.setString(1, title);
		stmt2.setString(2, location);
		stmt2.setString(3, desc);
		stmt2.setTimestamp(4, new Timestamp(timestamp));
		stmt2.setInt(5, visibility);
		stmt2.setLong(6, id);
		stmt2.executeUpdate();
		stmt2 = conn.prepareStatement("DELETE Event_has_Tags FROM Event_has_Tags INNER JOIN Event ON Event_has_Tags.Event_id = Event.id  WHERE (Event.id = ?)");
		stmt2.setLong(1, id);
		stmt2.executeUpdate();
		String[] categoriesStr = new String[categories.size()];
		for(int i = 0; i < categories.size(); ++i) {
			categoriesStr[i] = categories.get(i).textValue();
		}
		addTags(conn, id, categoriesStr);
		conn.commit();
		conn.close();
		// send messages to all users
		
			GCMHandler.sendMessage(get_user_ids(),id);
		// end messaging.
		return ok(JsonNodeFactory.instance.objectNode().put("ok", "ok"));
	}
	catch(SQLException e) {
		e.printStackTrace();
		return ok("err", e.getMessage());
	}
	
}
public static ArrayList<String> get_user_ids()
{
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE gcm_id IS NOT NULL");
		ResultSet s =stmt.executeQuery();
		ArrayList<String> user_ids = new ArrayList<String>();
		while(s.next())
		{
			String gcm_id = s.getString("gcm_id");
			user_ids.add(gcm_id);
			System.out.println(gcm_id+"\n");
		}
		s.close();
		return user_ids;
	}catch(Exception e)
	{
		return null;
	}

}

public static Result allTags()
{
	
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Tags");
		ResultSet s =stmt.executeQuery();
		ArrayList<String> tags = new ArrayList<String>();
		while(s.next())
		{
			tags.add(s.getString("tag"));
		}
		s.close();
		JsonNode tags_json=	JsonNodeFactory.instance.objectNode().put("tags", tags.toString());
		return ok(tags_json);
	}catch(Exception e)
	{
		return ok();
	}
}
public static Result top5() {
	JsonNode request = request().body().asJson();
	String category;
	try {
		category = request.get("category").textValue();
		
	}
	catch(Exception e) {
		e.printStackTrace();
		return ok(JsonNodeFactory.instance.objectNode()
				.put("error", "Parameters: category (text)"));
	}
	try(Connection conn = DB.getConnection()) {
		PreparedStatement stmt = conn.prepareStatement(EVENT_GET_SQL_UNRESTRICTED + " WHERE Tags.tag = ? ORDER BY Event.view_count DESC LIMIT 5");
		stmt.setString(1, category);
		ResultSet rs = stmt.executeQuery();
		return ok(buildEventResults(conn, rs, Application.getUserId(request)));
		
	}
	catch(SQLException e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
}

public static Result getEvent() {
	JsonNode request = request().body().asJson();
	long event_id;
	try {
		event_id = request.get("event_id").longValue();
	}
	catch(Exception e) {
		return ok(JsonNodeFactory.instance.objectNode().put("error", "usage: event_id (long)"));
	}
	long user_id;
	user_id = Application.getUserId(request);
	try(Connection conn = DB.getConnection()) {
		if(user_id != -1) {
			PreparedStatement stmt = conn.prepareStatement(EVENT_GET_SQL_UNRESTRICTED + " WHERE Event.id = ?");
			stmt.setLong(1, event_id);
			ResultSet rs = stmt.executeQuery();
			return ok(buildEventResults(conn, rs, Application.getUserId(request)).get(0));
		}
		else {
			PreparedStatement stmt = conn.prepareStatement(EVENT_GET_SQL_UNRESTRICTED + " WHERE Event.id = ?");
			stmt.setLong(1, event_id);
			ResultSet rs = stmt.executeQuery();
			return ok(buildEventResults(conn, rs, Application.getUserId(request)).get(0));
		}
	}
	catch(Exception e) {
		e.printStackTrace();
		return ok(JsonNodeFactory.instance.objectNode().put("error", e.getMessage()));
	}
	
}

public static Result getEventAttendees()
{
	JsonNode request  = request().body().asJson();
	// get all members for event.
	long event_id=request.get("event_id").longValue();

	try{
		Connection conn = DB.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT User.first_name, User.last_name from User INNER JOIN  Event_has_User ON User.id=Event_has_User.user_id WHERE Event_has_User.event_id = ? ");
		stmt.setLong(1, event_id);
		ResultSet rs = stmt.executeQuery();
		ArrayList<String> names = new ArrayList<String>();
		JSONObject obj = new JSONObject();
		JSONArray json_array = new JSONArray();
		while(rs.next())
		{
			String first = rs.getString(1);
			if(!first.equals("Scraper")){
			json_array.put(first +" "+ rs.getString(2));
			}
		}
		obj.put("names", json_array);
		return ok(obj.toString());
	}
	catch(Exception e)
	{

		return ok(e.toString());
	}
	


}


public static Result incrementViewCount()
{
	JsonNode request = request().body().asJson();
	int event_id = request.get("event_id").asInt();
	try{
		Connection conn = DB.getConnection();
		PreparedStatement stmt = conn.prepareStatement("UPDATE Event SET view_count = view_count +1 WHERE id = ?");
		stmt.setInt(1, event_id);
		stmt.executeUpdate();
		JSONObject json = new JSONObject();
		json.put("response", "done");
		return ok(json.toString());
	}
	catch(Exception e)
	{
		return ok(e.toString());
	}
}




}
