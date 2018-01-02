package com.example.wanghanp.losephone.Shake.contract.presenter;

import com.example.wanghanp.losephone.Shake.contract.ShakeContract;
import com.example.wanghanp.losephone.Shake.contract.mode.ShakeMode;

/**
 * Created by wanghanping on 2017/12/26.
 */

public class ShakePresenter implements ShakeContract.IShakePresenter {


    private ShakeContract.IShakeView mShakeView;
    private ShakeContract.IShakeMode mShakeMode;

    public ShakePresenter(ShakeContract.IShakeView shakeView) {
        this.mShakeView = shakeView;
        mShakeMode = new ShakeMode();
    }

    @Override
    public void toShake() {
        mShakeMode.toShake();
    }

    @Override
    public void disShake() {
        mShakeMode.disShake();
    }
}
