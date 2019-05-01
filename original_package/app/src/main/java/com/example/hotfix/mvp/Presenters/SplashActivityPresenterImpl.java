package com.example.hotfix.mvp.Presenters;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.hotfix.base.basemvp.BasePresenter;
import com.example.hotfix.ui.activity.MainActivity;
import com.example.hotfix.ui.activity.SplashActivity;
import com.example.hotfix.utils.BackgroundUtils;
import com.example.hotfix.utils.retrofitUtils.downloadpg.PlugApkUtils;

/**
 * Created by Sunshine on 2018/1/10.
 */
public class SplashActivityPresenterImpl extends BasePresenter<SplashActivity> {

    public SplashActivityPresenterImpl(SplashActivity splashActivityInstance) {
        super(splashActivityInstance);
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
            if (uiSoftReference.get().splashActivityInstance == null)
                return;
            if (PlugApkUtils.isPlugDownloadFinish && !PlugApkUtils.isPlugLoadFinish) {
                startNextActivity(5000);  // 给更多时间让补丁Apk加载完成，替换MainActivity的Bug
                return;
            }
            uiSoftReference.get().splashActivityInstance.startActivity(new Intent(uiSoftReference.get().splashActivityInstance, MainActivity.class));
            uiSoftReference.get().splashActivityInstance.finish();
        }, delayTime);
    }

    public void plugLoadFinish() {
        // 补丁包已经加载完成
        startNextActivity(3000);
    }
}
