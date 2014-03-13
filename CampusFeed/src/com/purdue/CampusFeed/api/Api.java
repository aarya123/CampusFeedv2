package com.purdue.CampusFeed.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;

/**
 * Make a static instance of this on app creation. Close it on shutdown
 * @author Matthew
 *
 */
public class Api implements Closeable {
	
	private static final String BASE_URL = "http://54.213.17.69:9000/";
	
	private Gson gson;

	public Api(Context context) {
		try {
           File httpCacheDir = new File(context.getCacheDir(), "http");
           long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
           HttpResponseCache.install(httpCacheDir, httpCacheSize);
		}
        catch (IOException e) {
           Log.i("Api", "HTTP response cache installation failed:" + e);
       }
		gson = GsonHelper.createCampusFeedGson();
	}
	
	private static class SearchEventRequest {
		public String query;
		public SearchEventRequest(String query) {
			this.query = query;
		}
	}
	
	public List<Event> search(String query) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "search_event").openConnection();
		conn.setRequestProperty("content-type", "application/json");
		conn.setDoOutput(true);
		OutputStream output = new BufferedOutputStream(conn.getOutputStream());
		InputStream input = new BufferedInputStream(conn.getInputStream());
		gson.toJson(new SearchEventRequest(query), new OutputStreamWriter(output));
		List<Event> response = gson.fromJson(new JsonReader(new InputStreamReader(input)), new TypeToken<List<Event>>() {}.getType());
		return response;
	}

	@Override
	public void close() throws IOException {
		HttpResponseCache cache = HttpResponseCache.getInstalled();
		if (cache != null) {
			cache.flush();
		}
	}
}
