//package com.example.wanghanp.base.preference;
//
//import android.content.Context;
//import android.util.Log;
//
//
//
///**
// * Created by wanghanping on 2018/1/26.
// */
//
//public class SettingPreference extends BasePreference {
//    public static final String MODE_SETTING_SAFE = "setting_safe";
//    public static final String MODE_SETTING_LAT = "setting_latidude";
//    public static final String MODE_SETTING_LONG = "setting_longitude";
//    public static final String MODE_SETTING_LOCATION = "setting_location_name";
//    public static final String MODE_SETTING_LOCATION_CONTENT = "setting_location_content";
//    public static final String MODE_SETTING_REMIND = "setting_location_remind";
//
//    public SettingPreference(Context context, Preference which) {
//        super(context, Preference.APP_SETTING);
//    }
//
//    public void setSafeMode(boolean safe) {
//        editor = preferences.edit();
//        editor.putBoolean(MODE_SETTING_SAFE,safe);
//        editor.commit();
//    }
//
//    public void getSafeMode() {
//        preferences.getBoolean(MODE_SETTING_SAFE,false);
//    }
//
//    public void setLatPoint(LatLng latLng) {
//        Log.d("wanghp009", "setLatPoint: latLng.latitude = "+latLng.latitude+"&&latLng.longitude = " +latLng.longitude);
//        editor = preferences.edit();
//        editor.putString(MODE_SETTING_LAT, String.valueOf( latLng.latitude));
//        editor.putString(MODE_SETTING_LONG, String.valueOf( latLng.longitude));
//        editor.commit();
//    }
//
//    public LatLng getLat() {
//        return new LatLng(Double.parseDouble(preferences.getString(MODE_SETTING_LAT, "0")),
//                Double.parseDouble(preferences.getString(MODE_SETTING_LAT, "0")));
//    }
//
//    public void setLocationName(String name) {
//        editor = preferences.edit();
//        editor.putString(MODE_SETTING_LOCATION, name);
//        editor.commit();
//    }
//
//    public String getLocationName() {
//        return preferences.getString(MODE_SETTING_LOCATION,"");
//    }
//
//    public void setLocationContent(String name) {
//        editor = preferences.edit();
//        editor.putString(MODE_SETTING_LOCATION_CONTENT, name);
//        editor.commit();
//    }
//
//    public String getLocationContent() {
//        return preferences.getString(MODE_SETTING_LOCATION_CONTENT,"");
//    }
//
//    public void setMapRemind(boolean remind) {
//        editor = preferences.edit();
//        editor.putBoolean(MODE_SETTING_REMIND,remind);
//        editor.commit();
//    }
//
//    public boolean isRemindLocation() {
//        return preferences.getBoolean(MODE_SETTING_REMIND,false);
//    }
//}
