package com.example.wanghanp.losephone.Shake.contract;

import com.example.wanghanp.base.UploadContract;
import com.example.wanghanp.http.APIService;
import com.example.wanghanp.http.RetrofitUtils;
import com.example.wanghanp.http.TokenResult;
import com.example.wanghanp.http.UserHttpResult;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wanghanping on 2018/1/15.
 */

public class UploadImgMode implements UploadContract.UPloadMode {

    public static final String url = "";
    String token = "ASDDSKKK19990SDDDSS";//用户token
    @Override
    public void upLoad(ArrayList<String> list, UploadContract.upLoadCallBack<String> call) {
        Map<String,RequestBody> hashMap = new HashMap<>();
        for (String path :
                list) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),new File(path));
            hashMap.put(token,requestBody);
        }
        RetrofitUtils.newInstence(url).create(APIService.class).upLoadImg(hashMap).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserHttpResult<TokenResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserHttpResult<TokenResult> tokenResultUserHttpResult) {

                    }
                });

    }
}
