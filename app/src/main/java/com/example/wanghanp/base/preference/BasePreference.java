package com.example.wanghanp.base.preference;

import android.content.Context;
import android.content.SharedPreferences;


public class BasePreference {

    protected SharedPreferences.Editor editor;
    protected final SharedPreferences preferences;
    protected final Context context;

    public enum Preference {
       APP_SETTING,
        APP_PLAYING
    }

    public BasePreference(Context context, Preference which) {
        this.context = context;
        String name = which.toString().toLowerCase();
        this.preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
