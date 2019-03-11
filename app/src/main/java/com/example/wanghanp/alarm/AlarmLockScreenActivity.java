package com.example.wanghanp.alarm;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wanghanp.baiduspeak.SpeakUtils;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.service.AlarmRingService;
import com.example.wanghanp.util.DensityUtil;
import com.example.wanghanp.util.StringUtils;

public class AlarmLockScreenActivity extends Activity implements ShowViewContract.TopView {
    public static final int MSG_UPDATE_UI = 1002;

    public static final int mTopTextColor = Color.parseColor("#CCCCCC");
    public static final int mCenterColor = Color.parseColor("#0c1415");
    private LinearLayout mTopLinealayout;
    private TextView mTopTimeText;
    private TextView mDateText;
    private RelativeLayout relativeLayout;
//    private InputMessageDialog inputMessageDialog;
    private TimeController mTimerController;
    private TextView mTitleText;
    private FrameLayout mContainer;
    private ImageView mBImageView;
    private RadiumTextView mLabelButton;
    private LinearLayout mainLayout;
    private LinearLayout.LayoutParams lineLayoutParams;
    private TextView mSubTitleText;
    private View mLineView;
    private LockView mLockView;
    private TextView mLockTextView;
    private FrameLayout mFrameLayout;
    private AlarmLockScreenActivity mContext;
    private int mTopHeight;
    private int screenWidth;
    private int screenHeight;
    private TextView mSecondText;
    private LinearLayout mRelativeLyout;
    private LinearLayout mDateLayout;
    private TextView mTemp;
    private TextView mText;
    private SpeakUtils mSpeak;
    private String mSpeakMsg;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpeak = new SpeakUtils(this);
        if (getIntent().hasExtra("msg")) {
            mSpeakMsg = getIntent().getStringExtra("msg");
        }
        this.mContext  = AlarmLockScreenActivity.this;
        mTopHeight = DensityUtil.dip2px(mContext, 180);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        final Window win = mContext.getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext.requestWindowFeature(Window.FEATURE_NO_TITLE);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;
        initView();
        initController();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSpeak != null && StringUtils.isReal(mSpeakMsg)) {
                    mSpeak.speak(mSpeakMsg);
                }
            }
        },1000);
    }

    /**
     * 初始化布局
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        RelativeLayout.LayoutParams rel_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(rel_params);
        relativeLayout.setBackgroundColor(mCenterColor);
        relativeLayout.setBackground(getResources().getDrawable(R.mipmap.player_bg));
        initTopView();
        iniFragmentlayout();
        setContentView(relativeLayout);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)//必须4.2机型以上
    private void iniFragmentlayout(){
        mFrameLayout = new FrameLayout(mContext);
        int id = View.generateViewId();
        mFrameLayout.setId(id);
        RelativeLayout.LayoutParams frameLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mFrameLayout.setLayoutParams(frameLayoutParams);
        relativeLayout.addView(mFrameLayout);
    }

    private void initController() {
        mTimerController = new TimeController(mContext, this);
        mTimerController.init();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTopView() {
        int lockWidth = DensityUtil.dip2px(mContext, 45);
        mTopLinealayout = new LinearLayout(mContext);
        mTopLinealayout.setPadding(DensityUtil.dip2px(mContext, 5), DensityUtil.dip2px(mContext, 10), 0, 0);
        mTopLinealayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mTopHeight));
        mTopLinealayout.setGravity(Gravity.CENTER);
        mTopLinealayout.setOrientation(LinearLayout.VERTICAL);

        mRelativeLyout = new LinearLayout(mContext);
        mRelativeLyout.setOrientation(LinearLayout.HORIZONTAL);
        mRelativeLyout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTopTimeText = new TextView(mContext);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mTopTimeText.setLayoutParams(l);
        mTopTimeText.setForegroundGravity(Gravity.CENTER);
        mTopTimeText.setMaxLines(1);
        mTopTimeText.setTextColor(Color.WHITE);
        mTopTimeText.setTextSize(DensityUtil.sp2px(mContext, 45));

        mSecondText = new TextView(mContext);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        ll.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mSecondText.setLayoutParams(ll);
        mSecondText.setTextColor(Color.WHITE);
        mSecondText.setTextSize(DensityUtil.sp2px(mContext, 35));
        mSecondText.setText(":");

        mDateLayout = new LinearLayout(mContext);
        mDateLayout.setOrientation(LinearLayout.HORIZONTAL);
        mDateLayout.setGravity(Gravity.CENTER_VERTICAL);
        mDateLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDateText = new TextView(mContext);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(DensityUtil.dip2px(mContext, 10), 0, 0, 0);
        mDateText.setLayoutParams(textLayoutParams);
        mDateText.setTextColor(Color.WHITE);
        mDateText.setTextSize(DensityUtil.sp2px(mContext, 12));

        ImageView imageView = new ImageView(mContext); //天气图标
        LinearLayout.LayoutParams iv = new LinearLayout.LayoutParams(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 15));
        iv.setMargins(DensityUtil.sp2px(mContext, 20),0,0,0);
        imageView.setLayoutParams(iv);
        imageView.setBackgroundResource(R.mipmap.day_01);

        mTemp = new TextView(mContext);
        LinearLayout.LayoutParams tempLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tempLayoutParams.setMargins(DensityUtil.dip2px(mContext, 5),0,0,0);
        mTemp.setLayoutParams(tempLayoutParams);
        mTemp.setTextColor(Color.WHITE);
        mTemp.setText("10℃");
        mTemp.setTextSize(DensityUtil.sp2px(mContext, 12));

        mDateLayout.addView(mDateText);
        mDateLayout.addView(imageView);
        mDateLayout.addView(mTemp);

        mText = new TextView(mContext);
        LinearLayout.LayoutParams texttParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        texttParams.setMargins(DensityUtil.dip2px(mContext, 5),DensityUtil.dip2px(mContext, 10),0,0);
        mText.setLayoutParams(texttParams);
        mText.setTextColor(Color.WHITE);
        mText.setText(" 自律既自由 自由从心开始");
        mText.setTextSize(DensityUtil.sp2px(mContext, 12));

        mLockView = new LockView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(lockWidth,lockWidth);
        layoutParams.setMargins(0,0,0,100);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mLockView.setLayoutParams(layoutParams);
        mLockView.setOnTouchListener(onTouchListener);

        mLockTextView = new TextView(mContext);
        RelativeLayout.LayoutParams lockTextViewLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lockTextViewLayoutParams.setMargins(0, 0, 0, 30);
        lockTextViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lockTextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mLockTextView.setLayoutParams(lockTextViewLayoutParams);
        mLockTextView.setTextColor(Color.WHITE);
        mLockTextView.setTextSize(12);
        mLockTextView.setText("滑动解锁");

        initAdView();
        mRelativeLyout.addView(mTopTimeText);
        mRelativeLyout.addView(mSecondText);
        mTopLinealayout.addView(mRelativeLyout);
        mTopLinealayout.addView(mDateLayout);
        mTopLinealayout.addView(mText);
        relativeLayout.addView(mTopLinealayout);
        relativeLayout.addView(mainLayout);
        relativeLayout.addView(mLockView);
        relativeLayout.addView(mLockTextView);
    }

    private void initAdView(){
        mainLayout = new LinearLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
//        mainLayout.setBackgroundColor(AlarmLockScreenActivity.mCenterColor);

        int mHeight_25 = DensityUtil.dip2px(mContext, 35);
        int mHeight_5 = DensityUtil.dip2px(mContext, 5);
        mLineView = new View(mContext);
        lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lineLayoutParams.setMargins(mHeight_25,0,mHeight_25,mHeight_5);
        mLineView.setBackgroundColor(mTopTextColor);
        mLineView.setLayoutParams(lineLayoutParams);
        mLineView.setVisibility(View.GONE);

        mTitleText = new TextView(mContext);
        LinearLayout.LayoutParams textLayoutParams;
        textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(mHeight_25, mHeight_5, mHeight_25, 5);
        mTitleText.setLayoutParams(textLayoutParams);
        mTitleText.setMaxEms(50);
        mTitleText.setGravity(Gravity.CENTER);
        mTitleText.setMaxLines(1);
        mTitleText.setTextColor(Color.WHITE);
        mTitleText.getPaint().setFakeBoldText(true);
        mTitleText.setTextSize(14);
//        mTitleText.setText("Need To Do!");

        mSubTitleText = new TextView(mContext);
        textLayoutParams.setMargins(mHeight_25, 10, mHeight_25, 10);
        mSubTitleText.setLayoutParams(textLayoutParams);
        mSubTitleText.setMaxEms(50);
        mSubTitleText.setMaxLines(1);
        mSubTitleText.setTextColor(mTopTextColor);
        mSubTitleText.setTextSize(11);
        mSubTitleText.setText("点击查看详情 >>");
        mSubTitleText.setVisibility(View.GONE);

        mContainer = new FrameLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mHeight_25,mHeight_5,mHeight_25 ,mHeight_5);
        mContainer.setLayoutParams(params);
        mContainer.setBackgroundColor(AlarmLockScreenActivity.mCenterColor);

        mBImageView = new ImageView(mContext);
        FrameLayout.LayoutParams ReimgLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
//        ReimgLayoutParams.setMargins(mHeight_25,mHeight_5,mHeight_25 ,mHeight_5);
        mBImageView.setLayoutParams(ReimgLayoutParams);
        mBImageView.setVisibility(View.GONE);
        mContainer.addView(mBImageView);

        mLabelButton = new RadiumTextView(mContext);
        LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelLayoutParams.setMargins(mHeight_25, 0, mHeight_5, 20);
        mLabelButton.setPadding(3,1,3,1);
        mLabelButton.setLayoutParams(labelLayoutParams);
        mLabelButton.setText("广告");
        mLabelButton.setTextSize(7);
        mLabelButton.setGravity(Gravity.CENTER);
        mLabelButton.setTextColor(mTopTextColor);
        mLabelButton.setVisibility(View.GONE);

        mainLayout.addView(mLineView);
        mainLayout.addView(mTitleText);
        mainLayout.addView(mSubTitleText);
        mainLayout.addView(mContainer);
        mainLayout.addView(mLabelButton);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            Intent intent = new Intent(this,AlarmRingService.class);
            stopService(intent);
        }catch (Exception e) {
            Log.d("alarm", "onDestroy: e = "+e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public TextView getTimeTextView() {
        return mTopTimeText;
    }

    @Override
    public TextView getDateTextView() {
        return mDateText;
    }

    @Override
    public TextView getSecondTextView() {
        return mSecondText;
    }

    private int lastX;
    private int lastY;
    private boolean isclick;
    private long startTime;
    private long endTime;
    private int mLeftDown;
    private int mRightDown;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLeftDown = v.getLeft();
                    mRightDown = v.getRight();
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    isclick = true;//当按下的时候设置isclick为false，具体原因看后边的讲解
                    startTime = System.currentTimeMillis();
                    break;
                /**
                 * layout(l,t,r,b) l Left position, relative to parent t Top position,
                 * relative to parent r Right position, relative to parent b Bottom
                 * position, relative to parent
                 * */
                case MotionEvent.ACTION_MOVE:

                    isclick = true;//当按钮被移动的时候设置isclick为true
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    if (dx > 250) {
                        dx = 250;
                    }
                    if (dx < -250) {
                        dx = -250;
                    }
                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }
                    v.setAlpha((250- dx +10 ) / 250);
                    v.layout(left, v.getTop(), right, v.getBottom());
                    if (Math.abs(v.getLeft() - mLeftDown) > 250) {
                        mHandler.sendEmptyMessage(1003);
                    } else {
                        mHandler.sendEmptyMessage(1001);
                    }
                    if (dx == 250) {
                        mHandler.sendEmptyMessage(1003);
                    }
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.sendEmptyMessage(1002);
                    v.setAlpha(1);
                    v.layout(mLeftDown, v.getTop(), mRightDown, v.getBottom());
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) > 0.1 * 1000L) {
                        isclick = true;
                    } else {
                        isclick = false;
                    }
                    break;
            }
            return isclick;
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    mLockTextView.setAlpha(0.5f);
                    break;
                case 1002:
                    mLockTextView.setAlpha(1);
                    if (isclick && mContext != null) {
                        mContext.finish();
                    }
                    break;
                case 1003:
                    mContext.finish();
                    break;

                case 1004:
                    break;
            }
        }
    };

}
