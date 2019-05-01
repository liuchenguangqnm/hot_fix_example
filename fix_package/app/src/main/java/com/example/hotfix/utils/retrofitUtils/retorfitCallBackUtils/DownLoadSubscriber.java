package com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.base.baseui.BaseActivity;
import com.example.hotfix.base.baseui.BaseActivityFullScreen;
import com.example.hotfix.base.baseui.BaseFragment;
import com.example.hotfix.base.baseui.BaseFragmentActivity;
import com.example.hotfix.utils.retrofitUtils.NetUtil;
import com.example.hotfix.utils.retrofitUtils.downloadpg.DownInfo;

import java.lang.ref.SoftReference;

import rx.Subscriber;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/1/14       Sunny           网络请求回调通用方法
 */
public class DownLoadSubscriber extends Subscriber<DownInfo> {
    // 页面实例
    private SoftReference<Object> uiInstanceSoftReference;
    private String requestUrl;
    private Gson gson;
    private BaseView viewImpl;

    public DownLoadSubscriber(String requestUrl, Object uiInstance, Gson gson) {
        this.requestUrl = requestUrl;
        this.gson = gson;
        this.uiInstanceSoftReference = new SoftReference<Object>(uiInstance);
        if (uiInstance instanceof BaseActivity) {
            BaseActivity baseActivityInstance = (BaseActivity) uiInstanceSoftReference.get();
            viewImpl = (BaseView) baseActivityInstance.viewImpl;
        } else if (uiInstance instanceof BaseFragmentActivity) {
            BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) uiInstanceSoftReference.get();
            viewImpl = (BaseView) baseFragmentActivity.viewImpl;
        } else if (uiInstance instanceof BaseFragment) {
            BaseFragment baseFragment = (BaseFragment) uiInstanceSoftReference.get();
            viewImpl = (BaseView) baseFragment.viewImpl;
        } else if (uiInstance instanceof BaseActivityFullScreen) {
            BaseActivityFullScreen baseActivityFullScreenInstance = (BaseActivityFullScreen) uiInstanceSoftReference.get();
            viewImpl = (BaseView) baseActivityFullScreenInstance.viewImpl;
        }
    }

    @Override
    public void onCompleted() {
        Log.i("RetrofitDownload", "complete: " + requestUrl);
    }

    @Override
    public void onError(Throwable e) {
        Log.i("RetrofitDownload", "error: " + "(" + requestUrl + ") " + e.getMessage());
        if (viewImpl != null) {
            if (!NetUtil.isNetworkConnected()) {
                e = new Exception("请求错误  请检查网络"); // 如果是返回Error，不能让吐司直接弹出Error的报错
                viewImpl.loadDataError(e, 404, requestUrl);
            }
        }
    }

    @Override
    public void onNext(DownInfo downInfo) {
        try {
            Log.i("RetrofitDownload", "next: " + requestUrl);
            // 正在下载过程中，只有网络切换或者断网了才会走onNext  TODO 原因需要再找找看
            Exception e = new Exception("请求错误  请检查网络");
            viewImpl.loadDataError(e, 404, requestUrl);
        } catch (NullPointerException exception) {
            Log.i(exception.getMessage(), "页面实例已经被回收了");
        }
    }
}
