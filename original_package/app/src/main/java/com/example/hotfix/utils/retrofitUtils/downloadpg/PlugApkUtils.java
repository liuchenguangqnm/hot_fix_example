package com.example.hotfix.utils.retrofitUtils.downloadpg;

import android.util.Log;
import android.widget.Toast;

import com.example.hotfix.MyApplication;
import com.example.hotfix.base.baseui.BaseActivity;
import com.example.hotfix.base.baseui.BaseActivityFullScreen;
import com.example.hotfix.base.baseui.BaseFragmentActivity;
import com.example.hotfix.ui.activity.SplashActivity;
import com.example.hotfix.utils.loadPlugApkUtils.PlugAppInstallUtil;
import com.example.hotfix.utils.loadPlugApkUtils.plugApkClassLoder.DownSourceManager;
import com.example.hotfix.utils.retrofitUtils.RetrofitManager;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/10/3     Sunshine          插件apk下载工具类
 */
public class PlugApkUtils {
    public static boolean isPlugDownloadFinish = false;  // 插件apk是否已经下载完成
    public static boolean isPlugLoadFinish = false;      // 插件apk是否已经加载完成

    public static void checkPlugApk(BaseActivity baseActivity, String downUrl, String fileName) {
        if (isPlugLoadFinish) // 判断当前插件是否已经加载完成了，加载过的无需再次加载
            return;
        if (fileName == null || fileName.equals("")) {
            Toast.makeText(MyApplication.instance, "被检测的插件apk文件名不可为空！", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(() -> {
            if (baseActivity != null)
                // 如果插件包已经下载成功了，就直接加载这个插件
                if (DownloadUtil.checkOrDownloadFinishFile(downUrl, DownInfo.APK_FILE)) {
                    isPlugDownloadFinish = true;
                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                    // ui处理
                    loadPlugFinish(baseActivity, downUrl, fileName);
                } else { // 否则下载插件包
                    isPlugDownloadFinish = false;
                    baseActivity.presenterImpl.requestDownLoadFromWeb(downUrl,
                            DownSourceManager.getInstance(downUrl, fileName).fileNameFilter.replace(".apk", ""),
                            (long progress, long total, boolean done, DownInfo downInfo) -> {
                                int intProgress = (int) (progress * 1.0 / total * 100);
                                if (done) { // 下载完成，加载apk插件
                                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                                    // ui处理
                                    loadPlugFinish(baseActivity, downUrl, fileName);
                                }
                            }, DownInfo.APK_FILE);
                }
        }).start();
    }

    public static void checkPlugApk(BaseActivityFullScreen baseActivityFullScreen, String downUrl, String fileName) {
        if (isPlugLoadFinish) // 判断当前插件是否已经加载完成了，加载过的无需再次加载
            return;
        if (fileName == null || fileName.equals("")) {
            Toast.makeText(MyApplication.instance, "被检测的插件apk文件名不可为空！", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(() -> {
            if (baseActivityFullScreen != null)
                // 如果插件包已经下载成功了，就直接加载这个插件
                if (DownloadUtil.checkOrDownloadFinishFile(downUrl, DownInfo.APK_FILE)) {
                    isPlugDownloadFinish = true;
                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                    // ui处理
                    loadPlugFinish(baseActivityFullScreen, downUrl, fileName);
                } else { // 否则下载插件包
                    isPlugDownloadFinish = false;
                    baseActivityFullScreen.presenterImpl.requestDownLoadFromWeb(downUrl,
                            DownSourceManager.getInstance(downUrl, fileName).fileNameFilter.replace(".apk", ""),
                            (long progress, long total, boolean done, DownInfo downInfo) -> {
                                int intProgress = (int) (progress * 1.0 / total * 100);
                                if (done) { // 下载完成，加载apk插件
                                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                                    // ui处理
                                    loadPlugFinish(baseActivityFullScreen, downUrl, fileName);
                                }
                            }, DownInfo.APK_FILE);
                }
        }).start();
    }

    public static void checkPlugApk(BaseFragmentActivity baseFragmentActivity, String downUrl, String fileName) {
        if (isPlugLoadFinish) { // 判断当前插件是否已经加载完成了，加载过的无需再次加载
            Toast.makeText(MyApplication.instance, "修复包已经成功加载", Toast.LENGTH_LONG).show();
            return;
        }
        if (fileName == null || fileName.equals("")) {
            Toast.makeText(MyApplication.instance, "被检测的插件apk文件名不可为空！", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(() -> {
            if (baseFragmentActivity != null)
                // 如果插件包已经下载成功了，就直接加载这个插件
                if (DownloadUtil.checkOrDownloadFinishFile(downUrl, DownInfo.APK_FILE)) {
                    isPlugDownloadFinish = true;
                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                    // ui处理
                    loadPlugFinish(baseFragmentActivity, downUrl, fileName);
                } else { // 否则下载插件包
                    isPlugDownloadFinish = false;
                    baseFragmentActivity.presenterImpl.requestDownLoadFromWeb(downUrl,
                            DownSourceManager.getInstance(downUrl, fileName).fileNameFilter.replace(".apk", ""),
                            (long progress, long total, boolean done, DownInfo downInfo) -> {
                                int intProgress = (int) (progress * 1.0 / total * 100);
                                if (done) { // 下载完成，加载apk插件
                                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                                    // ui处理
                                    loadPlugFinish(baseFragmentActivity, downUrl, fileName);
                                }
                            }, DownInfo.APK_FILE);
                }
        }).start();
    }

    /**
     * 插件加载完成的ui处理
     */
    private static void loadPlugFinish(BaseActivity baseActivity, String downUrl, String fileName) {
        if (baseActivity != null && baseActivity.presenterImpl != null) {
            baseActivity.runOnUiThread(() -> {
                Toast.makeText(MyApplication.instance, "修复包已经完成加载 重开应用可见效果", Toast.LENGTH_LONG).show();
                isPlugLoadFinish = true;
                if (baseActivity instanceof SplashActivity) {
                    SplashActivity splashActivityInstance = (SplashActivity) baseActivity;
                    splashActivityInstance.presenterImpl.plugLoadFinish();
                }
            });
        }
    }

    /**
     * 插件加载完成的ui处理
     */
    private static void loadPlugFinish(BaseActivityFullScreen baseActivityFullScreen, String downUrl, String fileName) {
        if (baseActivityFullScreen != null && baseActivityFullScreen.presenterImpl != null) {
            baseActivityFullScreen.runOnUiThread(() -> {
                Toast.makeText(MyApplication.instance, "修复包已经完成加载 重开应用可见效果", Toast.LENGTH_LONG).show();
                isPlugLoadFinish = true;
            });
        }
    }

    /**
     * 插件加载完成的ui处理
     */
    private static void loadPlugFinish(BaseFragmentActivity baseFragmentActivity, String downUrl, String fileName) {
        if (baseFragmentActivity != null && baseFragmentActivity.presenterImpl != null) {
            baseFragmentActivity.runOnUiThread(() -> {
                Toast.makeText(MyApplication.instance, "修复包已经完成加载 重开应用可见效果", Toast.LENGTH_LONG).show();
                isPlugLoadFinish = true;
            });
        }
    }

    /**
     * 停止对应url的下载操作
     */
    public static void stopDownload(String totalUrl) {
        RetrofitManager.stopDownload(totalUrl, DownInfo.APK_FILE);
    }

    /**
     * 通过提取apk文件中的dex来完成插件化的方法
     * TODO 另一种貌似比现在插件化更简易的动态加载的方法(还没有系统测试过)
     */
    private void loadPlugClassByApkDex(String downUrl) {
        try {
            String savePath = DownInfo.getInstance(downUrl, DownInfo.APK_FILE).getSavePath();
            File dexOutputDir = MyApplication.instance.getDir("dex", 0);
            DexClassLoader classLoader = new DexClassLoader(savePath, dexOutputDir.getAbsolutePath(), null, MyApplication.instance.getClassLoader());
            Class mLoadClass = null;
            mLoadClass = classLoader.loadClass("想要load的全类名");
            for (Method method : mLoadClass.getMethods()) {
                Log.i("PlugApkUtils", method.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
