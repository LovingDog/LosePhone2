package com.example.wanghanp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;

import java.util.List;

/**
 * Created by wanghanping on 2018/1/22.
 */

public class CommonUtil {
    private PowerManager.WakeLock mWakeLock;
    public static final int TYPE_Normal = 1;
    public static final int TYPE_Progress = 2;
    public static final int TYPE_BigText = 3;
    public static final int TYPE_Inbox = 4;
    public static final int TYPE_BigPicture = 5;
    public static final int TYPE_Hangup = 6;
    public static final int TYPE_Media = 7;
    public static final int TYPE_Customer = 8;

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void startWeChatAc(Context context) {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);

        context.getApplicationContext().startActivity(intent);
    }

    private void acquireWakeLock(Context context ) {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "myService");
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    public static void simpleNotify(Context context,String Ticker,String Title,String content,String subTitle ){
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        NotificationManager manger = (NotificationManager) context.getApplicationContext().getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Ticker是状态栏显示的提示
        builder.setTicker(Ticker);
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle(Title);
        //第二行内容 通常是通知正文
        builder.setContentText("提醒内容:"+content);
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        builder.setSubText(subTitle);
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        //number设计用来显示同种通知的数量和ContentInfo的位置一样，如果设置了ContentInfo则number会被隐藏
        builder.setNumber(2);
        //可以点击通知栏的删除按钮删除
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.mipmap.person);
        //下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_menu_send));
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context,1,intent,0);
        //点击跳转的intent
        builder.setContentIntent(pIntent);
        //通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        manger.notify(TYPE_Normal,notification);
    }
}
