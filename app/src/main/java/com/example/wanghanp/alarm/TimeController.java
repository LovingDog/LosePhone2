package com.example.wanghanp.alarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.wanghanp.alarm.AlarmLockScreenActivity.MSG_UPDATE_UI;


public class TimeController {

    private final MyRunnable mTask;
    private WeakReference<Context> mContext;
    ShowViewContract.TopView mTopView;
    private SimpleDateFormat mFormatter;
    private String mTime;
    private String mDate;
    private String mWay;
    private int mCount = 1;
    private int mTimeCount = -1;
    private String mSecond;


    public TimeController(Context context, ShowViewContract.TopView topView) {
        this.mContext = new WeakReference<>(context);
        mTopView = topView;
        mFormatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        mTask = new MyRunnable(context);
    }

    public void init() {
        mHandler.removeCallbacks(mTask);
        mHandler.post(mTask);
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_UI:
                    if (mContext.get() != null && mTopView != null) {
                        if (mTopView.getTimeTextView() != null) {
                            mTopView.getTimeTextView().setText(mTime);
                        }
                        if (mTopView.getSecondTextView() != null) {
                            mTopView.getSecondTextView().setText(mSecond);
                        }
                        if (mTopView.getDateTextView() != null) {
                            mTopView.getDateTextView().setText(mDate + "周" + mWay);
                        }
                    }
                    break;
            }
        }
    };

    class MyRunnable implements Runnable {

        private WeakReference<Context> runnableContext;

        public MyRunnable(Context context) {
            runnableContext = new WeakReference<>(context);
        }

        @Override
        public void run() {
            if (runnableContext.get() == null && mTopView != null) {
                return;
            }
            mHandler.postDelayed(mTask, 1000);
//            if (mTimeCount == 60 || mTimeCount == 0) {
                initTime();
                mHandler.removeMessages(MSG_UPDATE_UI);
                mHandler.sendEmptyMessage(MSG_UPDATE_UI);
//            }

//            if (mCount > 0) {
//                mCount ++;
//                if (mCount == 5) {
//                    mTopView.refreshTimers(mCount);
//                    mCount = 0;
//                }
//            }
        }
    };

    public void onDestroy() {
        mHandler.removeCallbacks(mTask);
        mHandler.removeCallbacksAndMessages(null);
//        RefWatcher refWatcher = MyApplication.getRefWatcher(mContext.get());//1
//        refWatcher.watch(this);
    }

    private void initTime() {//耗电，后续改成每次+1秒
        Date curDate = new Date(System.currentTimeMillis());
        String str = TimeUtil.getSecond(System.currentTimeMillis());
        String date = mFormatter.format(curDate);
        mTime = str.substring(0, str.lastIndexOf(":"));
        mSecond = str.substring(str.lastIndexOf(":"));
        mDate = date.substring(5, date.length() - 6);
        mWay = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="日";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
    }
}
