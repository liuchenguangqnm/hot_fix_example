package com.example.hotfix.utils.loadPlugApkUtils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.hotfix.MyApplicationPlug;
import com.example.hotfix.utils.loadPlugApkUtils.Proxies.ProxyActivity;
import com.example.hotfix.utils.loadPlugApkUtils.manifestparcer.IntentFilterParser;
import com.example.hotfix.utils.loadPlugApkUtils.plugApkClassLoder.MultiDexLoader;
import com.example.hotfix.utils.loadPlugApkUtils.plugApkClassLoder.DownSourceManager;

import java.io.File;
import java.util.Map;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/10/3      Sunshine         插件App安装工具
 */
public class PlugAppInstallUtil {
    // 是否在加载插件apk成功后直接打开MainActivity
    public static boolean isAutoStartMainActivity = false;
    // 插件apk的资源，开启插件apk之前要用它替换主activity的资源，否则加载布局和图片错误
    public static Resources apkResources;
    // 插件MainActivity的全类名
    public static String outSideApkMainActivity = "";

    public static void installApk(boolean isAutoStartMainActivity, String downUrl, String fileName) {
        PlugAppInstallUtil.isAutoStartMainActivity = isAutoStartMainActivity;
        MultiDexLoader.install(MyApplicationPlug.instance, downUrl, fileName);// 加载assets中的apk
        System.out.println("debug:HostApplication onCreate"); // 加载完成
        // 获取插件apk包含的所有activities
        getOutSideApkActivities(fileName, isAutoStartMainActivity);
    }

    /**
     * 获取所有的插件activityInfo
     *
     * @param apkFileName
     * @return
     */
    private static ActivityInfo[] getOutSideApkActivities(String apkFileName, boolean isAutoStartMainActivity) {
        // 获取插件apk的所有activities
        ActivityInfo[] activities = new ActivityInfo[0];
        File apkParentFile = MyApplicationPlug.instance.getDir(DownSourceManager.APK_DIR, Context.MODE_PRIVATE);
        File apkFile = new File(apkParentFile, "/" + apkFileName);
        if (!apkFile.exists()) {
            return activities;
        }
        PackageManager pm = MyApplicationPlug.instance.getPackageManager();
        // PackageInfo info = pm.getPackageArchiveInfo(Environment.getExternalStorageDirectory()+"/TianjinPlugin3.apk", PackageManager.GET_ACTIVITIES);
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            // setTheme(appInfo.theme); // TODO 怎样获取到插件apk的theme？
            if (appInfo != null) {
                activities = info.activities;
            }
        }
        // 加载插件 apk 包的 Resources 资源对象
        apkResources = getApkResources(apkFile.getAbsolutePath());
        // 插件MainActivity的全类名
        outSideApkMainActivity = saveMainActivity(apkFile, isAutoStartMainActivity);
        return activities;
    }

    /**
     * 加载插件 apk 包的 Resources 资源对象
     */
    private static Resources getApkResources(String apkPath) {
        Resources resources = null;
        try {
            // 首先创建 AssetManager 对象
            AssetManager assetManager = AssetManager.class.newInstance();
            // 通过反射 AssetManager 的 addAssetPath 方法，来加载指定 apk 包的 Resources 对象
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(assetManager, apkPath);
            Resources HostAppResource = MyApplicationPlug.instance.getResources();
            // 获得 Resources 对象
            resources = new Resources(assetManager, HostAppResource.getDisplayMetrics(), HostAppResource.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resources;
    }

    /**
     * 记录主页面activity全类名
     *
     * @param apkFile
     * @return
     */
    private static String saveMainActivity(File apkFile, boolean isAutoStartMainActivity) {
        if (apkFile.exists()) {
            IntentFilterParser parser = new IntentFilterParser(apkFile.getAbsolutePath());
            Map<String, IntentFilter> androidManifestIntentFilter = parser.getIntentFilter();
            for (Map.Entry<String, IntentFilter> stringIntentFilterEntry : androidManifestIntentFilter.entrySet()) {
                IntentFilter intentFilter = stringIntentFilterEntry.getValue();
                String action = null;
                String category = null;
                try {
                    action = intentFilter.getAction(0);
                    category = intentFilter.getCategory(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (action != null && category != null) {
                    if (action.equals("android.intent.action.MAIN") && category.equals("android.intent.category.LAUNCHER")) {
                        PlugAppInstallUtil.isAutoStartMainActivity = isAutoStartMainActivity;
                        // 成功获取插件主页面的全类名，予以记录
                        return stringIntentFilterEntry.getKey();
                    }
                }
            }
        }

        return "";
    }

    /**
     * 通过全类名开启一个插件apk的页面
     *
     * @param activityInstance
     * @param totalActivityClassName
     */
    public static void startNewActivityByTotalName(Context activityInstance, String totalActivityClassName) {
        try {
            Class<?> clazz = Class.forName(totalActivityClassName);
            Intent intent = new Intent(activityInstance, ProxyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("activityClass", clazz);
            intent.putExtras(bundle);
            activityInstance.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
