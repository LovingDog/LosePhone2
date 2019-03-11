package com.example.wanghanp.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class LiveService extends NotificationListenerService {

    public LiveService() {

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("alarm", "onNotificationPosted: ");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("alarm", "onNotificationRemoved: ");
    }
}