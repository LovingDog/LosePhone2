package com.example.wanghanp.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class MobileInfo {
    private static MobileInfo instance;

    private MobileInfo(Activity context) {
        this.context = context;
        initData();
    };

    public static MobileInfo getInstance(Activity context) {
        if (instance == null)
            instance = new MobileInfo(context);
        return instance;
    }

    private Activity context;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private DisplayMetrics dm;

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    private void initData() {
        dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }

}
