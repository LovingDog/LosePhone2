package com.example.wanghanp.alarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.wanghanp.base.BaseActivity;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.AddItemView;
import com.example.wanghanp.util.ConsUtils;
import com.example.wanghanp.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AddAlarmActivity extends BaseActivity implements View.OnClickListener{

    private TimePicker mTimePicker;//闹钟定时
    private int mHours=6;
    private int mMinute=30;
    private AddItemView mTag;
    private AddItemView mLazyLevel;
    private AddItemView mRing;
    private int mLevel;
    private String mTagDesc;
    private Intent mIntent;
    private String RingName;
    private String Ringid;
    private TextView mComplete;
    private TextView mTitle;
    private ImageView mBackImg;
    private AddItemView mChooseTime;
    private int[] mChooseDayValue;
    final boolean[] item=new boolean[]{false,false
            ,false,false,false,false,false};
    private String mWeekShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        initView();
        initListener();
    }

    private void initView() {
        mTimePicker = (TimePicker) findViewById(R.id.tp_set_alarm_add);
        mTimePicker.setIs24HourView(true);
        mTag = (AddItemView) findViewById(R.id.aiv_tag_add);
        mChooseTime = (AddItemView) findViewById(R.id.aiv_choose_time);

        mLazyLevel = (AddItemView) findViewById(R.id.aiv_lazy_add);
        mRing = (AddItemView) findViewById(R.id.aiv_ring_add);
        mComplete = (TextView) findViewById(R.id.bar_complete);
        mTitle = (TextView) findViewById(R.id.bar_title);
        mBackImg = (ImageView) findViewById(R.id.bar_back);

        mTitle.setText("返回");
        mComplete.setText("添加闹钟");
        mLevel=0;
        mTagDesc="闹钟";
        Ringid="flower.mp3";
        RingName="everybody";
        mIntent = getIntent();
        if(mIntent.getBooleanExtra("update",false)){
            updateView();
        } else {
            initData();
        }
        mChooseTime.setOnClickListener(this);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneAlarm();
            }
        });
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateView() {
        AlarmInfoDao dao=new AlarmInfoDao(this);
        AlarmInfo alarmInfo=dao.findById(mIntent.getStringExtra("oldId"));
        mTimePicker.setCurrentHour(alarmInfo.getHour());
        mTimePicker.setCurrentMinute(alarmInfo.getMinute());

        int[] day = alarmInfo.getDayOfWeek();//[1,2,4]
        for (int i = 0; i < item.length; i++) {
            for (int j = 0; j < day.length; j++) {
                if ((i+1) == day[j]) {
                    item[i] = true;
                }
            }
        }
        show(day);

        mComplete.setText("修改");
        mTag.setDesc(alarmInfo.getDesc());
        mLazyLevel.setDesc("赖床指数:"+alarmInfo.getLazyLevel()+"级");
        mRing.setDesc(alarmInfo.getRing());

        mHours=alarmInfo.getHour();
        mMinute=alarmInfo.getMinute();
        mLevel=alarmInfo.getLazyLevel();
        mTagDesc=alarmInfo.getDesc();
        Ringid=alarmInfo.getRingResId();
        RingName=alarmInfo.getRing();
    }

    private void initData() {
        String time = TimeUtil.getTimeFromMillisecond(System.currentTimeMillis());

        if (time.contains(":")) {
            String[] str = time.split(":");
            mTimePicker.setCurrentHour(Integer.parseInt(str[0]));
            mTimePicker.setCurrentMinute(Integer.parseInt(str[1]));
            return;
        }
        mTimePicker.setCurrentHour(mHours);
        mTimePicker.setCurrentMinute(mMinute);
    }

    //获取TimePicker的时间
    private void initListener() {
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mMinute = minute;
                mHours = hourOfDay;
            }
        });
        mLazyLevel.setOnClickListener(this);
        mRing.setOnClickListener(this);
        mTag.setOnClickListener(this);
    }
    //点击监听
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aiv_tag_add:
                showTagDialog();
                break;
            case R.id.aiv_lazy_add:
                showLazyDialog();
                break;
            case R.id.aiv_choose_time:
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

                break;
            case R.id.aiv_ring_add:
