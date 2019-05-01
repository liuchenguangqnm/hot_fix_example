package com.example.hotfix.mvp.Presenters;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.hotfix.base.basemvp.BasePresenter;
import com.example.hotfix.ui.activity.MainActivity;
import com.example.hotfix.ui.activity.SplashActivityPlug;
import com.example.hotfix.utils.BackgroundUtils;

/**
 * Created by Sunshine on 2018/1/10.
 */
public class SplashActivityPresenterImpl extends BasePresenter<SplashActivityPlug> {

    public SplashActivityPresenterImpl(SplashActivityPlug splashActivityPlugInstance) {
        super(splashActivityPlugInstance);
    }

    /**
     * 初始化App所需data
     */
    public void initAppData() {
        // 初始化网络请求过滤器和时间选择队列
        BackgroundUtils.initUrlRequestIntentFilter();
        // 暂时不需要刷新时间列表
        // BackgroundUtils.getTimedatas(false);
    }

    /**
     * 开启下一个页面的方法
     */
    public void startNextActivity(final long delayTime) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (uiSoftReference.get().splashActivityPlugInstance == null)
                return;
            uiSoftReference.get().splashActivityPlugInstance.startActivity(new Intent(uiSoftReference.get().splashActivityPlugInstance, MainActivity.class));
            uiSoftReference.get().splashActivityPlugInstance.finish();
        }, delayTime);
    }
}
