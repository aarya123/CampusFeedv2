package com.purdue.CampusFeed.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.Activities.MainActivity;

public class Login extends AsyncTask<String, Void, Integer> {

    Context c;

    public Login(Context c) {
        this.c = c;
    }

    protected Integer doInBackground(String... in) {
        Api.getInstance(c).login(in[0], in[1]);
        return 0;
    }
}