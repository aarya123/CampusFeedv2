package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

public class ScraperHandler extends Controller {
	
	public static int SCRAPER_ID = 28;
	public static Result scrapedPage()
	{
		JsonNode request = request().body().asJson();
		
		// get all data
		String url = request.get("url").textValue();
		String url_decoded=null;
		try {
			 url_decoded = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		return internalServerError();
		}
		int event_id= EventManager.createFromScrapedPage(request);
		if(event_id==-1)
		{
			return internalServerError();
		}
		else
		{
			try(Connection conn = DB.getConnection()) {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO CampusFeed.Scraper (scrape_url,event_id) VALUES (?,?)");
				stmt.setString(1, url_decoded);
				stmt.setInt(2, event_id);
				
				stmt.executeUpdate();
				
			
				
				
			}
			catch(SQLException e) {
				e.printStackTrace();

				return internalServerError();
			}
			
			return ok("success");
		}
		
		
		
	}
	

}
