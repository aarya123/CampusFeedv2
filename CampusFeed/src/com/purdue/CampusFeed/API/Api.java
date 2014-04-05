package com.purdue.CampusFeed.API;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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

    public Object getResponse(String method, String endpoint, String json, Type type) {
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
            Scanner in = new Scanner(input).useDelimiter("\\A");
            String raw = in.next();
            Object response = gson.fromJson(raw, type);
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
        LoginResponse resp = (LoginResponse) getResponse("POST", "login", gson.toJson(new LoginRequest(fb_user_id, access_token)), LoginResponse.class);
        if (resp != null) {
            login = new Auth(fb_user_id, resp.access_token);
            return true;
        } else {
            return false;
        }

    }

    public List<Event> advSearchEvent(AdvSearchQuery query) {
    	query.setAuth(login);
        return (List<Event>) getResponse("POST", "adv_search_event", gson.toJson(query), new TypeToken<List<Event>>() {
        }.getType());
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
                desc = event.description;
                location = event.location;
                categories = event.categories;
                title = event.name;
                this.auth = Api.this.login;
                visibility = 1;
                date_time = event.getDatetimeLong();
            }
        }
        class EventResponse {
            public long event_id;
        }
        EventResponse resp = (EventResponse) getResponse("POST", "create_event", gson.toJson(new CreateEventRequest(event)), EventResponse.class);
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
                this.title = event.description;
                this.desc = event.description;
                this.location = event.location;
                this.date_time = event.time;
                this.id = event.id;
                this.visibility = 1;
                this.categories = event.categories;
            }
        }
        class UpdateResponse {
            String ok;
        }
        UpdateResponse resp = (UpdateResponse) getResponse("POST", "update_event", gson.toJson(new Object()), UpdateResponse.class);
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
