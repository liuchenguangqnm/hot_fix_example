package com.example.hotfix.mvp.Views;

import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.ui.activity.MainActivity;

/**
 * Created by ASUS on 2018/1/10.
 */
public class MainActivityVieImpl extends BaseView<MainActivity> {

    public MainActivityVieImpl(MainActivity mainActivityInstance) {
        super(mainActivityInstance);
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

    /**
     * 首次网络请求成功后缓存加载背景图
     */
    public void bgBitmapFirstLoadSuc() {

    }
}
