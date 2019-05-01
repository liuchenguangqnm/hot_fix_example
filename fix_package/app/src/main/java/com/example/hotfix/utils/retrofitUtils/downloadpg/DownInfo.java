package com.example.hotfix.utils.retrofitUtils.downloadpg;

import android.widget.Toast;

import com.google.gson.Gson;
import com.example.hotfix.MyApplicationPlug;
import com.example.hotfix.utils.SharePerferenceUtil;

import java.util.HashMap;

/**
 * apk下载请求数据基础类
 * Created by WZG on 2016/10/20.
 */

public class DownInfo {
    // 下载的文件类型
    public static int SO_LIBRARY = 0x111111;
    public static int APK_FILE = 0x222222;
    private static Gson gson;
    /*存储位置*/
    private String savePath = "";
    /*下载url*/
    private String url;
    /*文件总长度*/
    private long countLength;
    /*下载长度*/
    private long readLength;
    /*超时设置*/
    private int DEFAULT_TIMEOUT = 6;
    /*下载唯一的HttpService*/
    private HttpService service;
    // 设定文件下载类型
    public int downLoadFileType = APK_FILE;
    // 文件名称
    public String fileName = "";
    // 下载信息实例字典
    private static HashMap<String, DownInfo> downInfoHashMap = new HashMap<>();

    /**
     * 初始化下载信息实体的两种方法
     */
    public static DownInfo getInstance(String totalUrl, int requestType, String fileName) {
        if (requestType == SO_LIBRARY) {
            Toast.makeText(MyApplicationPlug.instance, "So库动态加载不可以自定义文件名！", Toast.LENGTH_LONG);
            return null;
        }
        DownInfo instance = getInstance(totalUrl, requestType);
        if (instance.fileName.equals("") && fileName != null)
            instance.fileName = fileName;
        instance.setUrl(totalUrl);
        return instance;
    }

    public static DownInfo getInstance(String totalUrl, int requestType) {
        DownInfo instance = null;
        if (gson == null)
            gson = new Gson();
        if (!downInfoHashMap.containsKey(totalUrl)) {
            String soDownloadInfo = SharePerferenceUtil.getVaule(MyApplicationPlug.instance, "file_down_load", totalUrl);
            if (!soDownloadInfo.equals("")) {
                try {
                    instance = gson.fromJson(soDownloadInfo, DownInfo.class);
                    downInfoHashMap.put(totalUrl, instance);
                } catch (Exception e) {
                    e.printStackTrace();
                    /** 避免由于版本跟新本地缓存数据结构而导致的读取异常 */
                    // 清空本地缓存数据
                    SharePerferenceUtil.saveValue(MyApplicationPlug.instance, "file_down_load", totalUrl, "");
                    if (totalUrl.startsWith("http://") || totalUrl.startsWith("https://")) {
                        instance = new DownInfo(totalUrl);
                        instance.downLoadFileType = requestType;
                        downInfoHashMap.put(totalUrl, instance);
                    } else {
                        Toast.makeText(MyApplicationPlug.instance, "参数必须填写完整的下载地址！", Toast.LENGTH_LONG);
                        return null;
                    }
                }
            } else {
                if (totalUrl.startsWith("http://") || totalUrl.startsWith("https://")) {
                    instance = new DownInfo(totalUrl);
                    instance.downLoadFileType = requestType;
                    downInfoHashMap.put(totalUrl, instance);
                } else {
                    Toast.makeText(MyApplicationPlug.instance, "参数必须填写完整的下载地址！", Toast.LENGTH_LONG);
                    return null;
                }
            }
        } else {
            instance = downInfoHashMap.get(totalUrl);
        }
        return instance;
    }

    public static DownInfo clearDwonLoadInfo(String totalUrl) {
        // 清空本地缓存数据
        SharePerferenceUtil.saveValue(MyApplicationPlug.instance, "file_down_load", totalUrl, "");
        DownInfo instance = new DownInfo(totalUrl);
        downInfoHashMap.put(totalUrl, instance);
        return instance;
    }

    public void saveInstance(String totalUrl) {
        DownInfo downInfo = downInfoHashMap.get(totalUrl);
        if (downInfo != null) {
            if (gson == null)
                gson = new Gson();
            String instanceJson = gson.toJson(downInfo);
            // 存储json
            SharePerferenceUtil.saveValue(MyApplicationPlug.instance, "file_down_load", totalUrl, instanceJson);
        } else {
            // 存储json
            SharePerferenceUtil.saveValue(MyApplicationPlug.instance, "file_down_load", totalUrl, "");
        }
    }

    private DownInfo(String totalUrl) {
        setUrl(totalUrl);
    }

    public int getConnectionTime() {
        return DEFAULT_TIMEOUT;
    }

    public void setConnectionTime(int DEFAULT_TIMEOUT) {
        this.DEFAULT_TIMEOUT = DEFAULT_TIMEOUT;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (downLoadFileType == SO_LIBRARY) {
            String[] split = url.split("/");
            String rawFileName = split[split.length - 1];
            int cutIndex = rawFileName.indexOf("_");
            savePath = MyApplicationPlug.instance.getFilesDir().getPath() + "/download/" + rawFileName.substring(cutIndex + 1, rawFileName.length());
        } else if (downLoadFileType == APK_FILE) {
            if (fileName.equals(""))
                savePath = MyApplicationPlug.instance.getFilesDir().getPath() + "/download/" + System.currentTimeMillis() + ".apk";
            else
                savePath = MyApplicationPlug.instance.getFilesDir().getPath() + "/download/" + fileName + ".apk";
        }
    }

    public String getSavePath() {
        return savePath;
    }

    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }


    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public HttpService getService() {
        return service;
    }


}