//                startActivityForResult(new Intent(this, RingSetActivity.class), ConsUtils.ASK_FOR_RING);
                break;
        }
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
        switch (resultCode){
            case ConsUtils.RING_SET_CANCEL:
                break;
            case ConsUtils.RING_SET_DONG:
                RingName=data.getStringExtra("songname");
                if(data.getStringExtra("songid")!=null){
                    Ringid=data.getStringExtra("songid");
                }
                mRing.setDesc(RingName);
                break;
        }
    }

    public AlarmInfo getAddAlarmInfo(){
        AlarmInfo alarmInfo=new AlarmInfo();
        alarmInfo.setHour(mHours);
        alarmInfo.setMinute(mMinute);

        alarmInfo.setDayOfWeek(mChooseDayValue);
        alarmInfo.setLazyLevel(mLevel);
        alarmInfo.setTag("1");
        alarmInfo.setDesc(mTagDesc);
        Log.d("alarm", "加入是的铃声名字" +RingName);
        alarmInfo.setRing(RingName);
        alarmInfo.setRingResId(Ringid);
        return alarmInfo;
    }

    //底边栏的两个方法
    private void doneAlarm(){
        //当用户完成设置时，将时间封装到对象中，传回给homeActivity
        AlarmInfoDao dao=new AlarmInfoDao(this);
        AlarmInfo alarmInfo;
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        if(mIntent.getBooleanExtra("update", false)){
            alarmInfo=getAddAlarmInfo();
//            if (getRepeatDay()[0] == 0) {
//                ToastUtils.showShortToast("请选择时间",this);
//                return;
//            }
            intent.setClass(AddAlarmActivity.this, MainActivity.class);
            bundle.putSerializable("alarm", alarmInfo);
            intent.putExtras(bundle);
            Log.d("alarm", "修改闹钟" + alarmInfo.getDayOfWeek());
            //取消旧的闹钟
            AlarmInfo oldAlarmInfo=dao.findById(mIntent.getStringExtra("oldId"));
            AlarmClock alarmClock=new AlarmClock(AddAlarmActivity.this);
            alarmClock.turnAlarm(oldAlarmInfo,null,false);
            //修改数据库
            dao.updateAlarm(mIntent.getStringExtra("oldId"), alarmInfo);
//            setResult(ConsUtils.UPDATE_ALARM_DONE,intent);
        }else{
            alarmInfo=getAddAlarmInfo();
//            if (getRepeatDay()[0] == 0) {
//                ToastUtils.showShortToast("请选择时间",this);
//                return;
//            }
            dao.addAlarmInfo(alarmInfo);
            bundle.putSerializable("alarm", alarmInfo);
            intent.putExtras(bundle);
            Log.d("alarm", "添加闹钟" + alarmInfo.getDayOfWeek());
            setResult(ConsUtils.SET_ALARM_DONE, intent);
//            AlarmClock alarmClock=new AlarmClock(AddAlarmActivity.this);
//            alarmClock.turnAlarm(alarmInfo,null,true);
        }
        finish();
    }

    private void cancelAlarm(){
        if(mIntent.getStringExtra("oldId")!=null){
            setResult(ConsUtils.UPDATE_ALARM_CANCEL,new Intent());
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            setResult(ConsUtils.SET_ALARM_CANCEL, new Intent());
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelAlarm();
    }

    //选择赖床级数
    private void showLazyDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("选择你的赖床指数");
        String[] item=new String[]{"本宝宝从不赖床！","稍微拖延个七八分钟啦~"
                ,"半个小时准时起床！","七点的闹钟八点起~","闹钟是什么东西？！"};
        dialog.setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLevel=which;
                mLazyLevel.setDesc("赖床指数:"+mLevel+"级");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //编辑标签
    private void showTagDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        AlertDialog dialog=builder.create();
        View edit= View.inflate(this,R.layout.dialog_tag,null);
        final EditText tag= (EditText) edit.findViewById(R.id.et_tag);
      /*  dialog.setTitle();
        dialog.setView(edit);*/
        builder.setTitle("闹钟说明");
        builder.setView(edit);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTagDesc=tag.getText().toString();
                mTag.setDesc(mTagDesc);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
