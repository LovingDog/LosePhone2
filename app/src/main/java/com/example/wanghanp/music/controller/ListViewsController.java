package com.example.wanghanp.music.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.example.wanghanp.base.bean.SongInfo;
import com.example.wanghanp.base.interfacelistener.ContentUpdatable;
import com.example.wanghanp.base.interfacelistener.OnPlayListVisibilityChange;
import com.example.wanghanp.base.interfacelistener.OnUpdateStatusChanged;
import com.example.wanghanp.db.DBMusicocoController;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.aidl.IPlayControl;
import com.example.wanghanp.losephone.aidl.Song;
import com.example.wanghanp.losephone.manager.BroadcastManager;
import com.example.wanghanp.losephone.manager.MediaManager;
import com.example.wanghanp.losephone.music.service.PlayController;
import com.example.wanghanp.losephone.music.service.play.adapter.PlayListAdapter;
import com.example.wanghanp.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ListViewsController implements
        View.OnClickListener,
        ContentUpdatable,
        OnPlayListVisibilityChange {

    private final Activity activity;

    private final MediaManager mediaManager;
    private IPlayControl control;
    private DBMusicocoController dbController;

    private Dialog mDialog;

    private ListView mList;
    private View mLine;
    private View mListContainer;
    private ImageButton mPlayMode;
    private ImageButton mLocation;
    private TextView mSheet;

    private PlayListAdapter adapter;
    private final List<SongInfo> data = new ArrayList<>();

    private int currentSong;

    public ListViewsController(Activity activity, MediaManager mediaManager) {
        this.activity = activity;
        this.mediaManager = mediaManager;
        this.mDialog = new Dialog(activity, R.style.BottomDialog);

    }

    public void initViews() {
        View contentView = activity.getLayoutInflater().inflate(R.layout.main_play_list, null);
        mList = (ListView) contentView.findViewById(R.id.main_play_list);

        mListContainer = contentView.findViewById(R.id.main_play_list_container);

        mLocation = (ImageButton) contentView.findViewById(R.id.main_play_location);
        mPlayMode = (ImageButton) contentView.findViewById(R.id.main_play_mode);
        mSheet = (TextView) contentView.findViewById(R.id.main_play_sheet);
        mLine = contentView.findViewById(R.id.main_play_line);

        mLocation.setOnClickListener(this);
        mPlayMode.setOnClickListener(this);

        initDialog(contentView);

    }

    private void initDialog(View contentView) {
        mDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        DisplayMetrics metrics = Utils.getMetrics(activity);
        layoutParams.width = metrics.widthPixels;
        layoutParams.height = metrics.heightPixels * 5 / 9;
        contentView.setLayoutParams(layoutParams);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mDialog.setCanceledOnTouchOutside(true);

        //位置恢复
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentSong = mList.getFirstVisiblePosition();
            }
        });
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mList.setSelection(currentSong);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_play_mode:
                handlePlayModeChange();
                break;
            case R.id.main_play_location:
                handleLocationCurrentPlayingSong();
                break;
        }
    }

    private void handleLocationCurrentPlayingSong() {
        try {
            int index = control.currentSongIndex();
            mList.smoothScrollToPosition(index);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void handlePlayModeChange() {
        try {

            int mode = control.getPlayMode();
            mode = ((mode - 21) + 1) % 3 + 21;
            control.setPlayMode(mode);

            String str = updatePlayMode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void initData(IPlayControl control, DBMusicocoController dbController) {
        this.control = control;
        this.dbController = dbController;
//        this.songOperation = new SongOperation(activity, control, dbController);

        adapter = new PlayListAdapter(activity, data);
        mList.setAdapter(adapter);
        initAdapterClickListener();

        try {
            int index = control.currentSongIndex();
            if (index < adapter.getCount() - 1) {
                currentSong = index;
                mList.setSelection(currentSong);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initAdapterClickListener() {
        adapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, SongInfo info) {
                try {

                    int index = control.currentSongIndex();
                    if (position == index) {
                        if (control.status() != PlayController.STATUS_PLAYING)
                            control.resume();
                        return;
                    }

                    SongInfo in = adapter.getItem(position);
                    control.play(new Song(in.getData()));


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        adapter.setOnRemoveClickListener(new PlayListAdapter.OnItemRemoveClickListener() {
            @Override
            public void onRemove(int position, SongInfo info) {
                Song s = new Song(info.getData());
//                songOperation.removeSongFromCurrentPlayingSheet(null, s);

                // PlayListAdapter 在 MainActivity 和 PlayActivity 中都有，所以需要发送广播通知 MainActivity 更新
                // 【我的歌单】 列表
                BroadcastManager.getInstance().sendBroadcast(activity, BroadcastManager.FILTER_MY_SHEET_UPDATE, null);

            }
        });
    }

    @Override
    public void update(Object obj, OnUpdateStatusChanged statusChanged) {

        SongInfo info = (SongInfo) obj;
        if (info == null) {
            return;
        }

        try {

            List<Song> ss = control.getPlayList();
            data.clear();
            for (Song s : ss) {
                data.add(mediaManager.getSongInfo(activity, s));
            }
            adapter.notifyDataSetChanged();

            Song song = new Song(info.getData());
            int i = ss.indexOf(song);
            if (i != -1) {
                adapter.setSelectItem(i);
            }

            updatePlayMode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void noData() {
    }

    private String updatePlayMode() throws RemoteException {

        Drawable drawable = null;
        String mod;
        int mode = control.getPlayMode();

        switch (mode) {
            case PlayController.MODE_SINGLE_LOOP:
                drawable = activity.getResources().getDrawable(R.mipmap.single_loop);
                break;

            case PlayController.MODE_RANDOM:
                drawable = activity.getResources().getDrawable(R.mipmap.random);
                break;

            case PlayController.MODE_LIST_LOOP:
            default:
                drawable = activity.getResources().getDrawable(R.mipmap.list_loop);
                break;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            drawable.setTint(adapter.getVicTextColor());
        }

        mPlayMode.setImageDrawable(drawable);

        return "";
    }

    @Override
    public void show() {
        mDialog.show();
    }

    @Override
    public void hide() {
        if (visible()) {
            mDialog.dismiss();
        }
    }

    @Override
    public boolean visible() {
        return mDialog.isShowing();
    }

}
