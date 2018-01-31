package com.example.wanghanp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wanghanp.db.modle.LocationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghanping on 2018/1/29.
 */

public class LocationController {

    private final Context mContext;
    private final SQLiteDatabase database;

    private static final String TAG = "DBMusicocoController";

    public static final String DATABASE = "location.db";

    public static final String TABLE_LOCATION = "location";

    public static final String LOCATION_ID = "id";
    public static final String LOCATION_NAME = "location_name";
    public static final String LOCATION_CONTENT = "location_content";
    public static final String LOCATION_TYPE = "location_type";
    public static final String LOCATION_PATH = "location_path";
    public static final String LOCATION_LATITUDE = "location_latidude";
    public static final String LOCATION_LONGITUDE = "location_longitude";

    public static final String LOCATION_TAG1= "location_tag1";
    public static final String LOCATION_TAG2= "location_tag2";
    public static final String LOCATION_TAG3= "location_tag3";



    static void createTable(SQLiteDatabase sqlite) {
            String sql = "create table if not exists "+ TABLE_LOCATION +"(" +
                    LOCATION_ID +" integer primary key autoincrement,"+
                    LOCATION_NAME +" text unique, "+
                    LOCATION_CONTENT +" char(30), "+
                    LOCATION_TYPE +" integer, "+
                    LOCATION_PATH +" char(35), "+
                    LOCATION_LATITUDE+" REAL, "+
                    LOCATION_LONGITUDE+" REAL, "+
                    LOCATION_TAG1 + " text, "+
                    LOCATION_TAG2 + " text, "+
                    LOCATION_TAG3 + " text)";
        sqlite.execSQL(sql);


    }

    public LocationController(Context context) {
        this.mContext = context;
        LocationHelper mLocationhelper = new LocationHelper(mContext, DATABASE);
        this.database = mLocationhelper.getWritableDatabase();
    }

    public List<LocationInfo> getLocationInfos() {
        String sql = "select * from "+TABLE_LOCATION;
        Cursor cursor = this.database.rawQuery(sql,null);
        List<LocationInfo> locationList = new ArrayList<>();
        while (cursor.moveToNext()) {
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.id = cursor.getInt(cursor.getColumnIndex(LOCATION_ID));
            locationInfo.locationName = cursor.getString(cursor.getColumnIndex(LOCATION_NAME));
            locationInfo.content = cursor.getString(cursor.getColumnIndex(LOCATION_CONTENT));
            locationInfo.type = cursor.getInt(cursor.getColumnIndex(LOCATION_TYPE));
            locationInfo.path = cursor.getString(cursor.getColumnIndex(LOCATION_PATH));
            locationInfo.latitude = cursor.getDouble(cursor.getColumnIndex(LOCATION_LATITUDE));
            locationInfo.longitude = cursor.getDouble(cursor.getColumnIndex(LOCATION_LONGITUDE));
            locationInfo.tag1 = cursor.getString(cursor.getColumnIndex(LOCATION_TAG1));
            locationList.add(locationInfo);
        }

        if (cursor != null) {
            cursor.close();
        }
        return locationList;
    }

    public void insertLocationRemind(LocationInfo locationInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_NAME,locationInfo.locationName);
        contentValues.put(LOCATION_CONTENT,locationInfo.content);
        contentValues.put(LOCATION_TYPE,locationInfo.type);
        contentValues.put(LOCATION_PATH,locationInfo.path);
        contentValues.put(LOCATION_TAG1,locationInfo.tag1);
        contentValues.put(LOCATION_LATITUDE,locationInfo.latitude);
        contentValues.put(LOCATION_LONGITUDE,locationInfo.longitude);
        this.database.insert(TABLE_LOCATION,null,contentValues);
    }

    public void deleteLocationById(int id) {
        String where = LOCATION_ID+" = ?";
        String[] arg = new String[] {String.valueOf(id)};
        int a = this.database.delete(TABLE_LOCATION,where,arg);
    }

    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }
}
