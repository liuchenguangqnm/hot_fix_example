package com.example.hotfix.mvp.models;

import com.google.gson.Gson;
import com.example.hotfix.base.basemvp.BaseModel;
import com.example.hotfix.utils.retrofitUtils.RetrofitManager;
import com.example.hotfix.utils.retrofitUtils.downloadpg.ProgressListener;
import com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils.DownLoadSubscriber;
import com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils.MyAction1;
import com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils.NormalRequestSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ASUS on 2018/1/16.
 */

public class RequestModule<T> extends BaseModel<T> {
    // 普通接口请求的Map请求池
    public HashMap<String, RetrofitManager> normalRequestMap = new HashMap<>();

    public RequestModule(T uiInstance) {
        super(uiInstance);
    }

    @Override
    public void requestWebDownload(String totalUrl, ProgressListener progressListener, int downLoadFileType) {
        if (gson == null) {
            gson = new Gson();
        }

        RetrofitManager retrofitManagerDownload = RetrofitManager.builderDownload(progressListener, totalUrl, downLoadFileType);
        retrofitManagerDownload.startBaseRetrofitRequestDownload(totalUrl,
                new DownLoadSubscriber(totalUrl, uiInstanceSoftReference.get(), gson), downLoadFileType);
    }

    @Override
    public void requestWebDownload(String totalUrl, String fileName, ProgressListener progressListener, int downLoadFileType) {
        if (gson == null) {
            gson = new Gson();
        }

        RetrofitManager retrofitManagerDownload = RetrofitManager.builderDownload(progressListener, totalUrl, downLoadFileType);
        retrofitManagerDownload.startBaseRetrofitRequestDownload(totalUrl, fileName,
                new DownLoadSubscriber(totalUrl, uiInstanceSoftReference.get(), gson), downLoadFileType);
    }

    @Override
    public void requestWeb(HashMap requestParams, String requestUrl) {
        if (gson == null) {
            gson = new Gson();
        }
        // 生成加密请求头原始数据
        String requestRx = "";
        Set<Map.Entry> entrySet = requestParams.entrySet();
        for (Map.Entry entry : entrySet) {
            requestRx += (entry.getValue() + ",");
        }
        if (requestRx.endsWith(",") && requestRx.length() > 1)
            requestRx = requestRx.substring(0, requestRx.length() - 1);
        // 获取请求对象并发起请求
        RetrofitManager retrofitManager = normalRequestMap.get(requestUrl);
        if (retrofitManager == null) {
            retrofitManager = RetrofitManager.builder(requestRx, false);
            normalRequestMap.put(requestUrl, retrofitManager);  // 维护网络请求池
        }
        retrofitManager.startBaseRetrofitRequest(requestParams, requestUrl,
                new MyAction1(requestUrl), new NormalRequestSubscriber(requestUrl, uiInstanceSoftReference.get(), gson), gson);
    }

    @Override
    public void requestWeb(String requestParam, String requestUrl) {
        if (gson == null) {
            gson = new Gson();
        }
        // 生成加密请求头原始数据
        String requestRx = requestParam;
        // 新建请求对象并发起请求
        RetrofitManager retrofitManager = normalRequestMap.get(requestUrl);
        if (retrofitManager == null) {
            retrofitManager = RetrofitManager.builder(requestRx, false);
            normalRequestMap.put(requestUrl, retrofitManager);  // 维护网络请求池
        }
        retrofitManager.startBaseRetrofitRequest(requestParam, requestUrl,
                new MyAction1(requestUrl), new NormalRequestSubscriber(requestUrl, uiInstanceSoftReference.get(), gson), gson);
    }

    @Override
    public void requestQuery(HashMap requestParams, String requestUrl) {
        if (gson == null) {
            gson = new Gson();
        }
        // 生成加密请求头原始数据
        String requestRx = "";
        Set<Map.Entry> entrySet = requestParams.entrySet();
        for (Map.Entry entry : entrySet) {
            requestRx += (entry.getValue() + ",");
        }
        if (requestRx.endsWith(",") && requestRx.length() > 1)
            requestRx = requestRx.substring(0, requestRx.length() - 1);
        // 新建请求对象并发起请求
        RetrofitManager retrofitManager = normalRequestMap.get(requestUrl);
        if (retrofitManager == null) {
            retrofitManager = RetrofitManager.builder(requestRx, false);
            normalRequestMap.put(requestUrl, retrofitManager);  // 维护网络请求池
        }
        retrofitManager.startBaseRetrofitQuery(requestParams, requestUrl,
                new MyAction1(requestUrl), new NormalRequestSubscriber(requestUrl, uiInstanceSoftReference.get(), gson));
    }

    /**
     * 当页面销毁的时候停止所有的下载访问
     */
    @Override
    public void stopAllRequest() {
        for (Map.Entry<String, RetrofitManager> stringRetrofitManagerEntry : normalRequestMap.entrySet()) {
            stringRetrofitManagerEntry.getValue().stopNormalRequest();
        }
        normalRequestMap.clear();
    }
}
