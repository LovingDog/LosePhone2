package com.example.wanghanp.losephone.tabview;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
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
    private int mCurrenIndex;
    private CommonAdapter<LocationInfo> mCommonAdapter;
    private boolean mIsAdd;

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
        initDbData(false);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initDbData(boolean add) {
        mIsAdd = add;
        mLocationController = new LocationController(this);
        mLocationList = mLocationController.getLocationInfos();
        if (add) {
            mLocationList.add(new LocationInfo());
        }
        initCommonAdater(mLocationList);
    }

    private void initCommonAdater(List<LocationInfo> list) {
        mCommonAdapter = new CommonAdapter<LocationInfo>(this, R.layout.list_item_location_remind, list) {
            @Override
            protected void convert(ViewHolder holder, final LocationInfo LocationInfo, final int position) {
                final EditText name = holder.getConvertView().findViewById(R.id.autotext_search);
                final EditText content = holder.getConvertView().findViewById(R.id.edit_content);
                if (position == mLocationList.size() - 1 && mIsAdd ) {
                    holder.setVisible(R.id.lay_confirm, true);
                    content.setFocusableInTouchMode(true);
                    name.setEnabled(true);
                } else {
                    holder.setVisible(R.id.lay_confirm, false);
                    name.setEnabled(false);
                    content.setFocusable(false);
                    name.setBackground(null);
                    content.setBackground(null);

                }
                holder.setText(R.id.edit_content, LocationInfo.content);
                holder.setText(R.id.autotext_search, LocationInfo.locationName);
                holder.setOnClickListener(R.id.autotext_search, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCurrenIndex = position;
                        startActivityForResult(new Intent(LocationRemindActivity.this, SearchLocationActivity.class), REQ_SEARCH);
                    }
                });

                holder.setOnClickListener(R.id.lay_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LocationInfo locationInfo = mLocationList.get(position);

                        if (!name.getText().toString().equals("") && !content.getText().toString().equals("")) {
                            locationInfo.content = content.getText().toString();
                            mLocationController.insertLocationRemind(mLocationList.get(position));
                            initDbData(false);
                        }
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
                ToastUtils.showLongToast(getApplicationContext(), locationinfo.locationName);
                LocationInfo locationInfo2 = mLocationList.get(mCurrenIndex);
                locationInfo2.locationName = locationinfo.locationName;
                locationInfo2.latitude = locationinfo.latitude;
                locationInfo2.longitude = locationinfo.longitude;
                initCommonAdater(mLocationList);
                break;
        }
    }

    @OnClick({R.id.bar_lay_complete,R.id.bar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_lay_complete:
                initDbData(true);
                break;
            case R.id.bar_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationController.close();
    }
}
