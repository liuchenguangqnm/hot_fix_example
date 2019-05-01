package com.example.hotfix.mvp.Views;

import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.ui.activity.SplashActivity;

/**
 * Created by ASUS on 2018/1/10.
 */
public class SplashActivityVieImpl extends BaseView<SplashActivity> {

    public SplashActivityVieImpl(SplashActivity splashActivityInstance) {
        super(splashActivityInstance);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hidLoading() {

    }

    @Override
    public void showLoadError() {

    }

    @Override
    public void ShowNoData() {

    }

    @Override
    public void showData(BaseCallBackBean bean) {

    }

    @Override
    public void loadDataSuccess(String requestUrl, BaseCallBackBean baseCallBackBean) {

    }

    @Override
    public void loadDataError(Throwable throwable, int errorCode, String requestUrl) {

    }

    @Override
    public void getLoadingBgBitmap(int blurRadius) {

    }

    @Override
    public void resolveViewRes() {

    }

}
