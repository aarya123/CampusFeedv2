package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class Group extends Controller {
    public static Result createGroup() {
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
    	String groupName;
    	long userId;
    	if(!request.has("group_name")) {
    		return badRequest(JsonNodeFactory.instance.objectNode().put("error", "usage: group_name"));
    	}
    	groupName = request.get("group_name").textValue();
    	userId = Application.getUserId(request);
    	Connection conn = null;
    	PreparedStatement stmt = null;
    	try {
    		conn = DB.getConnection();
    		conn.setAutoCommit(false);
    		//run sql
    		stmt = conn.prepareStatement("INSERT INTO `Group` (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
    		stmt.setString(1, groupName);
    		stmt.execute();
    		ResultSet rs = stmt.getGeneratedKeys();
    		if(rs.next()) {
    			//get group id
        		long groupId = rs.getLong(1);
        		stmt.close();
    			//add user who created group as admin
    			stmt = conn.prepareStatement("INSERT INTO Group_has_User (user_id, group_id, is_admin) VALUES (?, ?, 1)");
    			stmt.setLong(1, userId);
    			stmt.setLong(2, groupId);
    			stmt.execute();
    			conn.commit();
    			return ok(JsonNodeFactory.instance.objectNode().put("group_id", groupId));
    		}
    	}
    	catch(SQLException e) {
    		//rollbak on failure
    		try {
    			conn.rollback();
    		}
    		catch(SQLException ex) {
    			ex.printStackTrace();
    		}
    		e.printStackTrace();
    	}
    	finally {
    		//cleanup
    		try {
    			if(conn != null) {
    				conn.close();
    			}
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    		try {
    			if(stmt != null) {
    				stmt.close();
    			}
    		}
    		catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	return internalServerError();
    }
    
    public static Result joinGroup() {
    	//check auth
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
    	long groupId;
    	long userId;
    	if(!request.has("group_id") || !request.get("group_id").canConvertToInt()) {
    		return badRequest(JsonNodeFactory.instance.objectNode().put("error", "usage: group_id (int)"));
    	}
    	groupId = request.get("group_id").intValue();
    	userId = Application.getUserId(request);
    	try(Connection conn = DB.getConnection()) {
    		try(PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO Group_has_User (user_id, group_id) VALUES (?, ?)")) {
	    		stmt.setLong(1, userId);
	    		stmt.setLong(2, groupId);
	    		stmt.execute();
	    		return ok();
    		}
    	}
    	catch(SQLException e) {
    		e.printStackTrace();
    		return internalServerError();
    	}
    }
    
    private static final String IS_MEMBER_ADMIN_SQL = "SELECT is_admin FROM Group_has_User WHERE user_id = ?";
    private static final String LEAVE_GROUP_SQL = "DELETE FROM Group_has_User WHERE user_id = ?";
}
