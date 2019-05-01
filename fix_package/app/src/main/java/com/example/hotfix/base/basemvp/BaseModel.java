package com.example.hotfix.base.basemvp;

import com.google.gson.Gson;
import com.example.hotfix.utils.retrofitUtils.downloadpg.ProgressListener;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by Sunshine on 2017/12/25
 * Model的基类，用于处理业务逻辑
 */
public abstract class BaseModel<T> {
    public Gson gson = new Gson();
    public SoftReference<T> uiInstanceSoftReference; // 页面实例

    public BaseModel(T uiInstance) {
        uiInstanceSoftReference = new SoftReference<T>(uiInstance);
    }

    public abstract void requestWeb(HashMap<String, String> requestParams, final String requestUrl);

    public abstract void requestWeb(String requestParam, final String requestUrl);

    public abstract void requestQuery(HashMap<String, String> requestParams1, final String requestUrl);

    public abstract void requestWebDownload(final String requestUrl, ProgressListener progressListener, int requestType);

    public abstract void requestWebDownload(final String requestUrl, String fileName, ProgressListener progressListener, int requestType);

    public abstract void stopAllRequest();
}