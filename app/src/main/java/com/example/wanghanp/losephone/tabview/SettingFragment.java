package com.example.wanghanp.losephone.tabview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wanghanp.alarm.AddAlarmActivity;
import com.example.wanghanp.alarm.AlarmClock;
import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.alarm.AlarmLockScreenActivity;
import com.example.wanghanp.alarm.location_alarm.LocationAlarmActivity;
import com.example.wanghanp.baiduspeak.BaiduSpeakMainActivity;
import com.example.wanghanp.base.preference.BasePreference;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.PublishPopWindow;
import com.example.wanghanp.service.LongRunningService;
import com.example.wanghanp.util.ConsUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MESSAGE_REFRESH_CONFIRM = 1001;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private boolean mSafeEnable = false;
    private boolean mMusicEnable = true;
    private boolean mElectricEnable = true;
    private boolean mMapEnable = true;
    private AlarmInfoDao mDao;
    private AlarmClock mAlarmClock;
    private List<AlarmInfo> mAlarmInfoList;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //添加新的闹钟时回调该方法
    public void addAlarmInfo(boolean startService){
        if (startService) {
            mDao = new AlarmInfoDao(getActivity());
            mAlarmClock = new AlarmClock(getActivity());
            mAlarmInfoList = mDao.getAllInfo();
            if(mAlarmInfoList.size()>0){
                //开启闹钟或关闭闹钟
                Bundle bundle=new Bundle();
                bundle.putSerializable("alarminfo", (Serializable) mAlarmInfoList);
                bundle.setClassLoader(AlarmInfo.class.getClassLoader());
                Intent intent = new Intent(getActivity(),LongRunningService.class);
                intent.putExtra("bundle",bundle);
                getActivity().startService(intent);
            }
        }
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addAlarmInfo(true);
//        mDao = new AlarmInfoDao(getActivity());
//        mAlarmClock = new AlarmClock(getActivity());
//        mAlarmInfoList = mDao.getAllInfo();
//        if(mAlarmInfoList.size()>0){
//            new Thread(){
//                @Override
//                public void run() {
//                    for (AlarmInfo alarmInfo:mAlarmInfoList) {
//                        Boolean isAlarmOn=PrefUtils.getBoolean(getContext(), alarmInfo.getId(), true);
//                        //开启闹钟或关闭闹钟
//                        mAlarmClock.turnAlarm(alarmInfo,null,isAlarmOn);
//                    }
//                }
//            }.start();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    PublishPopWindow popWindow;

    public void onClick(){
        popWindow = new PublishPopWindow(getActivity(), new PublishPopWindow.OnPopWindowClick() {
            @Override
            public void click(int type) {
                switch (type) {
                    case 0:
                        popWindow.dismiss();
//                        startActivity(new Intent(getActivity(), BaiduSpeakMainActivity.class));
                        getActivity().startActivityForResult(new Intent(getActivity(),AddAlarmActivity.class), ConsUtils.SET_ALARM_DONE);
                        break;
                    case 1:
                        popWindow.dismiss();
                        getActivity().startActivityForResult(new Intent(getActivity(),LocationAlarmActivity.class), ConsUtils.SET_LOCATION_ALARM_DONE);
                        break;
                }
            }
        });
        popWindow.showMoreWindow(mRecyclerView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("alarm","fragment_onactivityResult");
    }

    private void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL));
        mDao = new AlarmInfoDao(getActivity());
        mAlarmClock = new AlarmClock(getActivity());
        mAlarmInfoList = mDao.getAllInfo();
        initData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    return false;
                }


            });
        }
    }


    private void initData() {
        CommonAdapter<AlarmInfo> commonAdapter  = new CommonAdapter<AlarmInfo>(getActivity(),R.layout.list_item_clock,mAlarmInfoList) {
            @Override
            protected void convert(ViewHolder holder, AlarmInfo clock, int position) {
                final AlarmInfo clock1 = mAlarmInfoList.get(position);
                final String show = clock1.getTag();
                if (show.equals("1")) {
                    holder.getConvertView().setAlpha(1.0f);
                    holder.setBackgroundRes(R.id.iv_switch,R.mipmap.offline_diagnose_enable);
                } else {
                    holder.getConvertView().setAlpha(0.5f);
                    holder.setBackgroundRes(R.id.iv_switch,R.mipmap.offline_diagnose_unable);
                }
                if (clock1.getHour() == -1) {
                    holder.setText(R.id.tv_clock_time,clock1.getLocation());
//                    holder.setBackgroundRes(R.id.tv_clock_type,R.mipmap.clock_location);
                } else {
                    holder.setText(R.id.tv_clock_time,clock1.getHour()+":"+clock1.getMinute());
//                    holder.setBackgroundRes(R.id.tv_clock_type,R.mipmap.clock_time);
                }

                if (clock1.getDesc() != null) {
                    holder.setText(R.id.tv_clock_content,clock1.getDesc());
                } else {
                    holder.setText(R.id.tv_clock_content,"温馨提醒");
                }

                String day=Arrays.toString(clock1.getDayOfWeek());
                String desc;
                //判断当前的重复天数
                if(day.equals("0")){
                    desc="一次性闹钟";
                }else if(day.equals("[1,2,3,4,5]")){
                    desc="工作日";
                }else if(day.equals("[1,2,3,4,5,6,7]")){
                    desc="每天";
                }else if(day.equals("[6,7]")){
                    desc="周末";
                }else{
                    desc="每周 "+day+" 重复";
                }
                holder.setText(R.id.tv_clock_status, desc);

                holder.setOnClickListener(R.id.iv_switch, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (show.equals("1")) {
                            clock1.setTag("0");
                        } else {
                            clock1.setTag("1");
                        }
                        mDao.updateAlarm(clock1.getId(),clock1);
                        initData();
                    }
                });
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clock1.getLocation() != null) {
                            Intent intent = new Intent(getActivity(),LocationAlarmActivity.class);
                            intent.putExtra("update",true);
                            intent.putExtra("oldId",clock1.getId());
                            getActivity().startActivityForResult(intent, ConsUtils.SET_ALARM_DONE);
                        } else {
                            Intent intent = new Intent(getActivity(),AddAlarmActivity.class);
                            intent.putExtra("update",true);
                            intent.putExtra("oldId",clock1.getId());
                            getActivity().startActivityForResult(intent, ConsUtils.SET_ALARM_DONE);
                        }
                    }
                });
            }
        };
        mRecyclerView.setAdapter(commonAdapter);
    }

    private void checkeEnable(View view,boolean enable){
        if (enable) {
            view.setBackgroundResource(R.mipmap.offline_diagnose_unable);
        } else {
            view.setBackgroundResource(R.mipmap.offline_diagnose_enable);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
