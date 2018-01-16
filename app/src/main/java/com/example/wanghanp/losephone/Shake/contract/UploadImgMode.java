package com.example.wanghanp.losephone.Shake.contract;

import com.example.wanghanp.base.UploadContract;
import com.example.wanghanp.http.APIService;
import com.example.wanghanp.http.RetrofitUtils;
import com.example.wanghanp.http.TokenResult;
import com.example.wanghanp.http.UserHttpResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wanghanping on 2018/1/15.
 */

public class UploadImgMode implements UploadContract.UPloadMode {

    public static final String url = "http://192.168.61.11:8080/SSM/";
    String token = "ASDDSKKK19990SDDDSS";//用户token
    private Observable<UserHttpResult<TokenResult>> observe;
    MultipartBody.Part part;
    @Override
    public void upLoad(ArrayList<String> list, UploadContract.upLoadCallBack<String> call) {
        Map<String,RequestBody> hashMap = new HashMap<>();
        RequestBody requestBody = null;
        for (int i = 0;i< 1;i++) {
            String path = list.get(i);
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),new File(path));

            part = MultipartBody.Part.createFormData("file",new File(path).getName(),requestBody);
//            hashMap.put("file"+i+"\"; filename=\""+new File(path).getName(),requestBody);
            hashMap.put("file"+"\"; filename=\""+new File(path).getName(),requestBody);
        }
//        RetrofitUtils.newInstence(url).create(APIService.class).upLoadImg(requestBody).subscribeOn(Schedulers.newThread())
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
