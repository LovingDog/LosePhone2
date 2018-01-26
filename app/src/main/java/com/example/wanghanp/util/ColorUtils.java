package com.example.wanghanp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;

import com.example.wanghanp.losephone.R;


public class ColorUtils {

    /**
     * 获得一个随机的颜色
     */
    public static int getRandomColor() {
        int r = (int) (Math.random() * 255); //产生一个255以内的整数
        int g = (int) (Math.random() * 255); //产生一个255以内的整数
        int b = (int) (Math.random() * 255); //产生一个255以内的整数
        return Color.rgb(r, g, b);
    }

    /**
     * 获得一个比较暗的随机颜色
     */
    public static int getRandomBrunetColor() {
        int r = (int) (Math.random() * 100); //产生一个100以内的整数
        int g = (int) (Math.random() * 100);
        int b = (int) (Math.random() * 100);
        return Color.rgb(r, g, b);
    }

    /**
     * 0 状态栏背景色<br>
     * 1 标题栏背景色<br>
     * 2 控件首选色<br>
     * 3 主背景色<br>
     * 4 辅背景色<br>
     * 5 主字体色<br>
     * 6 辅字体色<br>
     * 7 底部导航背景色<br>
     * 8 标题栏主字体色<br>
     * 9 标题栏辅字体色<br>
     */
    public static int[] get10WhiteThemeColors(Context context) {
        int[] colors = new int[10];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.theme_white_primary);
            colors[1] = context.getColor(R.color.theme_white_primary_dark);
            colors[2] = context.getColor(R.color.theme_white_accent);
            colors[3] = context.getColor(R.color.theme_white_main_bg);
            colors[4] = context.getColor(R.color.theme_white_vic_bg);
            colors[5] = context.getColor(R.color.theme_white_main_text);
            colors[6] = context.getColor(R.color.theme_white_vic_text);
            colors[7] = context.getColor(R.color.theme_white_nav);
            colors[8] = context.getColor(R.color.theme_white_toolbar_main_text);
            colors[9] = context.getColor(R.color.theme_white_toolbar_vic_text);
        } else {
            colors[0] = context.getResources().getColor(R.color.theme_white_primary);
            colors[1] = context.getResources().getColor(R.color.theme_white_primary_dark);
            colors[2] = context.getResources().getColor(R.color.theme_white_accent);
            colors[3] = context.getResources().getColor(R.color.theme_white_main_bg);
            colors[4] = context.getResources().getColor(R.color.theme_white_vic_bg);
            colors[5] = context.getResources().getColor(R.color.theme_white_main_text);
            colors[6] = context.getResources().getColor(R.color.theme_white_vic_text);
            colors[7] = context.getResources().getColor(R.color.theme_white_nav);
            colors[8] = context.getResources().getColor(R.color.theme_white_toolbar_main_text);
            colors[9] = context.getResources().getColor(R.color.theme_white_toolbar_vic_text);
        }

        return colors;
    }

    /**
     * 0 状态栏背景色<br>
     * 1 标题栏背景色<br>
     * 2 控件首选色<br>
     * 3 主背景色<br>
     * 4 辅背景色<br>
     * 5 主字体色<br>
     * 6 辅字体色<br>
     * 7 底部导航背景色<br>
     * 8 标题栏主字体色<br>
     * 9 标题栏辅字体色<br>
     */
    public static int[] get10DarkThemeColors(Context context) {
        int[] colors = new int[10];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.theme_dark_primary);
            colors[1] = context.getColor(R.color.theme_dark_primary_dark);
            colors[2] = context.getColor(R.color.theme_dark_accent);
            colors[3] = context.getColor(R.color.theme_dark_main_bg);
            colors[4] = context.getColor(R.color.theme_dark_vic_bg);
            colors[5] = context.getColor(R.color.theme_dark_main_text);
            colors[6] = context.getColor(R.color.theme_dark_vic_text);
            colors[7] = context.getColor(R.color.theme_dark_nav);
            colors[8] = context.getColor(R.color.theme_dark_toolbar_main_text);
            colors[9] = context.getColor(R.color.theme_dark_toolbar_vic_text);
        } else {
            colors[0] = context.getResources().getColor(R.color.theme_dark_primary);
            colors[1] = context.getResources().getColor(R.color.theme_dark_primary_dark);
            colors[2] = context.getResources().getColor(R.color.theme_dark_accent);
            colors[3] = context.getResources().getColor(R.color.theme_dark_main_bg);
            colors[4] = context.getResources().getColor(R.color.theme_dark_vic_bg);
            colors[5] = context.getResources().getColor(R.color.theme_dark_main_text);
            colors[6] = context.getResources().getColor(R.color.theme_dark_vic_text);
            colors[7] = context.getResources().getColor(R.color.theme_dark_nav);
            colors[8] = context.getResources().getColor(R.color.theme_dark_toolbar_main_text);
            colors[9] = context.getResources().getColor(R.color.theme_dark_toolbar_vic_text);
        }

        return colors;
    }



    /**
     * 0 主字体颜色
     * 1 辅字体颜色
     */
    public static int[] get2WhiteThemeTextColor(Context context) {
        int[] colors = new int[2];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.theme_white_main_text); //主字体色
            colors[1] = context.getColor(R.color.theme_white_vic_text); // 辅字体色
        } else {
            colors[0] = context.getResources().getColor(R.color.theme_white_main_text);
            colors[1] = context.getResources().getColor(R.color.theme_white_vic_text);
        }

        return colors;
    }



    /**
     * 0 主字体颜色
     * 1 辅字体颜色
     */
    public static int[] get2DarkThemeTextColor(Context context) {
        int[] colors = new int[2];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.theme_dark_main_text); //主字体色
            colors[1] = context.getColor(R.color.theme_dark_vic_text); // 辅字体色
        } else {
            colors[0] = context.getResources().getColor(R.color.theme_dark_main_text);
            colors[1] = context.getResources().getColor(R.color.theme_dark_vic_text);
        }
        return colors;
    }

    public static int[] get2ToolbarTextColors(Context context) {
        int[] colors = new int[2];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.white_d);
            colors[1] = context.getColor(R.color.white_d_d);
        } else {
            colors[0] = context.getResources().getColor(R.color.white_d);
            colors[1] = context.getResources().getColor(R.color.white_d_d);
        }

        return colors;
    }

    public static int[] get2ColorWhiteThemeForPlayOptions(Context context) {
        int[] colors = new int[2];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.dark_l_l_l_l);
            colors[1] = context.getColor(R.color.white_d_d_d);
        } else {
            colors[0] = context.getResources().getColor(R.color.dark_l_l_l_l);
            colors[1] = context.getResources().getColor(R.color.white_d_d_d);
        }
        return colors;
    }

    public static int[] get2ColorDarkThemeForPlayOptions(Context context) {

        int[] colors = new int[2];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colors[0] = context.getColor(R.color.white_d_d);
            colors[1] = context.getColor(R.color.white_d_d_d);
        } else {
            colors[0] = context.getResources().getColor(R.color.white_d_d);
            colors[1] = context.getResources().getColor(R.color.white_d_d_d);
        }
        return colors;
    }

    /**
     * r g b >= 160 时返回 true
     */
    public static boolean isBrightSeriesColor(int color) {

        double d = android.support.v4.graphics.ColorUtils.calculateLuminance(color);
        if (d - 0.400 > 0.000001) {
            return true;
        } else {
            return false;
        }
    }

}
