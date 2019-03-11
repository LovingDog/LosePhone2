package com.example.wanghanp.receiver;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.alarm.AlarmLockScreenActivity;
import com.example.wanghanp.baiduspeak.SpeakUtils;
import com.example.wanghanp.service.AlarmRingService;
import com.example.wanghanp.service.LongRunningService;
import com.example.wanghanp.util.PrefUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe on 2016/1/11.
 */
public class AlarmReciver  extends BroadcastReceiver{

    private int lazylevel;
    private String tag;
    private Context context;
    private int A;
    private int B;
    private int id;
    private AlarmManager alarmManager;
    private String getid;
    private String resid;
    private int[] dayOfWeek;
    private double longtitude;
    private double latitude;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SpeakUtils mSpeak;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context=context;
            Log.d("alarm","context = "+context);

            Log.d("alarm","收到广播");
            getid = intent.getStringExtra("getid");
            Log.d("alarm","getid = "+getid);
            Bundle bundle=intent.getBundleExtra("name");
            bundle.setClassLoader(AlarmInfo.class.getClassLoader());
            Log.d("alarm","alarminfo = "+bundle.getSerializable("alarminfo"));

            if (bundle.containsKey("location_alarminfo")) {
                Log.d("alarm", "onReceive: startLocation");
                startLocation();
                return;
            }
            AlarmInfo currentAlarm=(AlarmInfo)bundle.getSerializable("alarminfo");
            lazylevel=currentAlarm.getLazyLevel();
            tag=currentAlarm.getTag();
            dayOfWeek=currentAlarm.getDayOfWeek();
            resid=currentAlarm.getRingResId();
            id = intent.getIntExtra("alarmid", 0);
            //先进行判断今天该闹钟是否该响
            //需要的数据是 赖床级数 标签 铃声
            Log.d("alarm", dayOfWeek[0]+"dayofweek0");
            Log.d("alarm","cancel"+intent.getBooleanExtra("cancel",false));
            if(intent.getBooleanExtra("cancel",false)){
                cancelAlarm(intent);
                return;
            }
            if(dayOfWeek[0]==0){
                wakePhoneAndUnlock();
                ringAlarm("亲爱的汪先森，您定的"+currentAlarm.getHour()
                                +"时"+currentAlarm.getMinute()+"分的闹钟，已到点,请"+currentAlarm.getDesc());
                PrefUtils.putBoolean(context,getid,false);
            }else{
                Log.d("alarm","执行else"+ dayOfWeek.length);
                Calendar calendar= Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int currentDay=calendar.get(Calendar.DAY_OF_WEEK)-1;
                for(int i=0;i< dayOfWeek.length;i++){
                    Log.d("alarm", dayOfWeek[i]+";"+currentDay);
                    if(dayOfWeek[i]==7){
                        dayOfWeek[i]=0;
                    }
                    Log.d("alarm", "onReceive: currentDay= "+currentDay +"& dayOfWeek = "+dayOfWeek[i] + "tag = "+currentAlarm.getTag());
                    if(currentDay== dayOfWeek[i] && currentAlarm.getTag().equals("1")){
                        Log.d("alarm", dayOfWeek[i]+";"+currentDay+"&tag = "+currentAlarm.getTag());
                        wakePhoneAndUnlock();
                        ringAlarm("亲爱的汪先森，您定的闹钟"+currentAlarm.getHour()
                                +"时"+currentAlarm.getMinute()+"分的闹钟，已到点,"+currentAlarm.getDesc()+",未来两小时可能会下雨哦，记得带伞");
                    }
                }
                runAlarmAgain(intent, getid);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("alarm","onReceive_exception");
        }

    }

    private void cancelAlarm(Intent intent) {
        Log.d("alarm","取消闹钟");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi= PendingIntent.getBroadcast(context,id,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

    private void runAlarmAgain(Intent intent, String id) {

    //未完成
        Bundle bundle=new Bundle();
        bundle.putString("getid", getid);
        bundle.setClassLoader(AlarmInfo.class.getClassLoader());
        Intent intent1 = new Intent(context,LongRunningService.class);
        intent1.putExtra("bundle",bundle);
        context.startService(intent);
    }

    //点亮屏幕并解锁
    private void wakePhoneAndUnlock() {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "WakeLock");
        mWakelock.acquire();//唤醒屏幕
//......
        KeyguardManager mManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKeyguardLock = mManager.newKeyguardLock("Lock");
//让键盘锁失效
        mKeyguardLock.disableKeyguard();
        mWakelock.release();//释放
    }

    private void ringAlarm(String msg) {
//        if(PrefUtils.getBoolean(context, ConsUtils.SHOULD_WETHER_CLOSE,false)){
//            //如果用户关闭了天气 不再弹出Activity;
//        }else{
//            //打开天气提示
        Intent it=new Intent(context, AlarmLockScreenActivity.class);
        it.putExtra("msg",msg);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
//        }
//
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Date date=calendar.getTime();
        Log.d("alarm", "收到广播的时间" + date.toString());
        showAlarmDialog();
//        Log.d("alarm", "赖床指数" + lazylevel + "," + tag + ",");
    }
    //展示闹钟对话框
    private void showAlarmDialog() {
        final Intent service=new Intent(context, AlarmRingService.class);
        service.putExtra("resid", resid);
        context.startService(service);
    }

    private void showMissingPermissionDialog(String content) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this.context);
        dialogBuilder.setTitle("提醒");
        dialogBuilder.setMessage("has alarm come");
        dialogBuilder.setNegativeButton("等下再提醒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogBuilder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (CommonUtil.isWeixinAvilible(getActivity())) {
//                    CommonUtil.startWeChatAc(getActivity());
//                    mlocationClient.stopLocation();
//                    ((BaiduSpeakMainActivity)getActivity()).pause();
//                }
            }
        });
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    private void startLocation() {
        mlocationClient = new AMapLocationClient(context.getApplicationContext());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(mListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //设置定位参数
        mLocationOption.setNeedAddress(true);
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setOnceLocation(true);
        //mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mlocationClient) {
            mlocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        //            mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }
    }

    private AMapLocationListener mListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            longtitude = aMapLocation.getLongitude();
            latitude = aMapLocation.getLatitude();
            new CompareDistanceAsyncTask(latitude,longtitude).execute();
        }
    };

    class CompareDistanceAsyncTask extends AsyncTask<Void,Void,AlarmInfo> {

        private double latitude;
        private double longitude;
        AlarmInfoDao alarmInfoDao = new AlarmInfoDao(context);
        public CompareDistanceAsyncTask(double lat,double longi) {
            Log.d("alarm", "CompareDistanceAsyncTask: lat = "+lat +"long ="+longi);
            this.latitude = lat;
            this.longitude = longi;
        }

        @Override
        protected AlarmInfo doInBackground(Void... voids) {
            Calendar calendar= Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentDay=calendar.get(Calendar.DAY_OF_WEEK)-1;
            for (AlarmInfo location:
                    alarmInfoDao.getAllAlarmLocationInfo()) {

                dayOfWeek = location.getDayOfWeek();
                for(int i=0;i< dayOfWeek.length;i++){
                    if(dayOfWeek[i]==7){
                        dayOfWeek[i]=0;
                    }
                    if(currentDay== dayOfWeek[i] && location.getTag().equals("1")){
                        double compareLat = Double.parseDouble(location.getLat());
                        double compreLong = Double.parseDouble(location.getLon());
                        float homeDistance = AMapUtils.calculateLineDistance(new LatLng(compareLat,compreLong),new LatLng(latitude,longitude));
                        Log.d("alarm", "doInBackground: distance = "+homeDistance+"&compareLat = "+compareLat
                                +"&compreLong = "+compreLong+"&alarmInfoDao.getAllAlarmLocationInfo() = "+alarmInfoDao.getAllAlarmLocationInfo().size());
                        //homeDistance < 600 &&
                        if (homeDistance < 1000) {
                            location.setTag("0");
                            Log.d("alarm", "doInBackground: ##############ring ring ring");
                            return location;
//                            wakePhoneAndUnlock();
//                            ringAlarm();
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(AlarmInfo alarmInfo) {
            super.onPostExecute(alarmInfo);
            Log.d("alarm", "onPostExecute: alarmInfo = "+alarmInfo);
            if (alarmInfo != null) {
                alarmInfoDao.updateAlarm(alarmInfo.getId(),alarmInfo);
                wakePhoneAndUnlock();
                ringAlarm("亲爱的汪先森，您已到达"+alarmInfo.getLocation()+","+alarmInfo.getDesc());
            }
            if (mlocationClient != null) {
                mlocationClient.stopLocation();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("location_alarminfo", null);
            bundle.setClassLoader(AlarmInfo.class.getClassLoader());
            Intent intent1 = new Intent(context,LongRunningService.class);
            intent1.putExtra("bundle",bundle);
            context.startService(intent1);

        }
    }
}
