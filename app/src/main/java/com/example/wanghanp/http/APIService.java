package com.example.wanghanp.http;




import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Url;
import rx.Observable;

/**
 * API--接口  服务[这里处理的是同一的返回格式 resultCode  resultInfo Data<T> --->这里的data才是返回的结果,T就是泛型 具体返回的been对象或集合]
 * Created by HDL on 2016/8/3.
 */
public interface APIService {

    /**
     * 用户发短信的接口
     *
     * @return RxJava 对象
     */
//    @POST("sendSMS.json?")
//    Observable<UserHttpResult> senSMS(@Query("telphone") String telphone, @Query("type") int type);
    @Multipart
    @POST("fileUploadController/uploadFile")
    Observable<UserHttpResult<TokenResult>> upLoadImg(@PartMap Map<String,RequestBody> hashMap);
//    Observable<UserHttpResult<TokenResult>> upLoadImg( @Part("file\"; filename=\"image.png") RequestBody imgs);

    @GET("user/findAllUser")
    Observable<UserHttpResult<TokenResult>> query();
//    /**
//     * 用户登录的接口
//     *
//     * @param username 用户名
//     * @param pwd      密码
//     * @return RxJava 对象
//     */
////    @POST("okhttp/UserInfoServlert")
////    Observable<UserHttpResult<TokenResult>> userLogin(@Query("username") String username, @Query("pwd") String pwd);
//
//    /**
//     * 查询ip地址信息的接口
//     *
//     * @param ip 需查询的ip
//     * @return RxJava 对象
//     */
//    @GET("service/getIpInfo.php")
//    Observable<IPHttpResult<IpInfo>> queryIp(@Query("ip") String ip);
//
//    /**
//     * 查询豆瓣排名前250的电影
//     *
//     * @param start 从第几部开始
//     * @param count 几页(一页有12部)
//     * @return
//     */
//    @GET("v2/movie/top250")
//    Observable<Movies> getMovies(@Query("start") int start, @Query("count") int count);
}
