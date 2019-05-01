package com.example.hotfix.utils.retrofitUtils.downloadManagerUtil;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.hotfix.MyApplication;

import java.lang.ref.SoftReference;

public class DownloadChangeObserver extends ContentObserver {
    private SoftReference<Handler> handlerSoftReference;
    public long downloadId = -1;
    private boolean isContinue = true;

    public DownloadChangeObserver(Handler handler) {
        super(handler);
        this.handlerSoftReference = new SoftReference<Handler>(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        new Handler().postDelayed(() ->
                new Thread(() -> {
                    if (downloadId != -1) {
                        try {
                            isContinue = true;
                            while (isContinue) {
                                float progress = getBytesAndStatus(downloadId);
                                if (handlerSoftReference != null && handlerSoftReference.get() != null) {
                                    Message msg = Message.obtain();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("progress", (int) (progress * 100 + .5));
                                    msg.setData(bundle);
                                    handlerSoftReference.get().sendMessage(msg);
                                }
                                if (progress >= 1) {
                                    isContinue = false;
                                }
                                Thread.sleep(800);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                }).start(), 1000);
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态 * * @param downloadId * @return
     */
    private float getBytesAndStatus(long downloadId) {
        float returnProgress = 0;
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            DownloadManager downloadManager = (DownloadManager) MyApplication.instance.getSystemService(Context.DOWNLOAD_SERVICE);
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                // 计算下载进度
                float downloaded = bytesAndStatus[0] * 1.0f;
                float total = bytesAndStatus[1] * 1.0f;
                if (bytesAndStatus[0] != -1 && bytesAndStatus[1] != -1) {
                    returnProgress = downloaded / total;
                }
                // 检测下载状态
                int status = bytesAndStatus[2];
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int reason = cursor.getInt(columnReason);
                checkDownloadStatus(status, reason);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return returnProgress;
    }

    /**
     * 检测下载状态
     */
    private void checkDownloadStatus(int status, int reason) {
        switch (status) {
            case DownloadManager.STATUS_FAILED:
                // 下载出错，ui停止继续下载
                isContinue = false;
                if (handlerSoftReference != null && handlerSoftReference.get() != null) {
                    Message msg = Message.obtain();
                    msg.obj = DownloadManager.STATUS_FAILED;
                    handlerSoftReference.get().sendMessage(msg);
                }
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        //some possibly transient error occurred but we can't resume the download
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        //no external storage device was found. Typically, this is because the SD card is not mounted
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        //the requested destination file already exists (the download manager will not overwrite an existing file)
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        //a storage issue arises which doesn't fit under any other error code
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        //an error receiving or processing data occurred at the HTTP level
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE://sd卡满了
                        //here was insufficient storage space. Typically, this is because the SD card is full
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        //there were too many redirects
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        //an HTTP code was received that download manager can't handle
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        //he download has completed with an error that doesn't fit under any other error code
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                // 下载出错，ui停止继续下载
                isContinue = false;
                if (handlerSoftReference != null && handlerSoftReference.get() != null) {
                    Message msg = Message.obtain();
                    msg.obj = DownloadManager.STATUS_PAUSED;
                    handlerSoftReference.get().sendMessage(msg);
                }
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        //the download exceeds a size limit for downloads over the mobile network and the download manager is waiting for a Wi-Fi connection to proceed
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        //the download is paused for some other reason
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        //the download is waiting for network connectivity to proceed
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        //the download is paused because some network error occurred and the download manager is waiting before retrying the request
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                //the download is waiting to start
                break;
            case DownloadManager.STATUS_RUNNING:
                //the download is currently running
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                //the download has successfully completed
                break;
        }
    }

}