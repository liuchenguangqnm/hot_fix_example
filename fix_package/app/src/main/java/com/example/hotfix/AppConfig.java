package com.example.hotfix;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/8/18.
 */
public class AppConfig {
    public static String APP_VERSION_NAME = "0.0";
    public static int APP_VERSION_CODE = 0;

    // loading bg radios
    public static int blurRadius = 5;
    // loading bg scale
    public static float blurScale = 1f / 4f;

    static { // 静态代码块儿初始化所需数据
        initVersion();
    }

    public static void initVersion() {
        try {
            PackageManager packageManager = MyApplicationPlug.instance.getPackageManager();
            PackageInfo packageInfo = null;
            packageInfo = packageManager.getPackageInfo(MyApplicationPlug.instance.getPackageName(), 0);
            APP_VERSION_NAME = packageInfo.versionName;
            APP_VERSION_CODE = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}