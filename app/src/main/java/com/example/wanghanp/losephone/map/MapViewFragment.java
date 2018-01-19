package com.example.wanghanp.losephone.map;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.service.SlideSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wanghanping on 2018/1/17.
 */

public class MapViewFragment extends Fragment implements AMapLocationListener,LocationSource,GeocodeSearch.OnGeocodeSearchListener {

    TextureMapView mMapView;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMap mAmap;
    private OnLocationChangedListener mListener = null;//定位监听器
    private ArrayList<TakePhotoBean> mPhotosList;

    private double mLastLat;
    private double mLastLong;
    private GeocodeSearch mGeocoderSearch;
    private double mHomeLastLat;
    private double mHomeLastLong;
    private SlideSettings mSlideSetting;

    public static final MapViewFragment getInstance(String path){
        MapViewFragment mapviewFragment = new MapViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bundle",path);
        mapviewFragment.setArguments(bundle);
        return mapviewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(getActivity());
        mSlideSetting = SlideSettings.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview,null);
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        initAmap();
        initLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchName("雨花台区梅山街道梅清苑2号门","025");
            }
        },1000);
        return view;
    }

    private void initAmap() {
        mGeocoderSearch = new GeocodeSearch(getActivity().getApplicationContext());
        mGeocoderSearch.setOnGeocodeSearchListener(this);


        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));
        if (isAdded()) {
            myLocationStyle.strokeColor(getResources().getColor(
                    R.color.accent_material_light));
            myLocationStyle.radiusFillColor(getResources().getColor(R.color.colorPrimary));
        }
        myLocationStyle.strokeWidth(1.3f);
        myLocationStyle.showMyLocation(false);

        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationRotateAngle(180);
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);
        mAmap.setLocationSource(MapViewFragment.this);
        mAmap.setMyLocationEnabled(true);
        mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
    }

    private void searchName(String name,String cityCode) {
        GeocodeQuery query = new GeocodeQuery(name, "025");

        mGeocoderSearch.getFromLocationNameAsyn(query);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initLocation() {

        mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(MapViewFragment.this);
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
//        if (null != mlocationClient) {
            mlocationClient.setLocationOption(mLocationOption);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            mlocationClient.stopLocation();
            mlocationClient.startLocation();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                double lat = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                float distance = AMapUtils.calculateLineDistance(new LatLng(mLastLat,mLastLong),new LatLng(lat,longitude));
                Log.d("wanghp007", "onLocationChanged: distance == " +distance);
                if (distance > 1000) {
                    mAmap.clear();
                    mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);

                    String paths = getArguments().getString("bundle");
                    TakePhotoBean takephotoBean = new TakePhotoBean();
                    takephotoBean.setPath(paths);
                    takephotoBean.setmIsSelf(true);
                    takephotoBean.setmLat(aMapLocation.getLatitude());
                    takephotoBean.setmLong(aMapLocation.getLongitude());
                    addMarker(takephotoBean);
                }
                mLastLat = aMapLocation.getLatitude();
                mLastLong = aMapLocation.getLongitude();
                float homeDistance = AMapUtils.calculateLineDistance(new LatLng(mHomeLastLat,mHomeLastLong),new LatLng(lat,longitude));
                if (homeDistance < 1000) {
                    mSlideSetting.playEnhancementMusic(getActivity());
                }
            }
        }
    }
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    public void addMarker(final TakePhotoBean bean) {
        final View mapimgView = LayoutInflater.from(getActivity())
                .inflate(R.layout.mapview, null);
        final ImageView imageview = mapimgView.findViewById(R.id.icon);
        Glide.with(getActivity())
                .load(bean.getPath())
                .placeholder(R.mipmap.person)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .into(new GlideDrawableImageViewTarget(imageview) {

                    @Override
                    public void onResourceReady(GlideDrawable arg0,
                                                GlideAnimation<? super GlideDrawable> arg1) {
                        // TODO Auto-generated method stub
                        super.onResourceReady(arg0, arg1);
                        imageview.setImageDrawable(arg0);
                        MarkerOptions options = new MarkerOptions();
                        BitmapDescriptor bpd = BitmapDescriptorFactory
                                .fromView(mapimgView);
                        options.draggable(false)
                                .position(new LatLng(bean.getmLat(),bean.getmLong())).snippet(" ")
                                .icon(bpd);
                        mAmap.addMarker(options);
                    }
                });
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {//i == 1000
        if (i == 1000) {
            List<GeocodeAddress> list = geocodeResult.getGeocodeAddressList();
            for (int j = 0; j < list.size(); j++) {
                GeocodeAddress address = list.get(j);
                Log.d("wanghp007", "onGeocodeSearched: " + address.getLatLonPoint());
                LatLonPoint latPoint = address.getLatLonPoint();
                mHomeLastLat = latPoint.getLatitude();
                mHomeLastLong = latPoint.getLongitude();
                Log.d("wanghp007", "onGeocodeSearched: " + latPoint.getLatitude() + " " + latPoint.getLongitude());
            }
        }
    }
}
