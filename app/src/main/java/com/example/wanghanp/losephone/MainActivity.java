package com.example.wanghanp.losephone;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.bumptech.glide.Glide;
import com.example.wanghanp.base.UploadContract;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.Shake.contract.UploadImgPresenter;
import com.example.wanghanp.losephone.Shake.contract.adapter.BannerAdapter;
import com.example.wanghanp.losephone.camera.CameraManager;
import com.example.wanghanp.losephone.map.MapViewFragment;
import com.example.wanghanp.losephone.service.SaveStateService;
import com.example.wanghanp.losephone.service.SlideSettings;
import com.example.wanghanp.losephone.tabview.SettingFragment;
import com.example.wanghanp.losephone.tabview.ShowFunFragment;
import com.example.wanghanp.myview.ShowPhotosActivity;
import com.example.wanghanp.myview.ZoomImageView;
import com.example.wanghanp.permissioncheck.PermissionsActivity;
import com.example.wanghanp.receiver.ScreenListener;
import com.example.wanghanp.receiver.SensorListener;
import com.example.wanghanp.util.MobileInfo;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener,
        SensorListener.PowerConnectionListener, CameraManager.RemoveMoreFilesListener,UploadContract.UploadView, AMapLocationListener,LocationSource {

    private SlideSettings mSlideSetting;
    private float[] gravity = new float[3];   //重力在设备x、y、z轴上的分量
    private float[] motion = new float[3];  //过滤掉重力后，加速度在x、y、z上的分量
    private double ratioY;
    private double angle;
    private int counter = 1;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mShake = false;
    private int mLockScreenState;
    private int mBatteryStatus = -1;
    private SensorListener mSensorLisener;
    private int mPeaceCount;
    private ScreenListener mScreenStateListener;

    private SurfaceView mCameraView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mFrontCamera;
    private CameraManager mCameramanager;
    private boolean mHasFocus = true;
    private List<TakePhotoBean> mPath;
    public ArrayList<String> mList;
    @InjectView(R.id.footer_rb_mine)
    TextView footer_mine;
    @InjectView(R.id.footer_rb_msg)
    TextView footer_msg;
//    @InjectView(R.id.container)
//    FrameLayout mFrameLayout;
    private boolean mShowSafeBt = false;
    private boolean mRequiresCheck;
    
    private boolean mCanTakePhoto;
    private MapViewFragment mMapViewFragment;
    private FragmentTransaction mFragmentTransaction;
    private int mBackUpTime = 50;
    private int mLocakScreenOriState;
    private int mSpanCount = 10;
    private ShowFunFragment mShowFunFragment;
    private SettingFragment mSettingFragment;
    private boolean mIsServiceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mCameraView = (SurfaceView) findViewById(R.id.back_surfaceview);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initBroadCast();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSlideSetting = SlideSettings.getInstance(MainActivity.this);

        Intent intent = new Intent(MainActivity.this, PermissionsActivity.class);
        intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, new String[] { Manifest.permission.CAMERA ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.READ_EXTERNAL_STORAGE});
        ActivityCompat.startActivityForResult(MainActivity.this, intent, 102, null);
        //do
        registerTimeBroadCast();
    }

    private void registerTimeBroadCast() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        MyTimeTickBroaCast myTimeTickBroadCast = new MyTimeTickBroaCast();
        getApplicationContext().registerReceiver(myTimeTickBroadCast,intentFilter);
    }

    private void initFragment2() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
        if (mShowFunFragment == null) {
            mShowFunFragment = ShowFunFragment.newInstance(mPath,"");
            transaction.add(R.id.content, mShowFunFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(mShowFunFragment);
        //提交事务
        transaction.commit();
    }
    private void initFragment3() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
        if (mSettingFragment == null) {
            mSettingFragment = new SettingFragment();
            transaction.add(R.id.content, mSettingFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(mSettingFragment);
        //提交事务
        transaction.commit();
    }
    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction){

        if(mShowFunFragment != null){
            transaction.hide(mShowFunFragment);
        }
        if(mSettingFragment !=null){
            transaction.hide(mSettingFragment);
        }
    }
    @OnClick({ R.id.footer_rb_msg, R.id.footer_rb_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.footer_rb_msg:
//                footer_msg.setSelected(true);
//                footer_mine.setSelected(false);
                initFragment2();
                break;
            case R.id.footer_rb_mine:
//                footer_msg.setSelected(false);
//                footer_mine.setSelected(true);
                initFragment3();
                break;
        }
    }

    @Override
    public void removeMoreSuccessListener() {
        mPath = mCameramanager.getTakePhotosList();
        initTakePhotosAdpater(mPath);
    }

    private void initTakePhotosAdpater(List<TakePhotoBean> mPath) {
        if (mShowFunFragment != null && mShowFunFragment.isAdded() && mPath.size() > 0) {
            mShowFunFragment.initTakePhotosAdpater(mPath);
        }
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void showInfo(String info) {

    }

    @Override
    public ArrayList<String> getImageList() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }


    private void initCamera() {
        mSurfaceHolder = mCameraView.getHolder();

        mCameramanager = new CameraManager(this, mFrontCamera, mSurfaceHolder, new CameraManager.TakePhotosListener() {
            @Override
            public void takePhotosSuccessListener(File file) {

                Log.d("wanghp007", "takePhotosSuccessListener: file" + file.exists() + "path == " + file.getAbsolutePath());
                if (mPath == null) {
                    mPath = new ArrayList<>();
                }
                TakePhotoBean takePhotoBean = new TakePhotoBean();
                takePhotoBean.setPath(file.getAbsolutePath());
                mPath.add(takePhotoBean);
                if (mPath.size() > mSpanCount ) {
                    mPath.remove(0);
                }
                initTakePhotosAdpater(mPath);
                mCanTakePhoto = true;
            }
        });
        mCameramanager.setmRemoveMoreFileListener(this);
        mPath = new ArrayList<>();
        ArrayList<TakePhotoBean> photos = mCameramanager.getTakePhotosList();
        for (int i = photos.size() - 1; i >= 0; i--) {
            String path = photos.get(i).getPath();
            if (new File(path).exists()) {
                mPath.add(photos.get(i));
            }
        }
        initTakePhotosAdpater(mPath);
        initFragment2();
    }

    private void takeFrontPhoto2() {
        if (mCameramanager.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            mFrontCamera = mCameramanager.getCamera();
            //自动对焦
            if (mHasFocus) {
                try {
                    mFrontCamera.autoFocus(mAutoFocus);
                } catch (RuntimeException e) {
                    mCameramanager.ondestroy();
                    Log.d("wanghp007", "takeFrontPhoto2: e == " + e);
                }
            }
            TakePhotosAsncTask(3);
        }
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

//        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        mSensorManager.registerListener(listener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

//    private SensorEventListener listener=new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            float value=event.values[0];
//            Toast.makeText(getApplicationContext(), "value == "+value, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };

    private void initBroadCast() {
        mSensorLisener = new SensorListener(MainActivity.this, this);
        mSensorLisener.registerPowerConnection();
        mScreenStateListener = new ScreenListener(this);
        getScreenState();
        mScreenStateListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.d("wanghp007", "onScreenOn: ");
                mLockScreenState = 0;
                mBackUpTime = 10;
                keepLive();
            }

            @Override
            public void onScreenOff() {
                Log.d("wanghp007", "onScreenOff: ");
                mLockScreenState = 1;
                mBackUpTime = 15;
            }

            @Override
            public void onUserPresent() {
                Log.d("wanghp007", "onUserPresent: ");
                mLockScreenState = 2;
                mBackUpTime = 10;

            }

            @Override
            public void onUserUnlock() {
                Log.d("wanghp007", "onUserUnlock: ");
                mLockScreenState = 3;
                mBackUpTime = 15;
            }
        });
    }

    private void getScreenState() {
        PowerManager manager = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            mLocakScreenOriState = 0;
        } else {
            mLocakScreenOriState = -1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequiresCheck) {
            mHasFocus = true;
            mCameramanager.setmSafeTakePhotos(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequiresCheck) {
            try {
                mSensorLisener.unRegisterPowerConnection();
                mSensorManager.unregisterListener(this);
//        mSensorManager.unregisterListener(listener);
                if (mSlideSetting != null) {
                    mSlideSetting.playWeakenMusic(MainActivity.this);
                }
                if (mScreenStateListener != null) {
                    mScreenStateListener.unregisterListener();
                }
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        if (mCameramanager != null) {
            mCameramanager.ondestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHasFocus = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("wanghp008", "onSensorChanged: ");
        if (!mShowSafeBt) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
        }
        if ((event.sensor.getType() == Sensor.TYPE_GYROSCOPE || event.sensor.getType() == Sensor.TYPE_ORIENTATION || event.sensor.getType() == Sensor.TYPE_GRAVITY)) {
//            showInfo("事件：" + " x:" + sensorEvent.values[0] + " y:" + sensorEvent.values[1]  + " z:" + sensorEvent.values[2]);
            for (int i = 0; i < 3; i++) {
        /* accelermeter是很敏感的，看之前小例子的log就知道。因为重力是恒力，我们移动设备，它的变化不会太快，不象摇晃手机这样的外力那样突然。因此通过low-pass filter对重力进行过滤。这个低通滤波器的权重，我们使用了0.1和0.9，当然也可以设置为0.2和0.8。 */
                gravity[i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]);
                motion[i] = event.values[i] - gravity[i];
            }

            //计算重力在Y轴方向的量，即G*cos(α)
            ratioY = gravity[1] / SensorManager.GRAVITY_EARTH;
            if (ratioY > 1.0)
                ratioY = 1.0;
            if (ratioY < -1.0)
                ratioY = -1.0;
            //获得α的值，根据z轴的方向修正其正负值。
            angle = Math.toDegrees(Math.acos(ratioY));
            if (gravity[2] < 0)
                angle = -angle;

            //避免频繁扫屏，每10次变化显示一次值

            if (counter++ < 30) {
                return;
            }
            counter = 0;
            if ((event.values[0] > 0.1) || (event.values[1] > 0.1) || (event.values[2] > 0.1)
                    || (gravity[0] > 0.1) || (gravity[1] > 0.1) || (gravity[2] > 0.1)
                    || (motion[0] > 0.1) || (motion[1] > 0.1) || (motion[2] > 0.1)) {
                if (mSlideSetting != null) {
                    if (mLockScreenState == 2 && mBatteryStatus == 4) {
                        // do user operation
                    } else {
                        Log.d("wanghp008", "onSensorChanged: mSlideSetting.isPlayerPlaying():" + mSlideSetting.isPlayerPlaying() + "mCameramanager.ismSafeTakePhotos():" + mCameramanager.ismSafeTakePhotos());
                        if (!mSlideSetting.isPlayerPlaying() && mCameramanager.ismSafeTakePhotos()) {
                            mCameramanager.setmSafeTakePhotos(false);
                            takeFrontPhoto2();
                            mSlideSetting.playEnhancementMusic(MainActivity.this);
                        }
                    }
                }
                mShake = true;
                if (mPeaceCount > 0) {
                    mPeaceCount = 0;
                }
            } else {
                if (mShake) {
                    mPeaceCount++;
                }
            }
            if (mPeaceCount == 12) {
                handler.removeCallbacks(task);
                handler.post(task);
                mPeaceCount = 0;
            }
        }
    }

    int count = 0;

    private void TakePhotosAsncTask(final int takeTimes) {
        mCanTakePhoto = true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                count = 0;
                int time = 500;
                while (count <= takeTimes) {
                    if (count == takeTimes && mCameramanager != null) {
                        mCameramanager.ondestroy();
                        mCameramanager.setmSafeTakePhotos(true);
                        count = 0;
                        break;
                    }
                    if (mCanTakePhoto) {
                        try {
                            mFrontCamera.startPreview();
                            mFrontCamera.takePicture(null, null, mCameramanager.new PicCallback(mFrontCamera));
                        } catch (RuntimeException e) {
                            Log.d("wanghp007", "run2: e = " + e);
                        }
//
                        mCanTakePhoto = false;
                        count++;
                        time = 100;
                    } else {
                        time = 3000;
                    }
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mSlideSetting != null) {
                mSlideSetting.playWeakenMusic(MainActivity.this);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void call(int powerStatus) {
        if (mBatteryStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING && mBatteryStatus
                != -1) {
            mPeaceCount = 0;
        }
//        Toast.makeText(this,"status == " +powerStatus,Toast.LENGTH_SHORT).show();
        mBatteryStatus = powerStatus;
    }

    /**
     * 自动对焦的回调方法，用来处理对焦成功/不成功后的事件
     */
    private Camera.AutoFocusCallback mAutoFocus = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //TODO:空实现
            Log.d("wanghp007", "onAutoFocus:success = " +success );
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            mRequiresCheck = true;
            initSensor();
            initCamera();
            startService(new Intent(MainActivity.this, SaveStateService.class));
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.d("wanghp009", "onLocationChanged: ");
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                Log.d("wanghp009", "onLocationChanged: "+amapLocation.getLatitude());
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.d("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private class MyTimeTickBroaCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_TIME_TICK) {
                Log.d("wanghp007", "Intent.ACTION_TIME_TICK " );
                keepLive();
            }
        }
    }

    private void keepLive() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningService:
                lists) {
            if (runningService.service.getClassName().equals("com.example.wanghanp.losephone.service.SaveStateService")) {
                Toast.makeText(getApplicationContext(),"Time_tick",Toast.LENGTH_SHORT).show();
                mIsServiceRunning = true;
            } else {
                mIsServiceRunning = false;
            }
            Log.d("wanghp007", "onReceive: mIsServiceRunning = " +mIsServiceRunning);
            if (!mIsServiceRunning) {
                startService(new Intent(MainActivity.this, SaveStateService.class));
            }
        }
    }
}
