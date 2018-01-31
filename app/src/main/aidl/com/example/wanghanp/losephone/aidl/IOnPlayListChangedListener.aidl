// IOnSongChange.aidl
package com.example.wanghanp.losephone.aidl;
import com.example.wanghanp.losephone.aidl.Song;

// Declare any non-default types here with import statements

interface IOnPlayListChangedListener {

    void onPlayListChange(in Song current,int index,int id);
}
