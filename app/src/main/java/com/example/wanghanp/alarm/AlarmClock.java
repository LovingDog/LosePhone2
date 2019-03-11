package com.example.wanghanp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.example.wanghanp.receiver.AlarmReciver;
import com.example.wanghanp.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe on 2016/1/13.
 */
public class AlarmClock {
    private AlarmInfo alarmInfo;
    private Context context;
    private final AlarmInfoDao dao;

    public AlarmClock(Context context) {
        this.context = context;
        dao = new AlarmInfoDao(context);
    }
    public void turnAlarm(AlarmInfo alarmInfo, String AlarmID, Boolean isOn){
        Log.d("alarm","start_turnAlarm");
        try {
            if(alarmInfo==null){
                Log.d("alarm","传入AlarmInfo不为空");
                alarmInfo=dao.findById(AlarmID);
            }
            Log.d("alarm","传入AlarmInfo.ring = "+alarmInfo.getRing()+"&&location= "+alarmInfo.getLocation());
            this.alarmInfo=alarmInfo;
            String location = alarmInfo.getLocation();

            AlarmManager mAlamManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int id=dao.getOnlyId(alarmInfo);
            Intent intent = new Intent(context, AlarmReciver.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("alarminfo", alarmInfo);
            bundle.setClassLoader(AlarmInfo.class.getClassLoader());
            intent.putExtra("name",bundle);
//            intent.putExtras(bundle);
            intent.setAction("com.wanghp.RING_ALARM");
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra("alarmid", id);
            intent.putExtra("cancel",false);
            intent.putExtra("getid",alarmInfo.getId());
            Log.d("alarm", "id" + id);
            //每个闹钟不同的pi
            PendingIntent pi= PendingIntent.getBroadcast(context,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if(isOn){
                startAlarm(mAlamManager,pi);
            }else{
                cancelAlarm(intent);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("alarm","start_exception");
        }
    }

    private void cancelAlarm(Intent intent) {
        Log.d("alarm","取消闹钟");
        intent.putExtra("cancel",true);
        context.sendBroadcast(intent);
    }

    public void startAlarm(AlarmManager mAlamManager, PendingIntent pi){
        Log.d("alarm","启动闹钟");
        Calendar c= Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,alarmInfo.getHour());
        c.set(Calendar.MINUTE,alarmInfo.getMinute());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
          Log.d("alarm", "当前系统版本" + Build.VERSION.SDK_INT);
        if(c.getTimeInMillis()< System.currentTimeMillis()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlamManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            } else {
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }
        }else{
            Log.d("alarm","执行定时任务");
            Date date=c.getTime();
            Log.d("alarm","定时的时间是"+date.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlamManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            } else {
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }
        }

    }


}
