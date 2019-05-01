package com.example.hotfix.utils.retrofitUtils.retorfitCallBackUtils;

import android.util.Log;

import com.example.hotfix.bean.BaseCallBackBean;

import rx.functions.Action1;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/1/14       Sunny        网络请求线程内部的call方法
 */
public class MyAction1 implements Action1<BaseCallBackBean> {
    private String requestUrl;

    public MyAction1(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void call(BaseCallBackBean masterTalkBackBean) {
        Log.i("Retrofit", "call: " + requestUrl);
    }
}
