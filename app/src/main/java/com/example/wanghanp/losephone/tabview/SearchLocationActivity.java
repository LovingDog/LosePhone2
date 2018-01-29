package com.example.wanghanp.losephone.tabview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.wanghanp.base.preference.BasePreference;
import com.example.wanghanp.base.preference.SettingPreference;
import com.example.wanghanp.db.modle.LocationInfo;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.NoScrollListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wanghanping on 2018/1/29.
 */

public class SearchLocationActivity extends AppCompatActivity {


    @InjectView(R.id.listview)
    NoScrollListView mListView;
    @InjectView(R.id.autotext_search)
    EditText mAutoCompleteTextView;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<Tip> mTipList;
    private ArrayList<String> mSearchReulst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        mTipList = new ArrayList<>();
        mSearchReulst = new ArrayList<>();
        initCompleteText(mSearchReulst);
        initTextChangeListener();
    }

    private void initTextChangeListener() {
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                doSearch(editable.toString());
            }
        });
    }

    private void initCompleteText(final ArrayList<String> mSearchReulst) {

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mSearchReulst);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mListView.setVisibility(View.GONE);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                LocationInfo locationInfo = new LocationInfo();
                Tip tip = mTipList.get(i);
                if (tip == null) {
                    return;
                }
                locationInfo.longitude = tip.getPoint().getLongitude();
                locationInfo.latitude = tip.getPoint().getLatitude();
                locationInfo.locationName = tip.getDistrict()+tip.getName();
                bundle.putSerializable(LocationRemindActivity.SEARCH_EXTRA, locationInfo);
                intent.putExtra(LocationRemindActivity.BUNDLE,bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void doSearch(String city) {
        InputtipsQuery inputquery = new InputtipsQuery(city, "");
        inputquery.setCityLimit(true);//限制在当前城市
        mTipList.clear();
        Inputtips inputTips = new Inputtips(getApplicationContext(), inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int code) {
                Log.d("wanghp007", "onGetInputtips: list.size() === " +list.size());
//                mTipList = list;
//                mSearchReulst.clear();
                for (int j = 0; j < list.size(); j++) {
                    Tip tip = list.get(j);
                    mTipList.add(tip);
                    Log.d("wanghp007", "onGetInputtips: tip.lat = " +tip.getPoint().getLatitude());
                    mSearchReulst.add(tip.getDistrict()+tip.getName());
                }
                if (mAdapter != null) {
//                    initCompleteText(mSearchReulst);
//                    mAutoCompleteTextView.re
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        inputTips.requestInputtipsAsyn();
    }
}
