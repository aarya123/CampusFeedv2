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
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
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
	
	public <T> T getResponse(String endpoint, String json) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(json.length()));
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream output = new BufferedOutputStream(conn.getOutputStream());
			new OutputStreamWriter(output).append(json).close();
			InputStream input = new BufferedInputStream(conn.getInputStream());
			T response = gson.fromJson(new JsonReader(new InputStreamReader(input)), new TypeToken<T>() {}.getType());
			input.close();
			return response;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public interface Callback<T> {
		public void call(T data);
	}
	
	public List<Event> searchEvent(String query) {
		return getResponse("search_event", gson.toJson(new SearchEventRequest(query)));
	}
	
	public void asyncSearchEvent(final String query, final Callback<List<Event>> callback) {
		new AsyncTask<Void, Void, List<Event>>() {

			@Override
			protected List<Event> doInBackground(Void... arg0) {
				return searchEvent(query);
			}
			
			@Override
			protected void onPostExecute(List<Event> results) {
				callback.call(results);
			}
			
		}.execute();
	}

	@Override
	public void close() throws IOException {
		HttpResponseCache cache = HttpResponseCache.getInstalled();
		if (cache != null) {
			cache.flush();
		}
	}
}
