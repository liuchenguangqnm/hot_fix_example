package com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.base.baseui.BaseActivity;
import com.example.hotfix.base.baseui.BaseActivityFullScreen;
import com.example.hotfix.base.baseui.BaseFragment;
import com.example.hotfix.base.baseui.BaseFragmentActivity;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.utils.retrofitUtils.NetUtil;

import java.lang.ref.SoftReference;

import rx.Subscriber;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/1/14       Sunny           网络请求回调通用方法
 */
public class NormalRequestSubscriber extends Subscriber<BaseCallBackBean> {
    // 页面实例
    private SoftReference<Object> uiInstanceSoftReference;
    private String requestUrl;
    private Gson gson;
    private BaseView viewImpl;
    private long startRequestTime = 0;

    public NormalRequestSubscriber(String requestUrl, Object uiInstance, Gson gson) {
        this.requestUrl = requestUrl;
        this.gson = gson;
        this.uiInstanceSoftReference = new SoftReference<Object>(uiInstance);
        startRequestTime = System.currentTimeMillis(); // 记录发起请求的时间
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
        System.gc(); // 手动清内存，照顾老手机
        Log.i("Retrofit", "complete: " + requestUrl);
    }

    @Override
    public void onError(Throwable e) {
        System.gc(); // 手动清内存，照顾老手机
        Log.i("Retrofit", "error: " + "(" + requestUrl + ") " + e.getMessage());
        if (viewImpl != null) {
            if (!NetUtil.isNetworkConnected()) {
                e = new Exception("请求错误  请检查网络"); // 如果是返回Error，不能让吐司直接弹出Error的报错
                viewImpl.loadDataError(e, 404, requestUrl);
            } else if (e instanceof java.net.SocketTimeoutException) {
                e = new Exception("请求超时"); // 如果是返回Error，不能让吐司直接弹出Error的报错
                viewImpl.loadDataError(e, 404, requestUrl);
            } else {
                e = new Exception("请求错误 请稍后再试"); // 如果是返回Error，不能让吐司直接弹出Error的报错
                viewImpl.loadDataError(e, 404, requestUrl);
            }
        }
    }

    @Override
    public void onNext(BaseCallBackBean masterTalkBackBean) {
        Log.i("普通请求监听", "url: " + requestUrl + ", 请求响应时间：" + (System.currentTimeMillis() - startRequestTime) / 1000f + "秒");
        try {
            System.gc(); // 手动清内存，照顾老手机
            Log.i("Retrofit", "next: " + requestUrl);
            // 获取返回msg
            String msg = masterTalkBackBean.msg;
            if (masterTalkBackBean.code == 200) {
                viewImpl.loadDataSuccess(requestUrl, masterTalkBackBean);
            } else {
                viewImpl.loadDataError(new Exception(msg != null && !msg.equals("") ? msg : "回调msg为空"), masterTalkBackBean.code, requestUrl);
            }
        } catch (Exception exception) {
            Log.i(exception.getMessage(), "数据解析过程出现异常");
        }
    }
}
