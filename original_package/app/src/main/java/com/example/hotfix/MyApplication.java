package com.example.hotfix;

import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.example.hotfix.utils.device.deviceOnlyCode.DeviceCodeUtil;
import com.example.hotfix.utils.retrofitUtils.downloadpg.SoUtils;
import com.example.hotfix.utils.UiUtils;
import com.example.hotfix.utils.retrofitUtils.HttpRequestUrls;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/1/3       Sunny          请填写变更内容
 */
public class MyApplication extends MultiDexApplication {
    public static MyApplication instance;
    public static boolean isSoLibDownloading = false;
    // 获得刘海适配的顶部距离高度
    public static int fixTitleBarHeight;
    // 设备唯一信息
    public static String PHONE_ONLY_INFO = "";

    @Override
    public void onCreate() {
        // 正式or测试
        HttpRequestUrls.CurrentHost = HttpRequestUrls.TestHost;
        // 记录contex公共实体
        instance = this;

        // 获得刘海适配的顶部距离高度
        fixTitleBarHeight = UiUtils.getStatusBarHeight(this) - UiUtils.dp2px(10);

        // 根据不同的cpu架构适配下载不同的so包（用于So库动态加载）
        SoUtils.checkCpuType();

        super.onCreate();

        // 获取手机标示信息
        PHONE_ONLY_INFO = DeviceCodeUtil.getDeviceCode(this);

        // Android7.0以上有关URI格式的异常判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    public void onTrimMemory(int level) { // 手机运行内存告急，后台应用即将被kill
        System.gc();
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() { // 手机运行内存告急，应用已经被kill
        System.gc();
        super.onLowMemory();
    }
}
