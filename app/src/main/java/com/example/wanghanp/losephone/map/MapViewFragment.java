//package com.example.wanghanp.losephone.map;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.AMapUtils;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.LocationSource;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.TextureMapView;
//import com.amap.api.maps.model.BitmapDescriptor;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.model.MyLocationStyle;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.geocoder.GeocodeAddress;
//import com.amap.api.services.geocoder.GeocodeQuery;
//import com.amap.api.services.geocoder.GeocodeResult;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.RegeocodeResult;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
//import com.example.wanghanp.base.bean.TakePhotoBean;
//import com.example.wanghanp.base.preference.BasePreference;
//import com.example.wanghanp.base.preference.SettingPreference;
//import com.example.wanghanp.db.LocationController;
//import com.example.wanghanp.db.modle.LocationInfo;
//import com.example.wanghanp.losephone.BaiduSpeakMainActivity;
//import com.example.wanghanp.losephone.R;
//import com.example.wanghanp.losephone.service.SlideSettings;
//import com.example.wanghanp.permissioncheck.PermissionsActivity;
//import com.example.wanghanp.util.CommonUtil;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//
///**
// * Created by wanghanping on 2018/1/17.
// */
//
//public class MapViewFragment extends Fragment implements AMapLocationListener,LocationSource {
//
//    TextureMapView mMapView;
//    //声明mlocationClient对象
//    public AMapLocationClient mlocationClient;
//    //声明mLocationOption对象
//    public AMapLocationClientOption mLocationOption = null;
//    private AMap mAmap;
//    private OnLocationChangedListener mListener = null;//定位监听器
//    private ArrayList<TakePhotoBean> mPhotosList;
//
//    private double mLastLat;
//    private double mLastLong;
//    private GeocodeSearch mGeocoderSearch;
//    private boolean mRemindLater = true;
//    private SettingPreference mSettingPreference;
//    private LocationController mLocationController;
//    private List<LocationInfo> mLocationList;
//
//    public static final MapViewFragment getInstance(String path){
//        MapViewFragment mapviewFragment = new MapViewFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("bundle",path);
//        mapviewFragment.setArguments(bundle);
//        return mapviewFragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_mapview, container, false);
//        ButterKnife.inject(this,view);
//        mMapView = view.findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        mAmap = mMapView.getMap();
//        if (mAmap != null) {
//            initAmap();
//            initLocation();
//        }
//        mSettingPreference = new SettingPreference(getActivity(), BasePreference.Preference.APP_SETTING);
//        return view;
//    }
//
//    private void initAmap() {
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.location_marker));
//        if (isAdded()) {
//            myLocationStyle.strokeColor(getResources().getColor(
//                    R.color.accent_material_light));
//            myLocationStyle.radiusFillColor(getResources().getColor(R.color.colorPrimary));
//        }
//        myLocationStyle.strokeWidth(0.3f);
//        myLocationStyle.showMyLocation(false);
//
//        mAmap.setMyLocationStyle(myLocationStyle);
//        mAmap.setMyLocationRotateAngle(180);
//        mAmap.getUiSettings().setMyLocationButtonEnabled(true);
//        mAmap.setLocationSource(MapViewFragment.this);
//        mAmap.setMyLocationEnabled(true);
//        mAmap.setMapType(AMap.MAP_TYPE_NORMAL);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//    private void initDbData() {
//        mLocationController = new LocationController(getActivity());
//        mLocationList = mLocationController.getLocationInfos();
//        Log.d("wanghp007", "initDbData: mLocationList.size = " +mLocationList.size());
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        initDbData();
//    }
//
//    private void initLocation() {
//
//        mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
////初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
////设置定位监听
//        mlocationClient.setLocationListener(MapViewFragment.this);
////设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
////设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(20000);
////设置定位参数
//        mLocationOption.setNeedAddress(true);
//        /**
//         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
//         */
//        mLocationOption.setOnceLocation(false);
////        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
//        if (null != mlocationClient) {
//            mlocationClient.setLocationOption(mLocationOption);
//        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
////            mlocationClient.stopLocation();
//            mlocationClient.startLocation();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ((BaiduSpeakMainActivity)getActivity()).pause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mlocationClient.stopLocation();
//        mlocationClient.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        Log.d("wanghp007", "onLocationChanged"+mSettingPreference.isRemindLocation() );
//        if (!mSettingPreference.isRemindLocation()) {
//            return;
//        }
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                double lat = aMapLocation.getLatitude();
//                double longitude = aMapLocation.getLongitude();
//                float distance = AMapUtils.calculateLineDistance(new LatLng(mLastLat,mLastLong),new LatLng(lat,longitude));
//                Log.d("wanghp007", "onLocationChanged: distance == " +distance);
////                new CompareDistanceAsyncTask().execute();
//                if (distance > 1000) {
//                    mAmap.clear();
//                    mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
//                    //将地图移动到定位点
//                    mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
//                    //点击定位按钮 能够将地图的中心移动到定位点
//                    mListener.onLocationChanged(aMapLocation);
//
//                    String paths = getArguments().getString("bundle");
//                    TakePhotoBean takephotoBean = new TakePhotoBean();
//                    takephotoBean.setPath(paths);
//                    takephotoBean.setmIsSelf(true);
//                    takephotoBean.setmLat(aMapLocation.getLatitude());
//                    takephotoBean.setmLong(aMapLocation.getLongitude());
//                    addMarker(takephotoBean);
//                }
//                mLastLat = aMapLocation.getLatitude();
//                mLastLong = aMapLocation.getLongitude();
//                excuteCompareLatlng();
//            }
//        }
//    }
//
//    private void excuteCompareLatlng(){
//        for (LocationInfo location:
//                mLocationList) {
//            double compareLat = location.latitude;
//            double compreLong = location.longitude;
//
//            Log.d("wanghp007", "doInBackground: compareLat = "+compareLat+"&& compreLong = " +compreLong);
//            float homeDistance = AMapUtils.calculateLineDistance(new LatLng(compareLat,compreLong),new LatLng(mLastLat,mLastLong));
//            Log.d("wanghp007", "doInBackground: homeDistance = "+homeDistance);
//            //homeDistance < 600 &&
//            if (homeDistance < 600 && mRemindLater) {
//                CommonUtil.simpleNotify(getActivity(), "新消息", "闹钟提醒",location.content, "LosePhone");
//                if (!((BaiduSpeakMainActivity)getActivity()).isCheck()){
//                    showMissingPermissionDialog(location.content);
//                    ((BaiduSpeakMainActivity)getActivity()).play();
//                    mRemindLater = false;
//                }
//                break;
//            }
//        }
//    }
//
//    class CompareDistanceAsyncTask extends AsyncTask<Void,Void,Void>{
//
////        private double latitude;
////        private double longitude;
////        public CompareDistanceAsyncTask(double lat,double longi) {
////            this.latitude = lat;
////            this.longitude = longi;
////        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Log.d("wanghp007", "doInBackground: mLocationList.size = "+mLocationList.size());
//            for (LocationInfo location:
//                    mLocationList) {
//                double compareLat = location.latitude;
//                double compreLong = location.longitude;
//
//                Log.d("wanghp007", "doInBackground: compareLat = "+compareLat+"&& compreLong = " +compreLong);
//                float homeDistance = AMapUtils.calculateLineDistance(new LatLng(compareLat,compreLong),new LatLng(mLastLat,mLastLong));
//                Log.d("wanghp007", "doInBackground: homeDistance = "+homeDistance);
//                //homeDistance < 600 &&
//                if (homeDistance < 600 && mRemindLater) {
//                    CommonUtil.simpleNotify(getActivity(), "新消息", "消息提醒",location.content, "LosePhone");
//                    if (!((BaiduSpeakMainActivity)getActivity()).isCheck()){
//                        showMissingPermissionDialog(location.content);
//                        ((BaiduSpeakMainActivity)getActivity()).play();
//                        mRemindLater = false;
//                    }
//                    break;
//                }
//            }
//            return null;
//        }
//    }
//    private void showMissingPermissionDialog(String content) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//        dialogBuilder.setTitle("提醒");
//        dialogBuilder.setMessage("主人，你已到家，请关注下");
//        dialogBuilder.setNegativeButton("等下再提醒", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mRemindLater = true;
//                ((BaiduSpeakMainActivity)getActivity()).play();
//            }
//        });
//        dialogBuilder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mRemindLater = false;
//                if (CommonUtil.isWeixinAvilible(getActivity())) {
//                    CommonUtil.startWeChatAc(getActivity());
//                    mlocationClient.stopLocation();
//                    ((BaiduSpeakMainActivity)getActivity()).pause();
//                }
//            }
//        });
//        dialogBuilder.setCancelable(false);
//        dialogBuilder.show();
//    }
//
//
//
//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//    }
//
//    @Override
//    public void deactivate() {
//
//    }
//
//    public void addMarker(final TakePhotoBean bean) {
//        final View mapimgView = LayoutInflater.from(getActivity())
//                .inflate(R.layout.mapview, null);
//        final ImageView imageview = mapimgView.findViewById(R.id.icon);
//        Glide.with(getActivity())
//                .load(bean.getPath())
//                .placeholder(R.mipmap.person)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .crossFade()
//                .into(new GlideDrawableImageViewTarget(imageview) {
//
//                    @Override
//                    public void onResourceReady(GlideDrawable arg0,
//                                                GlideAnimation<? super GlideDrawable> arg1) {
//                        // TODO Auto-generated method stub
//                        super.onResourceReady(arg0, arg1);
//                        imageview.setImageDrawable(arg0);
//                        MarkerOptions options = new MarkerOptions();
//                        BitmapDescriptor bpd = BitmapDescriptorFactory
//                                .fromView(mapimgView);
//                        options.draggable(false)
//                                .position(new LatLng(bean.getmLat(),bean.getmLong())).snippet(" ")
//                                .icon(bpd);
//                        mAmap.addMarker(options);
//                    }
//                });
//    }
//}
