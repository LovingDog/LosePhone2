package com.example.wanghanp.losephone.tabview;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.amap.api.services.help.Tip;
import com.example.wanghanp.db.LocationController;
import com.example.wanghanp.db.modle.LocationInfo;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.util.ToastUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wanghanping on 2018/1/29.
 */

public class LocationRemindActivity extends AppCompatActivity {
    public static final int REQ_SEARCH = 101;
    public static final String BUNDLE = "bundle";
    public static final String SEARCH_EXTRA = "tip";

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @InjectView(R.id.bar_lay_complete)
    LinearLayout mAddLay;
    @InjectView(R.id.bar_complete)
    TextView mAddText;
    @InjectView(R.id.bar_title)
    TextView mBarTitle;

    private LocationController mLocationController;

    private List<LocationInfo> mLocationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_remind);
        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        mAddText.setText("添加");
//        mBarTitle.setVisibility(View.GONE);
        mBarTitle.setText("地点闹钟提醒");
        mLocationList = new ArrayList<>();
        initRecyclerView();
        initDbData();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initDbData() {
       mLocationController = new LocationController(this);
        mLocationList = mLocationController.getLocationInfos();
        mLocationList.add(new LocationInfo());
        initCommonAdater(mLocationList);
    }

    private void initCommonAdater (List<LocationInfo> list) {
        CommonAdapter<LocationInfo> mCommonAdapter = new CommonAdapter<LocationInfo>(this,R.layout.list_item_location_remind,list) {
            @Override
            protected void convert(ViewHolder holder, LocationInfo LocationInfo, int position) {
//                holder.setText(R.id.edit_content,LocationInfo.content);
//                holder.setText(R.id.autotext_search,LocationInfo.locationName);
                holder.setOnClickListener(R.id.autotext_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         startActivityForResult(new Intent(LocationRemindActivity.this,SearchLocationActivity.class),REQ_SEARCH);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mCommonAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQ_SEARCH:
                LocationInfo locationinfo = (LocationInfo) data.getBundleExtra(BUNDLE).getSerializable(SEARCH_EXTRA);
                ToastUtils.showLongToast(getApplicationContext(),locationinfo.locationName);
                break;
        }
    }

    @OnClick({R.id.bar_lay_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_lay_complete:
                initDbData();
                break;
        }
    }
}
