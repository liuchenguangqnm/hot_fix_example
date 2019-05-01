package com.example.hotfix.utils.retrofitUtils.downloadpg;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * service统一接口数据
 * Created by WZG on 2016/7/16.
 */
public interface HttpService {

    /* 断点续传下载接口 */
    @Streaming/* 大文件需要加入这个判断，防止下载过程中写入到内存中 */
    @GET
    /* 下载地址需要通过@url动态指定（不适固定的），@head标签是指定下载的起始位置（断点续传的位置） */
    Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);

}