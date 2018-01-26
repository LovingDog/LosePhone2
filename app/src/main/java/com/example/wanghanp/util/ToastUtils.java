package com.example.wanghanp.util;

import android.content.Context;
import android.widget.Toast;



public class ToastUtils {

    public static void showShortToast(CharSequence msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
