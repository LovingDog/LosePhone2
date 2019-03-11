package com.example.wanghanp.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.wanghanp.alarm.AlarmClock;
import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.alarm.ScreenBroadcastListener;
import com.example.wanghanp.alarm.ScreenManager;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.receiver.AlarmReciver;
import com.example.wanghanp.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LongRunningService extends Service {
    private AlarmClock mAlarmClock;
    private AlarmInfo mAlarmInfo;
    private final static int GRAY_SERVICE_ID = 1001;
    private ScreenBroadcastListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        final ScreenManager screenManager = ScreenManager.getInstance(LongRunningService.this);
        if (listener == null) {
            listener = new ScreenBroadcastListener(this);
            Log.d("alarm", "registerListener_screen: ++++++");
            listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
                @Override
                public void onScreenOn() {
                    Log.d("alarm", "onScreenOn: ++++++");
                    sendBroadcast(new Intent("finish"));
                }
                @Override
                public void onScreenOff() {
                    Log.d("alarm", "onScreenOff: ++++++");
                    screenManager.startActivity();
                }
            });
        }

        if (intent.hasExtra("bundle")) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle.containsKey("alarminfo")) {
                List<AlarmInfo> mAlarmInfoList = (List<AlarmInfo>) bundle.getSerializable("alarminfo");
                if (mAlarmInfoList == null || mAlarmInfoList.size() == 0) {
                    return super.onStartCommand(intent, flags, startId);
                }
                for (AlarmInfo alarmInfo1:mAlarmInfoList) {
                    if (alarmInfo1.getLocation() == null) {
                        mAlarmClock = new AlarmClock(this);
                        mAlarmClock.turnAlarm(alarmInfo1,null,true);
                    }
                }
                AlarmInfoDao alarmInfoDao = new AlarmInfoDao(this);
                for (AlarmInfo alarmInfo :
                        alarmInfoDao.getAllAlarmLocationInfo()) {
                    if (StringUtils.isReal(alarmInfo.getLocation())) {
                        startReceiver();
                        break;
                    }
                }
            } else if (bundle.containsKey("getid")){
                String getId = bundle.getString("getid");
                AlarmClock alarmClock=new AlarmClock(this);
                alarmClock.turnAlarm(null,getId,true);
                //        alarmClock.turnAlarm(null,getid,true);

            } else if (bundle.containsKey("location_alarminfo")) {
                Log.d("alarm", "service start: bundle = "+bundle);
                mAlarmInfo = (AlarmInfo)bundle.getSerializable("location_alarminfo");
                startReceiver();
            }
        }
        int flag = START_STICKY;
        return super.onStartCommand(intent,flag,startId);
    }

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.notification_warn))
                .setContentText(getResources().getString(R.string.notification_warn_message))
                .setSmallIcon(R.mipmap.person)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(0x111, notification);
    }

    private void startReceiver(){
        AlarmManager mAlamManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciver.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("location_alarminfo", mAlarmInfo);
        bundle.setClassLoader(AlarmInfo.class.getClassLoader());
        intent.putExtra("name",bundle);
//            intent.putExtras(bundle);
        intent.setAction("com.wanghp.RING_ALARM");
        //每个闹钟不同的pi

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1 * 50 * 1000; // 这是一小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
//        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);


        // pendingIntent 为发送广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else {
            mAlamManager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

}