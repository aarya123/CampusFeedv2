package com.purdue.CampusFeed.AsyncTasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.purdue.CampusFeed.Activities.MainActivity;

public class Login extends AsyncTask<String,Void,String>{



    @Override
    protected String doInBackground(String... in) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost request = new HttpPost("http://54.213.17.69:9000/login");


            JSONObject auth = new JSONObject();
            auth.put("access_token",MainActivity.facebook_accessToken);
            auth.put("fb_user_id",MainActivity.facebook_userID);

            Log.d("campus",auth.toString());

            StringEntity params =new StringEntity(auth.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response1= httpClient.execute(request);
            // get response
            HttpEntity res = response1.getEntity();

            JSONObject r = new JSONObject( EntityUtils.toString(res));
            MainActivity.SERVER_LONG_TOKEN = r.getString("access_token");
            Log.d("MAYANK",MainActivity.SERVER_LONG_TOKEN);
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
            Log.d("MAYANK", ex.toString());
            ex.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return MainActivity.SERVER_LONG_TOKEN;
    }

    @Override
    protected void onPostExecute(String result) {

        // might want to change "executed" for the returned string passed
        // into onPostExecute() but that is upto you
    }

}