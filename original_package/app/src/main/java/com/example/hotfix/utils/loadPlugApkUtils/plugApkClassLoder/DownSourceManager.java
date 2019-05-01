package com.example.hotfix.utils.loadPlugApkUtils.plugApkClassLoder;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * 下载apk路径存储管理器
 */
public class DownSourceManager {
    public static final String TAG = "AssetsApkLoader";
    // 从下载apk存放路径复制出去的apk的目标目录
    public static final String APK_DIR = "bundle_apk";
    // 文件名称过滤
    public String fileNameFilter = "plug-util.apk";
    // 文件结尾过滤
    public static final String FILE_FILTER = ".apk";
    // 管理器实例集合
    private static HashMap<String, DownSourceManager> managerDic = new HashMap<>();

    public static DownSourceManager getInstance(String totalDownUrl, String fileName) {
        if (managerDic.get(totalDownUrl) == null) {
            DownSourceManager downSourceManager = new DownSourceManager(fileName);
            managerDic.put(totalDownUrl, downSourceManager);
            return downSourceManager;
        } else {
            return managerDic.get(totalDownUrl);
        }
    }

    private DownSourceManager(String fileName) {
        fileNameFilter = fileName;
    }

    /**
     * 将资源文件中的apk文件拷贝到私有目录中
     *
     * @param context
     */
    public void copyDownLoadedApk(Context context) {

        long startTime = System.currentTimeMillis();
        try {
            File dex = context.getDir(APK_DIR, Context.MODE_PRIVATE);
            dex.mkdir();
            String[] fileNames = new File(context.getFilesDir().getPath() + File.separator + "download").list();
            for (String fileName : fileNames) {
                System.out.println("debug:fileName = " + fileName);
                if (!fileName.equals(fileNameFilter)) {
                    continue;
                }
                InputStream in = null;
                OutputStream out = null;
                in = new BufferedInputStream(new FileInputStream(context.getFilesDir().getPath() + File.separator + "download" + File.separator + fileName));
                File f = new File(dex, fileName);
                if (f.exists() && f.length() == in.available()) {
                    Log.i(TAG, fileName + "no change");
                    return;
                }
                Log.i(TAG, fileName + " chaneged");
                out = new FileOutputStream(f);
                byte[] buffer = new byte[2048];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                Log.i(TAG, fileName + " copy over");
            }
            Log.i(TAG, "debug:copyAssets time = " + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
