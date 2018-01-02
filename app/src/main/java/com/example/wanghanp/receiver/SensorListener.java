package com.example.wanghanp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by wanghanping on 2017/12/27.
 */

public class SensorListener {
    private PowerConnectionReceiver mPowerConnectionReceiver;
    private PowerConnectionListener mPowerConnectionlisener;
    private Context mContext;
    private IntentFilter mIntentFilter;

    public SensorListener (Context context , PowerConnectionListener powerConnectionlisener) {
        this.mContext = context;
        this.mPowerConnectionlisener = powerConnectionlisener;
        mPowerConnectionReceiver = new PowerConnectionReceiver();
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra("status", -1);
//            BatteryManager.BATTERY_STATUS_CHARGING
                Log.d("wanghp007", "batteryStatus == " + status);
                if (mPowerConnectionlisener != null) {
                    mPowerConnectionlisener.call(status);
                }
            }
        }
    }

    public interface PowerConnectionListener{
        void call(int powerStatus);
    }

    public void registerPowerConnection() {
        mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.mContext.registerReceiver(mPowerConnectionReceiver, mIntentFilter);
    }

    public void unRegisterPowerConnection() {
        if (mPowerConnectionlisener != null) {
            this.mContext.unregisterReceiver(mPowerConnectionReceiver);
        }
    }
}
