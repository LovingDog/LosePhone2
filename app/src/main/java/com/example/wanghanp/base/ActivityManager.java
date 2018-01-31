package com.example.wanghanp.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.wanghanp.losephone.MainActivity;

import java.util.HashMap;
import java.util.Map;


public class ActivityManager {

    public static final String SONG_DETAIL_PATH = "song_detail_path";
    public static final String SONG_DETAIL_START_FROM_PLAY_ACTIVITY = "song_detail_start_from_play_activity";


    public static final String SHEET_MODIFY_ID = "sheet_modify_id";
    public static final String SHEET_SEARCH_ID = "sheet_search_id";

    public static final String SHEET_DETAIL_ID = "sheet_detail_id";
    public static final String SHEET_DETAIL_LOCATION_AT = "sheet_detail_location_at";

    public static final String WEB_URL = "web_url";

    private static ActivityManager mInstance;

    private final Map<String, Activity> activitys = new HashMap<>();

    private ActivityManager() {
    }

    public Activity getActivity(String name) {
        return activitys.get(name);
    }

    public void addActivity(Activity activity) {
        activitys.put(activity.getClass().getName(), activity);
    }

    public void removeActivity(Activity activity) {
        Activity item = activitys.remove(activity.getClass().getName());
        item = null;
    }

    public static ActivityManager getInstance() {
        if (mInstance == null) {
            mInstance = new ActivityManager();
        }
        return mInstance;
    }

    public static void startMainActivity(Context context){
        context.startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
    }

    public void startQQChartPanel(Context context, String qqId) {
        try {
            //可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqId;//uin是发送过去的qq号码
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
//            String msg = context.getString(R.string.error_check_qq_install);
//            ToastUtils.showShortToast(msg, context);
        }
    }

    public void startSystemPhoneCallPanel(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void startSystemEmailPanel(Context context, String email) {
        Uri uri = Uri.parse("mailto:" + email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, ""); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
//        context.startActivity(Intent.createChooser(intent, context.getString(R.string.info_choice_email_app)));
    }
}
