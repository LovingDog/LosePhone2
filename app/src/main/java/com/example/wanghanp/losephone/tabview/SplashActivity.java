package com.example.wanghanp.losephone.tabview;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.wanghanp.base.ActivityManager;
import com.example.wanghanp.db.DBMusicocoController;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.aidl.Song;
import com.example.wanghanp.losephone.manager.MediaManager;
import com.example.wanghanp.losephone.manager.PlayServiceManager;
import com.example.wanghanp.losephone.service.SaveStateService;
import com.example.wanghanp.permissioncheck.PermissionsActivity;
import com.example.wanghanp.permissioncheck.PermissionsChecker;
import com.example.wanghanp.util.MediaUtils;
import com.example.wanghanp.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class SplashActivity extends Activity {

    private TextView[] ts;

    private View container;
    private boolean animComplete;
    private MediaManager mediaManager;
    private DBMusicocoController dbController;
    private PermissionsChecker mChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (ActivityManager.getInstance().getActivity(SplashActivity.class.getName()) != null) {
//            // 应用已经启动并未被杀死，直接启动 BaiduSpeakMainActivity
//            startMainActivity();
//            return;
//        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
//        checkPermission();
        initCheckPermission();
//        ActivityManager.getInstance().addActivity(SplashActivity);
    }


    private void initCheckPermission() {
        mChecker = new PermissionsChecker(this);
        String[] graint = new String[] { Manifest.permission.CAMERA ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WAKE_LOCK};
        if (mChecker.lacksPermissions(graint)) {
            Intent intent = new Intent(SplashActivity.this, PermissionsActivity.class);
            intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, graint);
            ActivityCompat.startActivityForResult(SplashActivity.this, intent, 102, null);
        }else {
            initCreateView();
        }
    }

    private void initCreateView() {

        initViews();
        mediaManager = MediaManager.getInstance();
        dbController = new DBMusicocoController(this, true);
        initDataAndStartService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            initCreateView();
        }
    }


    private void initViews() {
        container = findViewById(R.id.splash_container);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{
                        getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.colorPrimaryDark)
                });
//        container.setBackground(gd);
        container.setClickable(false);

        ts = new TextView[]{
                (TextView) findViewById(R.id.splash_m),
                (TextView) findViewById(R.id.splash_u),
                (TextView) findViewById(R.id.splash_s),
                (TextView) findViewById(R.id.splash_i),
                (TextView) findViewById(R.id.splash_c),
                (TextView) findViewById(R.id.splash_o),
                (TextView) findViewById(R.id.splash_c1),
                (TextView) findViewById(R.id.splash_o1),
                (TextView) findViewById(R.id.splash_e1)
        };
        ts[0].post(new Runnable() {
            @Override
            public void run() {
                for (TextView t : ts) {
                    t.setVisibility(View.VISIBLE);
                    startTextInAnim(t);
                }
            }
        });
    }

    private void startTextInAnim(TextView t) {
        Random r = new Random();
        DisplayMetrics metrics = Utils.getMetrics(this);
        int x = r.nextInt(metrics.widthPixels * 4 / 3);
        int y = r.nextInt(metrics.heightPixels * 4 / 3);
        float s = r.nextFloat() + 4.0f;
        ValueAnimator tranY = ObjectAnimator.ofFloat(t, "translationY", y - t.getY(), 0);
        ValueAnimator tranX = ObjectAnimator.ofFloat(t, "translationX", x - t.getX(), 0);
        ValueAnimator scaleX = ObjectAnimator.ofFloat(t, "scaleX", s, 1.0f);
        ValueAnimator scaleY = ObjectAnimator.ofFloat(t, "scaleY", s, 1.0f);
        ValueAnimator alpha = ObjectAnimator.ofFloat(t, "alpha", 0.0f, 1.0f);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(1800);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.play(tranX).with(tranY).with(scaleX).with(scaleY).with(alpha);
        if (t == findViewById(R.id.splash_e1)) {
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startFinalAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    startFinalAnim();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        set.start();
    }

    private void startFinalAnim() {
        final ImageView image = (ImageView) findViewById(R.id.splash_logo);
        final TextView name = (TextView) findViewById(R.id.splash_name);

        ValueAnimator alpha = ObjectAnimator.ofFloat(image, "alpha", 0.0f, 0.7f);
        alpha.setDuration(1000);
        ValueAnimator alphaN = ObjectAnimator.ofFloat(name, "alpha", 0.0f, 0.7f);
        alphaN.setDuration(1000);
        ValueAnimator tranY = ObjectAnimator.ofFloat(image, "translationY", -image.getHeight() / 3, 0);
        tranY.setDuration(1000);
        ValueAnimator wait = ObjectAnimator.ofInt(0, 100);
        wait.setDuration(1500);
        wait.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            startOrFinish(animComplete);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                image.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.play(alpha).with(alphaN).with(tranY).before(wait);
        set.start();
    }

    private void initDataAndStartService() {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return init();
            }

            @Override
            protected void onPostExecute(Boolean b) {
                startOrFinish(animComplete);
            }
        }.execute();

    }

    // 媒体库为空退出，否则启动主 Activity
    private void startOrFinish(Boolean b) {
            if (animComplete) {
                startMainActivity();
            } else {
                animComplete = true;
            }
    }

    // 准备数据
    private boolean init() {

        // 耗时
        prepareData();

        //   耗时
        initAppDataIfNeed();

        //   耗时，启动服务之前先准备好数据
        stopService();
        startService();
        startService(new Intent(SplashActivity.this, SaveStateService.class));
        return true;
    }

    protected void prepareData() {
        mediaManager.refreshData(this);
    }

    protected void initAppDataIfNeed() {
        // 更新数据库
        List<Song> diskSongs = mediaManager.getSongList(this);
        List<Song> dbSongs = MediaUtils.DBSongInfoListToSongList(dbController.getSongInfos());
        Log.d("wanghp000", "initAppDataIfNeed: "+dbSongs.size());
        // 移除
        for (Song song : dbSongs) {
            Log.d("wanghp000", "initAppDataIfNeed: "+song.describeContents());
            if (!diskSongs.contains(song)) {
                dbController.removeSongInfo(song);
            }
        }

        // 新增
        dbSongs = MediaUtils.DBSongInfoListToSongList(dbController.getSongInfos());
        List<Song> add = new ArrayList<>();
        for (Song song : diskSongs) {
            if (!dbSongs.contains(song)) {
                add.add(song);
            }
        }
        if (add.size() > 0) {
            dbController.addSongInfo(add);
        }

    }

    /**
     * 启动服务，应确保获得文件读写权限后再启动，启动服务后再绑定，这样即使绑定这解除绑定，
     * 服务端也能继续运行 ？？？
     */
    protected void startService() {
        PlayServiceManager.startPlayService(this);
    }

    protected void stopService(){
        PlayServiceManager.stopPlayService(this);
    }

    private void startMainActivity() {
        ActivityManager.getInstance().startMainActivity(this);
        finish();
    }
}
