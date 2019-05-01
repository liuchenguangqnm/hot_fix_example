package com.example.hotfix.utils.retrofitUtils.downloadpg;

import com.example.hotfix.utils.retrofitUtils.RetrofitManager;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by ljd on 3/29/16.
 */
public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;
    private final DownInfo downInfo;
    private final long lastReadLength; // 上一次断点续传读取的长度

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener, String totalUrl, int downLoadFileType) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        // 获取断点续传上一个字节的下载位置
        this.downInfo = DownInfo.getInstance(totalUrl, downLoadFileType);
        lastReadLength = downInfo.getReadLength();
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;      // 初始化网络请求的读取长度
            long realTotalReadLength = 0L; // 初始化真实的读取长度

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                long total = responseBody.contentLength();
                if (progressListener != null) {
                    DownInfo downInfo = DownInfo.getInstance(ProgressResponseBody.this.downInfo.getUrl(),
                            ProgressResponseBody.this.downInfo.downLoadFileType);
                    // 修改downloadinfo的数据
                    if (downInfo.getCountLength() > total) { // 如果已经保存的total数比接口返回的大，说明这是断点续传
                        // 此时读取字节数需要加工处理
                        realTotalReadLength = lastReadLength + totalBytesRead;
                    } else {
                        downInfo.setCountLength(total);
                        realTotalReadLength = totalBytesRead;
                    }
                    downInfo.setReadLength(realTotalReadLength);
                    // 设置进度
                    progressListener.onProgress(realTotalReadLength, downInfo.getCountLength(), bytesRead == -1, downInfo);
                    if (bytesRead == -1) { // 如果下载完成，清除缓存的subscriber
                        // 清除subscriber缓存
                        RetrofitManager.stopDownload(downInfo.getUrl(), downInfo.downLoadFileType);
                    }
                }
                return bytesRead;
            }
        };
    }
}