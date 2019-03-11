package com.example.wanghanp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wanghanp.alarm.AlarmClock;
import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.service.LongRunningService;
import com.example.wanghanp.util.PrefUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Joe on 2016/1/23.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm","Boot收到广播");
        checkAndStartAlarm(context);
    }

    //开机时检查是否有闹钟需要开启
    private void checkAndStartAlarm(Context context) {
        Log.d("alarm","开始检查是否有闹钟");
        AlarmInfoDao dao=new AlarmInfoDao(context);
        ArrayList<AlarmInfo> list= (ArrayList<AlarmInfo>) dao.getAllInfo();
        AlarmClock clock=new AlarmClock(context);
//        for (AlarmInfo alarmInfo:list) {
//            if(PrefUtils.getBoolean(context, alarmInfo.getId(), true)){
//                Log.d("alarm","有闹钟，开启");
//                clock.turnAlarm(alarmInfo,null,true);
//            }
//
//        }
        Bundle bundle=new Bundle();
        bundle.putSerializable("alarminfo", (Serializable) list);
        bundle.setClassLoader(AlarmInfo.class.getClassLoader());
        Intent intent = new Intent(context,LongRunningService.class);
        intent.putExtra("bundle",bundle);
        context.startService(intent);
    }
}
