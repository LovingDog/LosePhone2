package com.example.wanghanp.losephone.music.service;

import android.app.Service;

import com.example.wanghanp.db.DBMusicocoController;
import com.example.wanghanp.losephone.manager.MediaManager;
import com.example.wanghanp.losephone.preference.PlayPreference;
import com.example.wanghanp.losephone.preference.SettingPreference;


/**
 * Created by DuanJiaNing on 2017/6/24.
 */

public abstract class RootService extends Service {

    protected DBMusicocoController dbController;
    protected MediaManager mediaManager;

    protected PlayPreference playPreference;
//    protected AppPreference appPreference;
    protected SettingPreference settingPreference;

    public RootService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.playPreference = new PlayPreference(this);
        this.settingPreference = new SettingPreference(this);
        this.dbController = new DBMusicocoController(this, false);
        this.mediaManager = MediaManager.getInstance();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (dbController != null) {
            dbController.close();
        }
    }
}
