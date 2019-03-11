package com.example.wanghanp.alarm.location_alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.base.BaseActivity;
import com.example.wanghanp.db.modle.LocationInfo;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.tabview.LocationRemindActivity;
import com.example.wanghanp.losephone.tabview.SearchLocationActivity;
import com.example.wanghanp.myview.AddItemView;
import com.example.wanghanp.util.ConsUtils;
import com.example.wanghanp.util.StringUtils;
import com.example.wanghanp.util.ToastUtils;
import com.google.gson.JsonObject;

public class LocationAlarmActivity extends BaseActivity {

    public static final int REQ_SEARCH = 101;
    public static final String BUNDLE = "bundle";
    public static final String SEARCH_EXTRA = "tip";

    private AddItemView mContent;
    private AddItemView mChooseTime;
    private AddItemView mLocation;
    private TextView mComplete;
    private TextView mTitle;
    private JsonObject mJsonObject;
    private String mWeekShow;
    private LocationInfo locationinfo;
    private int[] mChooseDayValue;
    private LinearLayout mCompleteLay;
    private ImageView mBackImg;
    private String mDesc;
    final boolean[] item=new boolean[]{false,false
            ,false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm_location);
        mContent = (AddItemView) findViewById(R.id.aiv_tag_content);
        mChooseTime = (AddItemView) findViewById(R.id.aiv_choose_time);
        mLocation = (AddItemView) findViewById(R.id.aiv_location);
        mComplete = (TextView) findViewById(R.id.bar_complete);
        mCompleteLay = (LinearLayout) findViewById(R.id.bar_lay_complete);
        mBackImg = (ImageView) findViewById(R.id.bar_back);
        mTitle = (TextView) findViewById(R.id.bar_title);
        mTitle.setText("返回");
        mComplete.setText("确定");
        mJsonObject = new JsonObject();

        initData();
        initEvent();
    }

    private void initData() {
        if (getIntent().hasExtra("update")) {
            String id = getIntent().getStringExtra("oldId");
            AlarmInfoDao alarmInfoDao = new AlarmInfoDao(this);
            AlarmInfo alarmInfo = alarmInfoDao.findById(id);
            mLocation.setDesc(alarmInfo.getLocation());
            mContent.setDesc(alarmInfo.getDesc());
            int[] day = alarmInfo.getDayOfWeek();//[1,2,4]
            for (int i = 0; i < item.length; i++) {
                for (int j = 0; j < day.length; j++) {
                    if ((i+1) == day[j]) {
                        item[i] = true;
                    }
                }
            }
            show(day);
        }
    }

    public AlarmInfo getAddAlarmInfo(){
        AlarmInfo alarmInfo=new AlarmInfo();
        alarmInfo.setHour(-1);
        alarmInfo.setMinute(-1);

        alarmInfo.setDayOfWeek(mChooseDayValue);
        alarmInfo.setLazyLevel(1);
        alarmInfo.setTag("1");
        alarmInfo.setRing("");
        alarmInfo.setRingResId("");
        alarmInfo.setDesc(mDesc);
        alarmInfo.setLat(String.valueOf(locationinfo.latitude));
        alarmInfo.setLon(String.valueOf(locationinfo.longitude));
        alarmInfo.setLocation(locationinfo.locationName);
        return alarmInfo;
    }

    private void initEvent() {
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCompleteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("wanghp","onClickComplete");
                if (mChooseDayValue == null || mChooseDayValue.length == 0) {
                    ToastUtils.showShortToast("时间不能为空", LocationAlarmActivity.this);
                    return;
                }
                if (locationinfo.locationName == null) {
                    ToastUtils.showShortToast("地址不能为空",LocationAlarmActivity.this);
                    return;
                }
                AlarmInfoDao dao=new AlarmInfoDao(LocationAlarmActivity.this);

                AlarmInfo alarmInfo = getAddAlarmInfo();
                dao.addAlarmInfo(alarmInfo);
                Intent intent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("alarm", alarmInfo);
                intent.putExtras(bundle);
                Log.d("alarm", "添加闹钟" + alarmInfo.getDayOfWeek());
                setResult(ConsUtils.SET_LOCATION_ALARM_DONE, intent);
                finish();
            }
        });
        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagDialog(new OnEditorLisntener() {
                    @Override
                    public void positionCall() {

                    }

                    @Override
                    public void negativeCall(String value) {
                        mDesc = value;
                        mContent.setDesc(value);
                    }

                    @Override
                    public void negativeCall(int[] value) {

                    }
                });
            }
        });

        mChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLazyDialog(item, new OnEditorLisntener() {
                    @Override
                    public void positionCall() {

                    }

                    @Override
                    public void negativeCall(String value) {

                    }

                    @Override
                    public void negativeCall(int[] value) {
                        if (value == null) {
                            return;
                        }
                        mChooseDayValue = value;
                        Log.d("wanghp","value = "+value);
                        show(value);
                    }
                });
            }
        });

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LocationAlarmActivity.this, SearchLocationActivity.class), REQ_SEARCH);
            }
        });
    }

    private void show(int[] value){
        mWeekShow = "";
        int count = 0;
        for (int i = 0; i < value.length; i++) {
            int item1 = value[i];
            count++;
            switch (item1) {
                case 7:
                    mWeekShow = mWeekShow+"周日，";
                    break;
                case 1:
                    mWeekShow = mWeekShow+"周一，";
                    break;
                case 2:

                    mWeekShow = mWeekShow+"周二，";
                    break;
                case 3:

                    mWeekShow = mWeekShow+"周三，";
                    break;
                case 4:

                    mWeekShow = mWeekShow+"周四，";
                    break;
                case 5:

                    mWeekShow = mWeekShow+"周五，";
                    break;
                case 6:

                    mWeekShow = mWeekShow+"周六，";
                    break;
            }
        }
        mChooseTime.setDesc(mWeekShow);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_SEARCH:
                locationinfo = (LocationInfo) data.getBundleExtra(BUNDLE).getSerializable(SEARCH_EXTRA);
                Log.d("alarm", "onActivityResult: lat = "+locationinfo.latitude +"&long = "+locationinfo.longitude);
                mLocation.setDesc(locationinfo.locationName);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
