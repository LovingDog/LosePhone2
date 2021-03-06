package com.example.wanghanp.losephone;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wanghanp.alarm.AlarmInfo;
import com.example.wanghanp.alarm.AlarmInfoDao;
import com.example.wanghanp.alarm.ScreenBroadcastListener;
import com.example.wanghanp.alarm.ScreenManager;
import com.example.wanghanp.baiduspeak.SpeakUtils;
import com.example.wanghanp.base.UploadContract;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.base.interfacelistener.ContentUpdatable;
import com.example.wanghanp.base.interfacelistener.OnServiceConnect;
import com.example.wanghanp.base.interfacelistener.OnUpdateStatusChanged;
import com.example.wanghanp.db.DBMusicocoController;
import com.example.wanghanp.losephone.aidl.IPlayControl;
import com.example.wanghanp.losephone.camera.CameraManager;
import com.example.wanghanp.losephone.manager.BroadcastManager;
import com.example.wanghanp.losephone.manager.MediaManager;
import com.example.wanghanp.losephone.manager.PlayServiceManager;
import com.example.wanghanp.losephone.music.service.play.PlayServiceConnection;
import com.example.wanghanp.losephone.tabview.BlankFragment;
import com.example.wanghanp.losephone.tabview.SettingFragment;
import com.example.wanghanp.music.controller.BottomNavigationController;
import com.example.wanghanp.permissioncheck.PermissionsActivity;
import com.example.wanghanp.permissioncheck.PermissionsChecker;
import com.example.wanghanp.receiver.ScreenListener;
import com.example.wanghanp.receiver.SensorListener;
import com.example.wanghanp.service.LiveService;
import com.example.wanghanp.service.LongRunningService;
import com.example.wanghanp.util.ConsUtils;
import com.example.wanghanp.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener,
        SensorListener.PowerConnectionListener, CameraManager.RemoveMoreFilesListener, UploadContract.UploadView, OnServiceConnect ,ContentUpdatable,View.OnClickListener {

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
    @InjectView(R.id.iv_add)
    ImageView mAddClock;
    //    @InjectView(R.id.container)
//    FrameLayout mFrameLayout;
    public boolean mShowSafeBt = false;

    private boolean mCanTakePhoto;
    private int mBackUpTime = 50;
    private int mLocakScreenOriState;
    private int mSpanCount = 10;
    private BlankFragment mShowFunFragment;
    private SettingFragment mSettingFragment;
    private boolean mIsServiceRunning;
    private PermissionsChecker mChecker;
    private PlayServiceManager playServiceManager;
    private MediaManager mediaManager;
    private BroadcastManager broadcastManager;
    private BottomNavigationController bottomNavigationController;
    private PlayServiceConnection sServiceConnection;
    protected IPlayControl control;
    private DBMusicocoController dbController;
    private double longtitude;
    private double latitude;
    private SpeakUtils mSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeak =  new SpeakUtils(this);

//        listener = new NotiftLocationListener();
//        mLocationClient.registerLocationListener(listener);
        AlarmInfoDao alarmInfoDao = new AlarmInfoDao(MainActivity.this);
        for (AlarmInfo alarmInfo1:alarmInfoDao.getAllInfo()) {
            if (alarmInfo1.getLocation() != null) {
                longtitude = Double.parseDouble(alarmInfo1.getLat());
                latitude = Double.parseDouble(alarmInfo1.getLon());
                break;
            }
        }

        Log.d("alarm", "onCreate: latitude = "+latitude+"-"+longtitude);
//        mNotifyLister.SetNotifyLocation(latitude,longtitude, 40000,mLocationClient.getLocOption().getCoorType());//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
//        mLocationClient.start();

        //注册监听函数
//        mLocationClient.registerNotify(mBdNotifyListener);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mCameraView = (SurfaceView) findViewById(R.id.back_surfaceview);
        playServiceManager = new PlayServiceManager(this);
        mediaManager = MediaManager.getInstance();

        // 单例持有的 Context 为 BaiduSpeakMainActivity 的，最早调用在此。
        broadcastManager = BroadcastManager.getInstance();
        bottomNavigationController = new BottomNavigationController(this, mediaManager);
        dbController = new DBMusicocoController(this, true);
        bottomNavigationController.initView();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        initBroadCast();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        initCamera();
        bindService();
//        initSensor();
        registerTimeBroadCast();
    }

    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerTimeBroadCast() {
//        initLiveActivity();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        MyTimeTickBroaCast myTimeTickBroadCast = new MyTimeTickBroaCast();
        getApplicationContext().registerReceiver(myTimeTickBroadCast, intentFilter);
        if (!isNotificationListenerEnabled(this)) {
            openNotificationListenSettings();
        }
        startService(new Intent(this, LiveService.class));
//        initLiveActivity();
    }

    private void initLiveActivity(){
        final ScreenManager screenManager = ScreenManager.getInstance(MainActivity.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                screenManager.finishActivity();
            }

            @Override
            public void onScreenOff() {
                screenManager.startActivity();
            }
        });
    }

    private void initFragment2() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
        if (mShowFunFragment == null) {
            mShowFunFragment = BlankFragment.newInstance("", "", new BlankFragment.OnbackPressListener() {
                @Override
                public void onbackPress() {

                }
            });
            transaction.add(R.id.content, mShowFunFragment);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(mShowFunFragment);
        //提交事务
        transaction.commit();
    }

    public void speak(){
        mSpeak.speak("小米之家 欢迎你");
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
    private void hideFragment(FragmentTransaction transaction) {

        if (mShowFunFragment != null) {
            transaction.hide(mShowFunFragment);
        }
        if (mSettingFragment != null) {
            transaction.hide(mSettingFragment);
        }
    }

    @OnClick({R.id.footer_rb_msg, R.id.footer_rb_mine,R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.footer_rb_msg:
//                footer_msg.setSelected(true);
//                footer_mine.setSelected(false);
                initFragment3();
                break;
            case R.id.footer_rb_mine:
//                footer_msg.setSelected(false);
//                footer_mine.setSelected(true);
                initFragment2();
                break;
            case R.id.iv_add:
                if (mSettingFragment != null) {
                    mSettingFragment.onClick();
                }
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
//            mShowFunFragment.initTakePhotosAdpater(mPath);
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

    private void bindService() {

        sServiceConnection = new PlayServiceConnection(bottomNavigationController, this, this);
        // 绑定成功后回调 onConnected
        playServiceManager.bindService(sServiceConnection);

    }

    private void unbindService() {
        if (sServiceConnection != null && sServiceConnection.hasConnected) {
            sServiceConnection.unregisterListener();
            unbindService(sServiceConnection);
            sServiceConnection.hasConnected = false;
        }
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
                if (mPath.size() > mSpanCount) {
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
        initFragment3();
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
    }

    private void initBroadCast() {
        mSensorLisener = new SensorListener(MainActivity.this, this);
        mSensorLisener.registerPowerConnection();
        mScreenStateListener = new ScreenListener(this);
        getScreenState();
        mScreenStateListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                mLockScreenState = 0;
                mBackUpTime = 10;
                keepLive();
            }

            @Override
            public void onScreenOff() {
                mLockScreenState = 1;
                mBackUpTime = 15;
            }

            @Override
            public void onUserPresent() {
                mLockScreenState = 2;
                mBackUpTime = 10;

            }

            @Override
            public void onUserUnlock() {
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
        if (bottomNavigationController != null && bottomNavigationController.hasInitData()) {
            // 启动应用时不需要更新，在 initSelfData 中统一更新全部
            bottomNavigationController.update(null, null);
        }
        mHasFocus = true;
//        mCameramanager.setmSafeTakePhotos(true);
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
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        ArrayList<AlarmInfo> list = getAllAlarmLocationInfo();
//        for (AlarmInfo alarminfo :
//                list) {
//            if (StringUtils.isReal(alarminfo.getLocation())){
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("location_alarminfo", alarminfo);
//                bundle.setClassLoader(AlarmInfo.class.getClassLoader());
                Intent intent = new Intent(getApplicationContext(),LongRunningService.class);
                startService(intent);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                return;
//            }
//        }

        super.onDestroy();
        try {
//            mSensorLisener.unRegisterPowerConnection();
//            mSensorManager.unregisterListener(this);
//        mSensorManager.unregisterListener(listener);
          bottomNavigationController.paly(false);
            if (mScreenStateListener != null) {
                mScreenStateListener.unregisterListener();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
        if (mCameramanager != null) {
            mCameramanager.ondestroy();
        }
        unbindService();
    }

    private ArrayList<AlarmInfo> getAllAlarmLocationInfo(){
        AlarmInfoDao alarmInfoDao = new AlarmInfoDao(this);
        ArrayList<AlarmInfo> list = (ArrayList<AlarmInfo>) alarmInfoDao.getAllInfo();
        ArrayList<AlarmInfo> locationList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (AlarmInfo alarminfo :
                    list) {
                if (StringUtils.isReal(alarminfo.getLocation())) {
                    locationList.add(alarminfo);
                }
            }
        }
        return locationList;
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
                    if (mLockScreenState == 2 && mBatteryStatus == 4) {
                        // do user operation
                    } else {
                         if (!bottomNavigationController.checked() && mCameramanager.ismSafeTakePhotos()) {
                            mCameramanager.setmSafeTakePhotos(false);
                            takeFrontPhoto2();
//                             bottomNavigationController.paly(false);
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
//            bottomNavigationController.paly(false);
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
            Log.d("wanghp007", "onAutoFocus:success = " + success);
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
        Log.d("alarm","onActivityResult.ring = ");

        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 102 && resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            initSensor();
            initCamera();
            registerTimeBroadCast();
            bindService();
        }else if (requestCode == ConsUtils.SET_ALARM_DONE) {
            Bundle bundle=data.getExtras();
            if (bundle != null) {
                AlarmInfo alarmInfo=(AlarmInfo)bundle.getSerializable("alarm");
                Log.d("alarm","onActivityResult.ring = "+alarmInfo.getRing());
                mSettingFragment.addAlarmInfo(true);
            }
        }else if (requestCode == ConsUtils.SET_LOCATION_ALARM_DONE) {
            Bundle bundle=data.getExtras();
            Log.d("wanghp", "onActivityResult: bundle = "+bundle);
            mSettingFragment.addAlarmInfo(false);
            if (bundle != null) {
                AlarmInfo alarmInfo=(AlarmInfo)bundle.getSerializable("alarm");
                longtitude = Double.parseDouble(alarmInfo.getLon());
                latitude = Double.parseDouble(alarmInfo.getLat());
                Bundle bundle1=new Bundle();
                bundle1.putSerializable("location_alarminfo", alarmInfo);
                bundle1.setClassLoader(AlarmInfo.class.getClassLoader());
                Intent intent = new Intent(this,LongRunningService.class);
                intent.putExtra("bundle",bundle1);
                startService(intent);
            }
        }
    }

    @Override
    public void onConnected(ComponentName name, IBinder service) {
        this.control = IPlayControl.Stub.asInterface(service);
        initSelfData();
    }

    private void initSelfData() {
        bottomNavigationController.initData(control, dbController);
    }

    @Override
    public void disConnected(ComponentName name) {
        sServiceConnection = null;
        sServiceConnection = new PlayServiceConnection(bottomNavigationController, this, this);
        // 重新绑定
        playServiceManager.bindService(sServiceConnection);
    }

    @Override
    public void update(Object obj, OnUpdateStatusChanged statusChanged) {
        bottomNavigationController.update(obj, statusChanged);
    }

    @Override
    public void noData() {

    }

    private class MyTimeTickBroaCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_TIME_TICK) {
                Log.d("wanghp007", "Intent.ACTION_TIME_TICK ");
                keepLive();
            }
        }
    }

    private void keepLive() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> lists = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningService :
                lists) {
            if (runningService.service.getClassName().equals("com.example.wanghanp.service.LongRunningService")) {
                mIsServiceRunning = true;
            } else {
                mIsServiceRunning = false;
            }
            Log.d("alarm", "onReceive: mIsServiceRunning = " +mIsServiceRunning+" &&name = "+runningService.service.getClassName());
//            Toast.makeText(getApplicationContext(),"Time_tick_mIsServiceRunning:"+mIsServiceRunning,Toast.LENGTH_SHORT).show();
            if (!mIsServiceRunning) {
                startService(new Intent(MainActivity.this, LongRunningService.class));
            }
        }
    }
    public void play() {
        bottomNavigationController.paly(false);
    }

    public void pause() {
        bottomNavigationController.pause();
    }

    public boolean isCheck() {
        return bottomNavigationController.checked();
    }


}
