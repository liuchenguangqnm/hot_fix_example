package com.example.hotfix.utils.retrofitUtils.downloadpg;

import android.util.Log;

import com.example.hotfix.MyApplication;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/9/1        Sunshine          请填写变更内容
 */

public class DownloadUtil {
    /**
     * 检测文件是否已经下载完成了
     *
     * @param totalUrl         完整的下载url
     * @param downLoadFileType 下载文件类型
     * @return
     */
    public static boolean checkOrDownloadFinishFile(String totalUrl, int downLoadFileType) {
        if (totalUrl.equals(""))
            return false;
        System.gc(); // 手动清下内存，照顾老手机
        DownInfo downInfo = DownInfo.getInstance(totalUrl, downLoadFileType);
        String savePath = downInfo.getSavePath();
        File savePathFile = new File(savePath);
        long countLength = downInfo.getCountLength();
        long savePathLenth = savePathFile.length();
        long readLength = downInfo.getReadLength();
        if (savePathFile.exists() && countLength == savePathLenth && readLength == savePathLenth) {
            Log.i("文件下载相关", "应用已经可以不经过下载直接打开下载文件 ========== " + downInfo.getUrl());
            return true;
        } else {
            // 开启文件的下载
            if (!savePathFile.exists()) { // 如果此文件不存在，就要删除所有缓存重新下一遍
                Log.i("文件下载相关", "文件已经不存在删除之重新下载");
                savePathFile.delete();
                DownInfo.clearDwonLoadInfo(totalUrl); // 清空之前的缓存进度，从零开始下
            }
            return false;  // 返回false，指示重新下载文件
        }
    }

    /**
     * 写入下载完成的文件
     *
     * @param file
     * @param info
     */
    public static void writeCache(ResponseBody responseBody, File file, DownInfo info) {
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            long allLength;
            if (info.getCountLength() == 0) {
                allLength = responseBody.contentLength();
            } else {
                allLength = info.getCountLength();
            }
            FileChannel channelOut = null;
            RandomAccessFile randomAccessFile = null;
            randomAccessFile = new RandomAccessFile(file, "rwd");
            channelOut = randomAccessFile.getChannel();
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, info.getReadLength(), allLength - info.getReadLength());
            byte[] buffer = new byte[1024 * 8];
            int len;
            int record = 0;
            while ((len = responseBody.byteStream().read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
                record += len;
            }
            responseBody.byteStream().close();
            if (channelOut != null) {
                channelOut.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
