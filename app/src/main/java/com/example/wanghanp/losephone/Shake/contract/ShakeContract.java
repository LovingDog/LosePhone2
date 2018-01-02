package com.example.wanghanp.losephone.Shake.contract;

import android.app.Activity;

/**
 * Created by wanghanping on 2017/12/26.
 */

public class ShakeContract {
//view
public interface IShakeView{
        Activity getActivity();
        void showShakeView();
        void dissShakeView();
        String getInfo();
    }

    //presenter
    public interface IShakePresenter{
        void toShake();
        void disShake();
    }

    //mode

    public interface IShakeMode{
        void toShake();
        void disShake();
    }
}
