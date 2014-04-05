package com.purdue.CampusFeed.API;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Make a static instance of this on app creation. Close it on shutdown
 *
 * @author Matthew
 */
public class Api implements Closeable {

    private static final String BASE_URL = "http://54.213.17.69:9000/";

    private static Gson gson;

    private static Api instance;

    private Auth login;

    static {
        class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {
            @Override
            public Date deserialize(JsonElement jsonElement, Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
                return new Date(jsonElement.getAsLong());
            }

            @Override
            public JsonElement serialize(Date date, Type type,
                                         JsonSerializationContext context) {
                return new JsonPrimitive(date.getTime());
            }


        }
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
    }


    private Api(Context context) {
        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("Api", "HTTP response cache installation failed:" + e);
        }
    }

    public static Api getInstance(Context context) {
        if (instance == null) {
            instance = new Api(context);
        }
        return instance;
    }

    public <T> T getResponse(String method, String endpoint, String json) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + endpoint).openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(json.length()));
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            if (method.equals("POST")) {
                conn.setDoOutput(true);
                OutputStream output = new BufferedOutputStream(conn.getOutputStream());
                new OutputStreamWriter(output).append(json).close();
            }
            InputStream input = new BufferedInputStream(conn.getInputStream());
            T response = gson.fromJson(new JsonReader(new InputStreamReader(input)), new TypeToken<T>() {
            }.getType());
            input.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean login(String fb_user_id, String access_token) {
        class LoginRequest {
            public String fb_user_id;
            public String access_token;

            public LoginRequest(String fb_user_id, String access_token) {
                this.fb_user_id = fb_user_id;
                this.access_token = access_token;
            }
        }
        class LoginResponse {
            public String access_token;
        }
        LoginResponse resp = getResponse("POST", "login", gson.toJson(new LoginRequest(fb_user_id, access_token)));
        if (resp != null) {
            login = new Auth(fb_user_id, resp.access_token);
            return true;
        } else {
            return false;
        }

    }

    public List<Event> searchEvent(String query) {
        class SearchEventRequest {
            public String query;

            public SearchEventRequest(String query) {
                this.query = query;
            }
        }
        return getResponse("POST", "search_event", gson.toJson(new SearchEventRequest(query)));
    }
    
    public static class AdvSearchQuery {
    	private long start_date = 0;
    	private long end_date = Long.MAX_VALUE;
    	private String title = null;
    	private String desc = null;
    	
    	public void setStartDate(long startDate) {
    		start_date = startDate;
    	}
    	
    	public void setEndDate(long endDate) {
    		end_date = endDate;
    	}
    	
    	public void setTitle(String title) {
    		this.title = title;
    	}
    	
    	public void setDesc(String desc) {
    		this.desc = desc;
    	}
    	
    	public long getStartDate() {
    		return start_date;
    	}
    	
    	public long getEndDate() {
    		return end_date;
    	}
    	
    	public String getTitle() {
    		return title;
    	}
    	
    	public String getDesc() {
    		return desc;
    	}
    }
    
    public List<Event> advSearchEvent(AdvSearchQuery query) {
    	return getResponse("POST", "adv_search_event", gson.toJson(query));
    }

    public long createEvent(Event event) {
        if (login == null) {
            return -1;
        }
        class CreateEventRequest {
            public String desc;
            public String location;
            public String[] categories;
            public String title;
            public Auth auth;
            public int visibility;
            public long date_time;

            public CreateEventRequest(Event event) {
                desc = event.eventDescription;
                location = event.eventLocation;
                categories = event.categories;
                title = event.eventName;
                this.auth = Api.this.login;
                visibility = 1;
                try {
                    date_time = new SimpleDateFormat("M-d-yyyy k:m").parse(event.datetime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    date_time = 0;
                }

            }
        }
        class EventResponse {
            public long event_id;
        }
        EventResponse resp = getResponse("POST", "create_event", gson.toJson(new CreateEventRequest(event)));
        if (resp != null) {
            return resp.event_id;
        } else {
            return -1;
        }
    }

    public boolean updateEvent(Event event) {
        if (login == null) {
            return false;
        }
        class UpdateEventRequest {
            public Auth auth;
            public String title;
            public String desc;
            public String location;
            public long date_time;
            public long id;
            public int visibility;
            public String[] categories;

            public UpdateEventRequest(Event event) {
                this.auth = login;
                this.title = event.eventDescription;
                this.desc = event.eventDescription;
                this.location = event.eventLocation;
                try {
                    this.date_time = new SimpleDateFormat("M-d-yyyy k:m").parse(event.datetime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    this.date_time = 0;
                }
                ;
                this.id = event.id;
                this.visibility = 1;
                this.categories = event.categories;
            }
        }
        class UpdateResponse {
            String ok;
        }
        UpdateResponse resp = getResponse("POST", "update_event", gson.toJson(new Object()));
        return resp != null;
    }

    @Override
    public void close() throws IOException {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }
}
