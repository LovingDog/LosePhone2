package com.example.wanghanp.losephone.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;

public class SaveStateService extends Service {
    public SaveStateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int flag = Service.START_STICKY;
        showNotification();
        return super.onStartCommand(intent, flag, startId);
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopForeground(true);
    }
}
