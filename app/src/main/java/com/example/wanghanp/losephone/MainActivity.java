package com.example.wanghanp.losephone;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.Shake.contract.adapter.BannerAdapter;
import com.example.wanghanp.losephone.camera.CameraManager;
import com.example.wanghanp.losephone.playService.SlideSettings;
import com.example.wanghanp.receiver.ScreenListener;
import com.example.wanghanp.receiver.SensorListener;
import com.example.wanghanp.util.MobileInfo;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener,SensorListener.PowerConnectionListener, CameraManager.RemoveMoreFilesListener {

    private SlideSettings mSlideSetting;
    private float[] gravity = new float[3];   //重力在设备x、y、z轴上的分量
    private float[] motion = new float[3];  //过滤掉重力后，加速度在x、y、z上的分量
    private double ratioY;
    private double angle;
    private int counter = 1;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int mBackUpTime = 50;
    private boolean mShake  = false;
    private int mLockScreenState;
    private int mBatteryStatus = -1;
    private int mLocakScreenOriState ;
    private SensorListener mSensorLisener;
    private int mPeaceCount;
    private ScreenListener mScreenStateListener;

    private SurfaceView mCameraView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mFrontCamera;
    private CameraManager mCameramanager;
    private boolean mHasFocus = true;
    private List<TakePhotoBean> mPath;

    @InjectView(R.id.viewpager)
    public ViewPager mViewPager;
    public List<ImageView> mlist;
    @InjectView(R.id.tv_bannertext)
    public TextView mTextView;
    @InjectView(R.id.points)
    public LinearLayout mLinearLayout;
    @InjectView(R.id.recyclerview_takephotos)
    public RecyclerView mTakePhotoRecyclerView;

    public int mSpanCount = 5;
    private CommonAdapter<TakePhotoBean> mCommonAdapter;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mCameraView = (SurfaceView) findViewById(R.id.back_surfaceview);
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
        initSensor();
        initCamera();
        initData();
        initRecyclerView();
        //do
    }

    private void initRecyclerView() {
        mTakePhotoRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        mTakePhotoRecyclerView.setLayoutManager(new GridLayoutManager(this,mSpanCount));//设置为listview的布局
        mTakePhotoRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置动画
//        mTakePhotoRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//添加分割线
    }

    private void initTakePhotosAdpater(List<TakePhotoBean> path) {
        mCommonAdapter = new CommonAdapter<TakePhotoBean>(MainActivity.this,R.layout.list_item_takephotos,path) {
            @Override
            protected void convert(ViewHolder holder, TakePhotoBean takePhotoBean, int position) {
                ImageView img = holder.getView(R.id.iv_takephotos);
                img.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth / mSpanCount,mScreenWidth / mSpanCount));
                Glide.with(mContext).load(takePhotoBean.getPath())
                .override(mScreenWidth / mSpanCount,mScreenWidth / mSpanCount)
                        .into(img);
            }
        };
        Log.d("wanghp0077", "mTakePhotoRecyclerView== null: "+(mTakePhotoRecyclerView == null));
        mTakePhotoRecyclerView.setAdapter(mCommonAdapter);
    }

    // 广告图素材
    private int[] bannerImages = {R.mipmap.image1, R.mipmap.image2, R.mipmap.image3, R.mipmap.image1};
    // 广告语
    private String[] bannerTexts = {"因为因为 所以所以", "莫道桑榆晚，微霞尚满天", "心平则智", "精细 和谐 大气 开放"};

    // ViewPager适配器与监听器
    private BannerAdapter mAdapter;
    private BannerListener bannerListener;

    // 圆圈标志位
    private int pointIndex = 0;
    // 线程标志
    private boolean isStop = false;

    /**
     * 初始化数据
     */
    private void initData() {
        mScreenWidth = MobileInfo.getInstance(MainActivity.this).getScreenWidth();
        mlist = new ArrayList<ImageView>();
        View view;
        LinearLayout.LayoutParams params;
        for (int i = 0; i < bannerImages.length; i++) {
            // 设置广告图
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(bannerImages[i]);
            mlist.add(imageView);
            // 设置圆圈点
            view = new View(this);
            params = new LinearLayout.LayoutParams(5, 5);
            params.leftMargin = 10;
            view.setBackgroundResource(R.drawable.banner_circle_unchecked);
            view.setLayoutParams(params);
            view.setEnabled(false);
            mLinearLayout.addView(view);
        }
        mAdapter = new BannerAdapter(mlist);
        bannerListener = new BannerListener();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(bannerListener);
    }

    @Override
    public void removeMoreSuccessListener() {
        mPath = mCameramanager.getTakePhotosList();
        initTakePhotosAdpater(mPath);
    }


    //实现VierPager监听器接口
    class BannerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            int newPosition = position % bannerImages.length;
            mTextView.setText(bannerTexts[newPosition]);
            mLinearLayout.getChildAt(newPosition).setEnabled(true);
            mLinearLayout.getChildAt(pointIndex).setEnabled(false);
            // 更新标志位
            pointIndex = newPosition;
        }

    }

    private void initCamera() {
        mSurfaceHolder = mCameraView.getHolder();

        mCameramanager = new CameraManager(this,mFrontCamera, mSurfaceHolder, new CameraManager.TakePhotosListener() {
            @Override
            public void takePhotosSuccessListener(File file) {
                    Log.d("wanghp007", "takePhotosSuccessListener: file"+file.exists()+"path == " +file.getAbsolutePath());
                if (mPath == null) {
                    mPath = new ArrayList<>();
                }
                TakePhotoBean takePhotoBean = new TakePhotoBean();
                takePhotoBean.setPath(file.getAbsolutePath());
                mPath.add(takePhotoBean);
                if (mPath.size() > mSpanCount * 2) {
                    mPath.remove(0);
                }
                initTakePhotosAdpater(mPath);
            }
        });
        mCameramanager.setmRemoveMoreFileListener(this);
        mPath = mCameramanager.getTakePhotosList();
        initTakePhotosAdpater(mPath);
    }

    private void takeFrontPhoto2() {
        if ( mCameramanager.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            mFrontCamera = mCameramanager.getCamera();
            //自动对焦
            if (mHasFocus) {
                try {
                    mFrontCamera.autoFocus(mAutoFocus);
                } catch (RuntimeException e) {
                    Log.d("wanghp007", "takeFrontPhoto2: e == " +e);
                }
            }
            // 拍照
            handler.removeCallbacks(takeTask);
            handler.postDelayed(takeTask,500);
        }
    }

    Runnable takeTask = new Runnable() {
        @Override
        public void run() {
            mFrontCamera.takePicture(null, null, mCameramanager.new PicCallback(mFrontCamera));
        }
    };

    private void initSensor() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void initBroadCast() {
        mSensorLisener = new SensorListener(MainActivity.this,this);
        mSensorLisener.registerPowerConnection();
        mScreenStateListener = new ScreenListener(this);
        getScreenState();
        mScreenStateListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.d("wanghp007", "onScreenOn: ");
                mLockScreenState = 0;
                mBackUpTime = 10;
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
        mHasFocus = true;
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
        mSensorLisener.unRegisterPowerConnection();
        if (mSlideSetting != null) {
            mSlideSetting.playWeakenMusic(MainActivity.this);
        }
        if (mScreenStateListener != null) {
            mScreenStateListener.unregisterListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHasFocus = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
//            Log.d("wanghp008","TYPE_GYROSCOPE");
//            showInfo("事件：" + " x:" + sensorEvent.values[0] + " y:" + sensorEvent.values[1]  + " z:" + sensorEvent.values[2]);
        }

        for(int i = 0 ; i < 3; i ++){
        /* accelermeter是很敏感的，看之前小例子的log就知道。因为重力是恒力，我们移动设备，它的变化不会太快，不象摇晃手机这样的外力那样突然。因此通过low-pass filter对重力进行过滤。这个低通滤波器的权重，我们使用了0.1和0.9，当然也可以设置为0.2和0.8。 */
            gravity[i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]);
            motion[i] = event.values[i] - gravity[i];
        }

        //计算重力在Y轴方向的量，即G*cos(α)
        ratioY = gravity[1]/ SensorManager.GRAVITY_EARTH;
        if(ratioY > 1.0)
            ratioY = 1.0;
        if(ratioY < -1.0)
            ratioY = -1.0;
        //获得α的值，根据z轴的方向修正其正负值。
        angle = Math.toDegrees(Math.acos(ratioY));
        if(gravity[2] < 0)
            angle = - angle;

        //避免频繁扫屏，每10次变化显示一次值
        Log.d("wanghp008", "onSensorChanged: counter ++ == " +counter +++"&&mBackUpTime = " +mBackUpTime+"&& % = " +counter ++ % mBackUpTime);
        if(counter ++ % mBackUpTime == 0){
            counter = 1;
            Log.d("wanghp008", "onSensorChanged_counter == " +counter);
            if ((event.values[0] > 0.1) || (event.values[1] > 0.1) || (event.values[2] > 0.1)
                    || (gravity[0] > 0.1) || (gravity[1] > 0.1) || (gravity[2] > 0.1)
                    || (motion[0] > 0.1 ) || (motion[1] > 0.1) || (motion[2] > 0.1)) {
                if (mSlideSetting != null) {
                    if (mLockScreenState == 2 && mBatteryStatus == 4){
                        Log.d("wanghp007", "onSensorChanged: unTakePhotos");
                        // do user operation
                    } else {
                        Log.d("wanghp007", "onSensorChanged: TakePhotos");
                        if (!mSlideSetting.isPlayerPlaying() && mCameramanager.ismSafeTakePhotos()) {
                            Log.d("wanghp007", "onSensorChanged: mSlideSetting.isPlayerPlaying():"+mSlideSetting.isPlayerPlaying());
                            mSlideSetting.playEnhancementMusic(MainActivity.this);
                            mCameramanager.setmSafeTakePhotos(false);
                            takeFrontPhoto2();
                        }
                    }
                }
                mShake = true;
                if (mPeaceCount > 0) {
                    mPeaceCount = 0;
                }
            } else {
                if (mShake) {
                    mPeaceCount ++ ;
                }
            }
            if (mPeaceCount == 8) {
                handler.removeCallbacks(task);
                handler.post(task );
                mPeaceCount = 0;
            }
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mSlideSetting != null) {
                mSlideSetting.playWeakenMusic(MainActivity.this);
            }
        }
    };

    private Handler handler = new Handler(){
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
                != -1){
            mPeaceCount = 0;
        }
//        Toast.makeText(this,"status == " +powerStatus,Toast.LENGTH_SHORT).show();
        mBatteryStatus = powerStatus;
    }

    /**
     * 自动对焦的回调方法，用来处理对焦成功/不成功后的事件
     */
    private Camera.AutoFocusCallback mAutoFocus =  new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //TODO:空实现
        }
    };

}
