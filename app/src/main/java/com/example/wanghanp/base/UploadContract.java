package com.example.wanghanp.base;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by wanghanping on 2018/1/15.
 */

public class UploadContract {
    public interface UploadView{
        Context getContext();
        void showInfo(String info);
        ArrayList<String> getImageList();
    }

    public interface UploadPresenter{
        void upLoad();
    }

    public interface UPloadMode{
        void upLoad(ArrayList<String> list, upLoadCallBack<String> call);
    }

    public interface upLoadCallBack<T>{
        void getCallBack(T call);
    }
}
