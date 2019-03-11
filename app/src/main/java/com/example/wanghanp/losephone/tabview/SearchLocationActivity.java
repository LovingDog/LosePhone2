package com.example.wanghanp.losephone.tabview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.Projection;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.wanghanp.db.modle.LocationInfo;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.NoScrollListView;
import com.example.wanghanp.util.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wanghanping on 2018/1/29.
 */

public class SearchLocationActivity extends Activity implements AMapLocationListener,LocationSource {


    @InjectView(R.id.listview)
    NoScrollListView mListView;
    @InjectView(R.id.autotext_search)
    EditText mAutoCompleteTextView;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<Tip> mTipList;
    private ArrayList<String> mSearchReulst;
    TextureMapView mMapView;
    private AMap mAmap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView mTvComplete;
    private LocationInfo locationInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        ButterKnife.inject(this);
        initView();

        mMapView = findViewById(R.id.mapview);
        mTvComplete = findViewById(R.id.tv_complete);
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        if (mAmap != null) {
            initAmap();
            initLocation();
        }
        initEvent();
    }

    private void initEvent() {
        mTvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(LocationRemindActivity.SEARCH_EXTRA, locationInfo);
                intent.putExtra(LocationRemindActivity.BUNDLE,bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initAmap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));
//        if (isAdded()) {
            myLocationStyle.strokeColor(getResources().getColor(
                    R.color.accent_material_light));
            myLocationStyle.radiusFillColor(getResources().getColor(R.color.colorPrimary));
//        }
        myLocationStyle.strokeWidth(0.3f);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);

        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationRotateAngle(180);
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);
        mAmap.setLocationSource(SearchLocationActivity.this);
        mAmap.setMyLocationEnabled(true);
        mAmap.setLocationSource(this);
        mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mAmap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                Log.e("wanghp007", "onCameraChangeFinish: cameraPosition.target"+cameraPosition.target.toString()
                        +"&&getMapCenterPoint = "+getMapCenterPoint().toString() );
                LatLng latLng = getMapCenterPoint();
                mHandler.removeMessages(1001);
                mHandler.sendEmptyMessage(1001);
                ToastUtils.showShortToast("onCameraChangeFinish"+cameraPosition.target.toString(),SearchLocationActivity.this);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            searchAddressByLatLng(getMapCenterPoint());
        }
    };

    private void searchAddressByLatLng(final LatLng latLng){
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener(){

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

                if (locationInfo == null) {
                    locationInfo = new LocationInfo();
                }
                locationInfo.longitude = latLng.longitude;
                locationInfo.latitude = latLng.latitude;
                locationInfo.locationName = result.getRegeocodeAddress().getFormatAddress();
                String formatAddress = result.getRegeocodeAddress().getFormatAddress();
                if (mAutoCompleteTextView != null) {
                    mAutoCompleteTextView.setText(formatAddress);
                }
                Log.e("formatAddress", "formatAddress:"+formatAddress);
                Log.e("formatAddress", "rCode:"+rCode);

            }});
        LatLonPoint lp = new LatLonPoint(latLng.latitude,latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200,GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private void initLocation() {

        mlocationClient = new AMapLocationClient(getApplicationContext());
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(SearchLocationActivity.this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
//设置定位参数
        mLocationOption.setNeedAddress(true);
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setOnceLocation(false);
//        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mlocationClient) {
            mlocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }
    }

    private void initView() {
        mTipList = new ArrayList<>();
        mSearchReulst = new ArrayList<>();
        initCompleteText(mSearchReulst);
        initTextChangeListener();
    }

    @OnClick({R.id.lay1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay1:
                finish();
                break;
        }
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
                locationInfo = new LocationInfo();
                Tip tip = mTipList.get(i);
                if (tip == null) {
                    return;
                }
                locationInfo.longitude = tip.getPoint().getLongitude();
                locationInfo.latitude = tip.getPoint().getLatitude();
                locationInfo.locationName = tip.getDistrict()+tip.getName();

            }
        });
    }

    private void doSearch(String city) {
        InputtipsQuery inputquery = new InputtipsQuery(city, "");
        inputquery.setCityLimit(true);//限制在当前城市
        mTipList.clear();
        mSearchReulst.clear();
        Inputtips inputTips = new Inputtips(getApplicationContext(), inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int code) {
//                mTipList = list;
//                mSearchReulst.clear();
                for (int j = 0; j < list.size(); j++) {
                    Tip tip = list.get(j);
                    mTipList.add(tip);
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

    public MarkerOptions getMarkerOption(String str, double lat, double lgt){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
        markerOptions.position(new LatLng(lat,lgt));
        markerOptions.title(str);
        markerOptions.snippet("纬度:" + lat + "   经度:" + lgt);
        markerOptions.period(100);

        return markerOptions;
    }

    /**
     * by moos on 2017/09/05
     * func:获取屏幕中心的经纬度坐标
     * @return
     */
    public LatLng getMapCenterPoint() {
        int left = mMapView.getLeft();
        int top = mMapView.getTop();
        int right = mMapView.getRight();
        int bottom = mMapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mMapView.getX() + (right - left) / 2);
        int y = (int) (mMapView.getY() + (bottom - top) / 2);
        Projection projection = mAmap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            ToastUtils.showShortToast(aMapLocation.getAddress(),SearchLocationActivity.this);

            mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude())));
            getMarkerOption("当前位置",aMapLocation.getLatitude(),aMapLocation.getLongitude());
            //添加定位图标
                        mlocationClient.stopLocation();

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        ToastUtils.showShortToast("activate",SearchLocationActivity.this);
        if (null != mlocationClient) {
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mlocationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        ToastUtils.showShortToast("activate",SearchLocationActivity.this);
    }
}
