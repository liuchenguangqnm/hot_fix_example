package com.example.hotfix.utils.retrofitUtils.downloadpg;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.hotfix.MyApplication;
import com.example.hotfix.ui.activity.MainActivity;
import com.example.hotfix.utils.device.deviceOnlyCode.SystemPropertiesProxy;
import com.example.hotfix.utils.retrofitUtils.RetrofitManager;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import dalvik.system.PathClassLoader;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/4/30       Sunny          后台So库下载相关
 */
public class SoUtils {
    /**
     * 检测so库是否已经下载完成了
     *
     * @param totalUrl
     */
    public static void checkOrDownloadSo(String totalUrl) {
        if (totalUrl.equals(""))
            return;
        System.gc(); // 手动清下内存，照顾老手机
        DownInfo downInfo = DownInfo.getInstance(totalUrl, DownInfo.SO_LIBRARY);
        String savePath = downInfo.getSavePath();
        File savePathFile = new File(savePath);
        long countLength = downInfo.getCountLength();
        long savePathLenth = savePathFile.length();
        long readLength = downInfo.getReadLength();
        if (savePathFile.exists() && countLength == savePathLenth && readLength == savePathLenth) {
            Log.i("文件下载相关-so", "应用已经可以不经过下载直接加载so库 ========== " + downInfo.getUrl());
            createNewNativeDir(MyApplication.instance, downInfo.getUrl());
            // 加载so库;
            File destFile = new File(downInfo.getSavePath());
            // 使用load方法加载内部储存的SO库（每次用到的时候再加载，否则每次开App加载会导致一些手机慢）
            System.load(destFile.getAbsolutePath());
            MyApplication.isSoLibDownloading = false;
        } else {
            // 开启So库的下载
            MyApplication.isSoLibDownloading = true;
            if (!savePathFile.exists()) { // 如果so库文件不存在，就要删除所有缓存重新下一遍
                Log.i("文件下载相关-so", "so文件已经不存在删除之重新下载");
                savePathFile.delete();
                DownInfo.clearDwonLoadInfo(totalUrl);
            }
            MainActivity.mainActivityInstance.presenterImpl.requestDownLoadFromWeb(totalUrl,
                    (long progress, long total, boolean done, DownInfo downingInfo) -> {
                        int rate = (int) (progress * 1.0 / total * 100 + .5);
                        Log.i("文件下载相关-so", rate + "%");
                        if (done) { // 下载完毕，加载so库
                            Log.i("文件下载相关-so", "开始加载so库 ========== " + downingInfo.getUrl());
                            createNewNativeDir(MyApplication.instance, downingInfo.getUrl());
                            // 加载so库;
                            File destFile = new File(downingInfo.getSavePath());
                            // 使用load方法加载内部储存的SO库
                            System.load(destFile.getAbsolutePath());
                            MyApplication.isSoLibDownloading = false;
                        }
                    }, DownInfo.SO_LIBRARY);
        }
    }

    public static void checkCpuType() {
        String cpuOS = SystemPropertiesProxy.get(MyApplication.instance, "ro.product.cpu.abi");
        if (cpuOS.equals("arm64-v8a")) {
            Log.i("so库加载相关", "arm64v8手机");
        } else if (cpuOS.contains("arm")) {
            Log.i("so库加载相关", "arm7手机");
        }
    }

    /**
     * 关于系统目录找不到so库的问题，解决办法：
     * 1、把App动态加载so的目录加入到系统目录列表nativeLibraryDirectories
     * 2、删除第三方项目和jar包里面的 loadLibrary 语句
     *
     * @param context
     * @throws Exception
     */
    public static void createNewNativeDir(Context context, String totalUrl) {
        try {
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Field declaredField = Class.forName("dalvik.system.BaseDexClassLoader").getDeclaredField("pathList");
            declaredField.setAccessible(true);
            Object pathList = declaredField.get(pathClassLoader);
            // 获取当前类的属性
            Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            ((Field) nativeLibraryDirectories).setAccessible(true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // 获取 DEXPATHList中的属性值
                File[] files = (File[]) ((Field) nativeLibraryDirectories).get(pathList);
                Object filesss = Array.newInstance(File.class, files.length + 1);
                // 添加自定义.so路径
                Array.set(filesss, 0, getDownloadFileDir(totalUrl, DownInfo.SO_LIBRARY));
                // 将系统自己的追加上
                for (int i = 1; i < files.length + 1; i++) {
                    Array.set(filesss, i, files[i - 1]);
                }
                ((Field) nativeLibraryDirectories).set(pathList, filesss);
            } else {
                ArrayList<File> files1 = (ArrayList<File>) ((Field) nativeLibraryDirectories).get(pathList);
                ArrayList<File> files2 = (ArrayList<File>) files1.clone();
                files2.add(0, getDownloadFileDir(totalUrl, DownInfo.SO_LIBRARY));
                ((Field) nativeLibraryDirectories).set(pathList, files2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getDownloadFileDir(String totalUrl, int downLoadFileType) {
        DownInfo downInfo = DownInfo.getInstance(totalUrl, downLoadFileType);
        if (downInfo != null) {
            return new File(downInfo.getSavePath());
        }
        return null;
    }

    /**
     * 停止对应url的下载操作
     */
    public static void stopDownload(String totalUrl) {
        RetrofitManager.stopDownload(totalUrl, DownInfo.SO_LIBRARY);
    }


    //获取总内存大小
    public static float getTotalMemorySize(Context context) {
        float size = 0;
        //获取ActivityManager管理，要获取【运行相关】的信息，与运行相关的信息有关
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();//outInfo对象里面包含了内存相关的信息
        activityManager.getMemoryInfo(outInfo);//把内存相关的信息传递到outInfo里面C++思想

        size = (outInfo.totalMem * 1.0f) / 1024 / 1024 / 1024; // (单位GB)
        //通过读取配置文件方式获取总内大小。文件目录：/proc/meminfo

        return size;
    }
}
