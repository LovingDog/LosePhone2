package com.example.wanghanp.losephone.Shake.contract;

import com.example.wanghanp.base.UploadContract;

/**
 * Created by wanghanping on 2018/1/15.
 */

public class UploadImgPresenter implements UploadContract.UploadPresenter {

    private UploadContract.UploadView mUploadView;
    private UploadImgMode mUploadImg;

    public UploadImgPresenter(UploadContract.UploadView uploadView){
        this.mUploadView = uploadView;
        mUploadImg = new UploadImgMode();
    }
    @Override
    public void upLoad() {
        mUploadImg.upLoad(this.mUploadView.getImageList(), new UploadContract.upLoadCallBack<String>() {
            @Override
            public void getCallBack(String call) {

            }
        });
    }
}
