package com.example.wanghanp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wanghanping on 2018/1/29.
 */

public class LocationHelper extends SQLiteOpenHelper {
    public LocationHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        initLocationDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void initLocationDb(SQLiteDatabase db) {
        LocationController.createTable(db);
    }
}
