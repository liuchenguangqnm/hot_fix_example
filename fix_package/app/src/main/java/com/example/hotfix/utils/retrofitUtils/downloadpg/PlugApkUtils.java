package com.example.hotfix.utils.retrofitUtils.downloadpg;

import android.util.Log;
import android.widget.Toast;

import com.example.hotfix.MyApplicationPlug;
import com.example.hotfix.ui.activity.MainActivity;
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
    private static boolean isPlugLoadFinish = false;

    public static void checkPlugApk(String downUrl, String fileName) {
        if (isPlugLoadFinish) // 判断当前插件是否已经加载完成了，加载过的无需再次加载
            return;
        if (fileName == null || fileName.equals("")) {
            Toast.makeText(MyApplicationPlug.instance, "被检测的插件apk文件名不可为空！", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(() -> {
            if (MainActivity.mainActivityInstance != null)
                // 如果插件包已经下载成功了，就直接加载这个插件
                if (DownloadUtil.checkOrDownloadFinishFile(downUrl, DownInfo.APK_FILE, false)) {
                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                    // ui处理
                    loadPlugFinish(MainActivity.mainActivityInstance, downUrl, fileName);
                } else { // 否则下载插件包
                    MainActivity.mainActivityInstance.presenterImpl.requestDownLoadFromWeb(downUrl,
                            DownSourceManager.getInstance(downUrl, fileName).fileNameFilter.replace(".apk", ""),
                            (long progress, long total, boolean done, DownInfo downInfo) -> {
                                int intProgress = (int) (progress * 1.0 / total * 100);
                                if (done) { // 下载完成，加载apk插件
                                    PlugAppInstallUtil.installApk(false, downUrl, fileName);
                                    // ui处理
                                    loadPlugFinish(MainActivity.mainActivityInstance, downUrl, fileName);
                                }
                            }, DownInfo.APK_FILE);
                }
        }).start();
    }

    /**
     * 插件加载完成的ui处理
     *
     * @param mainActivityInstance
     */
    private static void loadPlugFinish(MainActivity mainActivityInstance, String downUrl, String fileName) {
        isPlugLoadFinish = true;
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
            File dexOutputDir = MyApplicationPlug.instance.getDir("dex", 0);
            DexClassLoader classLoader = new DexClassLoader(savePath, dexOutputDir.getAbsolutePath(), null, MyApplicationPlug.instance.getClassLoader());
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
