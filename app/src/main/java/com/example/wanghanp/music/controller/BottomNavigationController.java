package com.example.wanghanp.music.controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wanghanp.base.ActivityManager;
import com.example.wanghanp.base.bean.SongInfo;
import com.example.wanghanp.base.interfacelistener.ContentUpdatable;
import com.example.wanghanp.base.interfacelistener.OnUpdateStatusChanged;
import com.example.wanghanp.db.DBMusicocoController;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.aidl.IPlayControl;
import com.example.wanghanp.losephone.aidl.Song;
import com.example.wanghanp.losephone.manager.BroadcastManager;
import com.example.wanghanp.losephone.manager.MediaManager;
import com.example.wanghanp.losephone.manager.PlayNotifyManager;
import com.example.wanghanp.losephone.music.service.PlayController;
import com.example.wanghanp.losephone.music.service.PlayServiceCallback;
import com.example.wanghanp.losephone.music.service.play.PeriodicTask;
import com.example.wanghanp.losephone.preference.PlayPreference;
import com.example.wanghanp.myview.media.PlayView;
import com.example.wanghanp.util.Utils;


/**
 * Created by DuanJiaNing on 2017/6/27.
 */

public class BottomNavigationController implements
        View.OnClickListener,
        PlayServiceCallback,
        ContentUpdatable {

    private final Activity activity;
    private final BroadcastManager broadcastManager;
    private final PeriodicTask task;
    private IPlayControl mControl;

    private final MediaManager mediaManager;
    private final PlayPreference playPreference;

    private View mContainer;
    private View mProgress;
    private View mProgressBG;
    private ImageView mAlbum;
    private TextView mName;
    private TextView mArts;
    private PlayView mPlay;
    private ImageButton mShowList;

//    private BitmapBuilder builder;
//    private final PeriodicTask task;
    private ListViewsController listViewsController;

    private SongInfo currentSong;
    private boolean hasInitData = false;

    private PlayNotifyManager playNotifyManager;

    public BottomNavigationController(Activity activity, MediaManager mediaManager) {
        this.activity = activity;
        this.mediaManager = mediaManager;
        this.playPreference = new PlayPreference(activity);
//        this.builder = new BitmapBuilder(activity);
        this.broadcastManager = BroadcastManager.getInstance();
        this.listViewsController = new ListViewsController(activity, mediaManager);
//
        task = new PeriodicTask(new PeriodicTask.Task() {
            @Override
            public void execute() {
                mContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        updateProgress();
                    }
                });
            }
        }, 800);
    }

    public void initView() {
        initSelfViews();
        listViewsController.initViews();
    }

    private void initSelfViews() {

        mAlbum = (ImageView) activity.findViewById(R.id.list_album);
        mName = (TextView) activity.findViewById(R.id.list_name);
        // 跑马灯
        mName.setSelected(true);
        mArts = (TextView) activity.findViewById(R.id.list_arts);
        mPlay = (PlayView) activity.findViewById(R.id.list_play);
        mShowList = (ImageButton) activity.findViewById(R.id.list_list);
        mContainer = activity.findViewById(R.id.list_bottom_nav_container);
        mProgress = activity.findViewById(R.id.list_progress);
        mProgressBG = activity.findViewById(R.id.list_progress_bg);

        mContainer.setOnClickListener(this);
        mShowList.setOnClickListener(this);
        mPlay.setOnClickListener(this);

    }

    @Override
    public void dataIsReady(IPlayControl control) {
    }

    public void initData(IPlayControl control, DBMusicocoController dbController) {
        this.mControl = control;
        this.playNotifyManager = new PlayNotifyManager(activity, control, dbController);
        try {
            Song song = control.currentSong();
            if (song != null) {
                playNotifyManager.updateSong(mediaManager.getSongInfo(activity, song));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        playNotifyManager.show();
        playNotifyManager.initBroadcastReceivers();

        listViewsController.initData(control, dbController);

        try {
            // songChanged & onPlayListChange 回调会对 currentSong 进行赋值，但应用第一次启动时的回调很有可能已经错过，
            // 这是因为 MainActivity 中的 bindService 方法比较耗时导致，该方法会对 MediaManager 进行数
            // 据初始化，这些数据时急需的，不能异步获取，只能阻塞
            Song song = control.currentSong();
            currentSong = this.mediaManager.getSongInfo(activity, song);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        hasInitData = true;
        update(null,null);
    }

    public boolean hasInitData() {
        return hasInitData;
    }

    @Override
    public void songChanged(Song song, int index, boolean isNext) {
        if (song == null || index == -1) {
            //播放列表是空的
            return;
        }

        currentSong = mediaManager.getSongInfo(activity, song);
        playNotifyManager.updateSong(currentSong);
        update(null, null);

        // 如果此时正在浏览歌单详情 SheetDetailActivity ，需要通知更新
        BroadcastManager.getInstance().sendBroadcast(activity, BroadcastManager.FILTER_SHEET_DETAIL_SONGS_UPDATE, null);
    }

    private void updateProgress() {
        int progress;
        int duration = (int) currentSong.getDuration();
        final float phoneWidth = Utils.getMetrics(activity).widthPixels;

        try {
            progress = mControl.getProgress();
            int width = (int) (phoneWidth * (progress / (float) duration));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mProgress.getLayoutParams();
            params.width = width;
            mProgress.setLayoutParams(params);
            mProgress.invalidate();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateSongInfo() {

        Song song = new Song(currentSong.getData());
        SongInfo info = mediaManager.getSongInfo(activity, song);

        String name = info.getTitle();
        String arts = info.getArtist();
        Log.d("wanghp007", "updateSongInfo: song_name ==  "+name);

        mName.setText(name);
        mArts.setText(arts);

        Glide.with(activity).load(info.getAlbum_path()).placeholder(R.mipmap.person)
                .override(mAlbum.getWidth() == 0 ? 100 : mAlbum.getWidth(), mAlbum
                        .getHeight() == 0 ? 10 : mAlbum.getHeight()).into(mAlbum);
    }

    @Override
    public void update(@Nullable Object obj, OnUpdateStatusChanged completed) {

        try {

            if (mControl.status() == PlayController.STATUS_PLAYING) {
                mPlay.setPlayStatus(true);
                startProgressUpdateTask();
            } else {
                mPlay.setPlayStatus(false);
                stopProgressUpdateTask();
            }

            if (currentSong != null) {
                updateProgress();
                updateSongInfo();
                listViewsController.update(currentSong, null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void noData() {
    }

    @Override
    public void startPlay(Song song, int index, int status) {
        currentSong = mediaManager.getSongInfo(activity, song);
        playNotifyManager.updateSong(currentSong);

        startProgressUpdateTask();
        mPlay.setChecked(true);

        // 列表上的播放按钮状态
        broadcastManager.sendBroadcast(activity, BroadcastManager.FILTER_MY_SHEET_UPDATE, null);
    }

    @Override
    public void stopPlay(Song song, int index, int status) {
        stopProgressUpdateTask();
        mPlay.setChecked(false);
        playNotifyManager.updateSong(mediaManager.getSongInfo(activity, song));

        //??
        broadcastManager.sendBroadcast(activity, BroadcastManager.FILTER_MY_SHEET_UPDATE, null);
    }

    public void startProgressUpdateTask() {
        task.start();
    }

    public void stopProgressUpdateTask() {
        task.stop();
    }

    @Override
    public void onPlayListChange(Song current, int index, int id) {
        if (current == null || index < 0) {
            return;
        }

        currentSong = mediaManager.getSongInfo(activity, current);
        update(null, null);
        playPreference.updateSheet(id);

        //发送广播通知 MySheetController 更新列表（列表的选中播放状态）
        broadcastManager.sendBroadcast(activity, BroadcastManager.FILTER_MY_SHEET_UPDATE, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_bottom_nav_container:
//                ActivityManager.getInstance().startPlayActivity(activity);
                break;
            case R.id.list_play: {
                paly(true);
                break;
            }
            case R.id.list_list:
                if (listViewsController.visible()) {
                    listViewsController.hide();
                } else {
                    listViewsController.show();
                }
                break;
            default:
                break;
        }
    }

    public void paly(boolean touchView) {
        if (mControl == null) {
            return;
        }
        if (!touchView) {
            mPlay.toggle();
        }
        boolean play = mPlay.isChecked();
        Log.d("wanghp007", "paly: play = " +play);
        try {
            if (play) {
                mControl.resume();
            } else mControl.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            if (checked()) {
                mControl.pause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean checked() {
        return mPlay.isChecked();
    }



    public void unregisterReceiver() {
        playNotifyManager.unregisterReceiver();
        playNotifyManager.hide();
    }

    public void hidePlayNotify() {
        if (playNotifyManager.visible()) {
            playNotifyManager.hide();
        }
    }
}
