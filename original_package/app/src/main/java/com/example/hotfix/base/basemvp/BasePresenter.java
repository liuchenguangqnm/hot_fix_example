package com.example.hotfix.base.basemvp;

import android.widget.Toast;

import com.example.hotfix.MyApplication;
import com.example.hotfix.mvp.models.RequestModule;
import com.example.hotfix.utils.retrofitUtils.NetUtil;
import com.example.hotfix.utils.retrofitUtils.downloadpg.ProgressListener;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/1/14       Sunny          请填写变更内容
 */
public abstract class BasePresenter<T> {
    // 请求Module
    private RequestModule requestModuleDownload;
    private RequestModule requestModuleNormal;
    // 页面实例
    public SoftReference<T> uiSoftReference; // 页面实例

    public BasePresenter(T uiInstance) {
        uiSoftReference = new SoftReference<T>(uiInstance);
    }

    public boolean requestDownLoadFromWeb(String totalUrl, ProgressListener progressListener, int downLoadFileType) {
        if (requestModuleDownload == null)
            requestModuleDownload = new RequestModule<T>(uiSoftReference.get());
        if (NetUtil.isWifiProxy()) {
            Toast.makeText(MyApplication.instance, "禁止使用wifi代理访问接口!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            requestModuleDownload.requestWebDownload(totalUrl, progressListener, downLoadFileType);
            return true;
        }
    }

    public boolean requestDownLoadFromWeb(String totalUrl, String fileName, ProgressListener progressListener, int downLoadFileType) {
        if (requestModuleDownload == null)
            requestModuleDownload = new RequestModule<T>(uiSoftReference.get());
        if (NetUtil.isWifiProxy()) {
            Toast.makeText(MyApplication.instance, "禁止使用wifi代理访问接口!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            requestModuleDownload.requestWebDownload(totalUrl, fileName, progressListener, downLoadFileType);
            return true;
        }
    }

    public boolean requestDataFromWeb(HashMap<String, String> requestParams, String requestUrl) {
        if (requestModuleNormal == null)
            requestModuleNormal = new RequestModule<T>(uiSoftReference.get());
        if (NetUtil.isWifiProxy()) {
            Toast.makeText(MyApplication.instance, "禁止使用wifi代理访问接口!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            requestModuleNormal.requestWeb(requestParams, requestUrl);
            return true;
        }
    }

    public boolean requestDataFromWeb(String requestParam, String requestUrl) {
        if (requestModuleNormal == null)
            requestModuleNormal = new RequestModule<T>(uiSoftReference.get());
        if (NetUtil.isWifiProxy()) {
            Toast.makeText(MyApplication.instance, "禁止使用wifi代理访问接口!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            requestModuleNormal.requestWeb(requestParam, requestUrl);
            return true;
        }
    }

    public boolean requestDataQuery(HashMap<String, String> requestParams1, String requestUrl) {
        if (requestModuleNormal == null)
            requestModuleNormal = new RequestModule<T>(uiSoftReference.get());
        if (NetUtil.isWifiProxy()) {
            Toast.makeText(MyApplication.instance, "禁止使用wifi代理访问接口!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            requestModuleNormal.requestQuery(requestParams1, requestUrl);
            return true;
        }
    }

    /**
     * 当页面销毁的时候停止下载访问
     */
    public void stopNormalRequestWhenDestroy() {
        if (requestModuleNormal != null) {
            requestModuleNormal.stopAllRequest();
        }
    }
}
