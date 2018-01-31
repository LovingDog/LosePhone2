package com.example.wanghanp.losephone.music.service;

import android.content.Context;

import com.example.wanghanp.losephone.aidl.PlayControlImpl;


/**
 * Created by DuanJiaNing on 2017/5/23.
 */

public class PlayServiceIBinder extends PlayControlImpl {

    private Context mContext;

    public PlayServiceIBinder(Context context) {
        super(context);
        this.mContext = context;
    }
}
