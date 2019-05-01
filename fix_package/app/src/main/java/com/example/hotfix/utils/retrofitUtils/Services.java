package com.example.hotfix.utils.retrofitUtils;


import com.example.hotfix.bean.BaseCallBackBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Sunshine on 2017/12/25
 */
public interface Services {

    // 请求的接口地址：http://192.168.2.251/TPJoyschoolLc/AppV2/AppCameraIntercomAuthorization/getHeadmasterAuthorization
    // @GET("AppCameraIntercomAuthorization/getHeadmasterAuthorization?")
    // Observable<MasterTalkBackBean> getHeadmasterAuthorization(@Query("schoolGUID") String schoolGUID);

    // @Body不可以和@FormUrlEncoded同时使用
    @POST(HttpRequestUrls.bodyRequest)
    Observable<BaseCallBackBean> drawmoney(
            @Body RequestBody json
    );

    @FormUrlEncoded
    @POST(HttpRequestUrls.query)
    Observable<BaseCallBackBean> compTaskRecord(
            @Query("recordId") String recordId,
            @Field("body") String body
    );

}
