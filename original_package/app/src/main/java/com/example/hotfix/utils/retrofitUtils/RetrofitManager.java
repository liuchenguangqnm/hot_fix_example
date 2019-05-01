package com.example.hotfix.utils.retrofitUtils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.example.hotfix.MyApplication;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.utils.retrofitUtils.MD5Util.MD5Util;
import com.example.hotfix.utils.retrofitUtils.RSAUtil.RSAUtils;
import com.example.hotfix.utils.retrofitUtils.downloadpg.DownloadUtil;
import com.example.hotfix.utils.retrofitUtils.downloadpg.DownInfo;
import com.example.hotfix.utils.retrofitUtils.downloadpg.HttpService;
import com.example.hotfix.utils.retrofitUtils.downloadpg.ProgressListener;
import com.example.hotfix.utils.retrofitUtils.downloadpg.ProgressResponseBody;
import com.example.hotfix.utils.retrofitUtils.exception.RetryWhenNetworkException;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Sunshine on 2017/12/25
 * Retrofit管理类
 */
public class RetrofitManager {
    private OkHttpClient mOkHttpClientQuery;
    private OkHttpClient mOkHttpClientBody;
    private OkHttpClient mOkHttpClientDownload;
    public Retrofit retrofitQuery;
    public Retrofit retrofitBody;
    public Retrofit retrofitDownload;
    // 普通请求订阅实体
    public Subscription normalRequestSubscription;
    // 下载回调字典，用于查找到对应的字典暂停下载（由于下载任务是全局性的，因此这里写static修饰）
    public static HashMap<DownInfo, Subscriber<DownInfo>> downloadSubscriberMap = new HashMap();

    public static RetrofitManager builder(String requestRx, boolean isQuery) {
        return new RetrofitManager(requestRx, null, "", isQuery, 0);
    }

    public static RetrofitManager builderDownload(ProgressListener progressListener, String totalUrl, int downLoadFileType) {
        return new RetrofitManager("", progressListener, totalUrl, false, downLoadFileType);
    }

    /**
     * 获取云端响应头拦截器（带下载进度监听的）
     */
    private Interceptor getDownloadInterReceptor(final ProgressListener progressListener,
                                                 final String totalUrl, int downLoadFileType) {
        // 云端响应头拦截器
        Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 下面可以添加请求头
                Request request = chain.request()
                        .newBuilder()
                        .build();
                Response originalResponse = chain.proceed(request);
                // 下载进度信息返回体
                ProgressResponseBody progressResponseBody = new ProgressResponseBody(originalResponse.body(), progressListener, totalUrl, downLoadFileType);
                return originalResponse.newBuilder().body(progressResponseBody).build();
            }
        };
        return mRewriteCacheControlInterceptor;
    }

    /**
     * 获取云端响应头拦截器（不带下载进度监听的 query）
     */
    public static Interceptor getNormalInterReceptorQuery(String requestRx) {
        // 获取请求头时间戳
        final String time = System.currentTimeMillis() + "";
        // 获取请求头rx加密请求参数
        String rxRsa = RSAUtils.rsaDecodeMethod(requestRx);
        String rxMd5 = MD5Util.getMD5(requestRx);
        // 正式生成拦截器
        Interceptor normalInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 添加请求头
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("time", time)
                        .addHeader("rxRsa", rxRsa)
                        .addHeader("rxMD5", rxMd5)
                        .build();
                return chain.proceed(request);
            }

        };
        return normalInterceptor;
    }

    /**
     * 获取云端响应头拦截器（不带下载进度监听的 body）
     */
    public static Interceptor getNormalInterReceptorBody(String requestRx) {
        // 获取请求头时间戳
        final String time = System.currentTimeMillis() + "";
        // 获取请求头rx加密请求参数
        String rxRsa = RSAUtils.rsaDecodeMethod(requestRx);
        String rxMd5 = MD5Util.getMD5(requestRx);
        // 正式生成拦截器
        Interceptor normalInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 添加请求头
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("time", time)
                        .addHeader("Content-Type", "application/json;charset=UTF-8")
                        .addHeader("rxRsa", rxRsa)
                        .addHeader("rxMD5", rxMd5)
                        .build();
                return chain.proceed(request);
            }
        };
        return normalInterceptor;
    }

    /**
     * 初始化OKHttpClient
     */
    private void initOkHttpClient(String requestRx, ProgressListener progressListener, String requestUrl, int downLoadFileType, boolean isQuery) {
        if (mOkHttpClientQuery == null && progressListener == null && isQuery) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClientQuery == null && progressListener == null) {
                    // 可用于log调试的拦截器
                    // HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(MyApplication.instance.getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);
                    mOkHttpClientQuery = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(getNormalInterReceptorQuery(requestRx))
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            // okHttp3信任所有的证书
                            .sslSocketFactory(createSSLSocketFactory())
                            .hostnameVerifier(new TrustAllHostnameVerifier())
                            .build();
                }
            }
        }
        if (mOkHttpClientBody == null && progressListener == null && !isQuery) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClientBody == null) {
                    // 可用于log调试的拦截器
                    // HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(MyApplication.instance.getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);
                    mOkHttpClientBody = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(getNormalInterReceptorBody(requestRx))
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            // okHttp3信任所有的证书
                            .sslSocketFactory(createSSLSocketFactory())
                            .hostnameVerifier(new TrustAllHostnameVerifier())
                            .build();
                }
            }
        }
        if (progressListener != null) {
            // 生成云响应拦截器
            Interceptor myInterReceptor = getDownloadInterReceptor(progressListener, requestUrl, downLoadFileType);
            mOkHttpClientDownload = new OkHttpClient.Builder()
                    .addInterceptor(myInterReceptor)           // 拦截器相关
                    // .addNetworkInterceptor(myInterReceptor)    // 拦截器相关
                    .connectTimeout(15, TimeUnit.SECONDS)
                    // okHttp3信任所有的证书
                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .build();
        }
    }

    /**
     * 配置Retrofit对象
     */
    private RetrofitManager(String requestRx, ProgressListener progressListener, String requestUrl, boolean isQuery, int downLoadFileType) {
        initOkHttpClient(requestRx, progressListener, requestUrl, downLoadFileType, isQuery);
        String baseUrl = "";
        // 初始化普通的网络请求Manager
        if (progressListener == null) {
            baseUrl = HttpRequestUrls.CurrentHost;
            if (isQuery) {
                retrofitQuery = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(mOkHttpClientQuery)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } else {
                retrofitBody = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(mOkHttpClientBody)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } else { // 初始化下载文件的Manager
            // 允许下载外来文件，首先从请求全地址中获取域名作为baseUrl
            String tempBaseUrl = requestUrl.replace("http://", "").replace("https://", "");
            tempBaseUrl = tempBaseUrl.substring(0, tempBaseUrl.indexOf("/"));
            if (requestUrl.startsWith("http://"))
                baseUrl = "http://" + tempBaseUrl;
            else if (requestUrl.startsWith("https://"))
                baseUrl = "https://" + tempBaseUrl;
            retrofitDownload = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(mOkHttpClientDownload)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    /**
     * 生成Service发起请求(普通，body，参数键值对表单)
     */
    public void startBaseRetrofitRequest(HashMap<String, String> requestParams, String requestUrl,
                                         Action1<BaseCallBackBean> callMethod, Subscriber<BaseCallBackBean> subscriver, Gson gson) {

        String requestJson = gson.toJson(requestParams).toString();
        Log.i("接口请求参数: " + requestUrl, requestParams.toString());

        Services serviceApi = retrofitBody.create(Services.class);
        Observable<BaseCallBackBean> baseCallBackBeanObservable = UrlRequestIntentFilter.requestUrl(requestJson, serviceApi, requestUrl);
        if (baseCallBackBeanObservable == null) {
            Log.i("Retrofit", "获取的Iservice为null");
            return;
        }

        normalRequestSubscription = baseCallBackBeanObservable.subscribeOn(Schedulers.newThread()) // 请求在新的线程中执行
                .observeOn(Schedulers.io()) // 请求完成后在io线程中执行
                .doOnNext(callMethod)
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(subscriver);
    }

    /**
     * 生成Service发起请求(普通，body，参数只有字符串)
     */
    public void startBaseRetrofitRequest(String requestParam, String requestUrl,
                                         Action1<BaseCallBackBean> callMethod, Subscriber<BaseCallBackBean> subscriver, Gson gson) {

        String requestJson = requestParam;
        Log.i("接口请求参数: " + requestUrl, requestParam);

        Services serviceApi = retrofitBody.create(Services.class);
        Observable<BaseCallBackBean> baseCallBackBeanObservable = UrlRequestIntentFilter.requestUrl(requestJson, serviceApi, requestUrl);
        if (baseCallBackBeanObservable == null) {
            Log.i("Retrofit", "获取的Iservice为null");
            return;
        }

        normalRequestSubscription = baseCallBackBeanObservable.subscribeOn(Schedulers.newThread()) // 请求在新的线程中执行
                .observeOn(Schedulers.io()) // 请求完成后在io线程中执行
                .doOnNext(callMethod)
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(subscriver);
    }

    /**
     * 生成Service发起请求(普通，query)
     */
    public void startBaseRetrofitQuery(HashMap<String, String> requestParams, String requestUrl,
                                       Action1<BaseCallBackBean> callMethod, Subscriber<BaseCallBackBean> subscriver) {

        Log.i("接口请求参数: " + requestUrl, requestParams.toString());

        Services serviceApi = retrofitQuery.create(Services.class);
        Observable<BaseCallBackBean> baseCallBackBeanObservable = UrlRequestIntentFilter.requestQuery(requestParams, serviceApi, requestUrl);
        if (baseCallBackBeanObservable == null) {
            Log.i("Retrofit", "获取的Iservice为null");
            return;
        }

        normalRequestSubscription = baseCallBackBeanObservable.subscribeOn(Schedulers.newThread()) // 请求在新的线程中执行
                .observeOn(Schedulers.io()) // 请求完成后在io线程中执行
                .doOnNext(callMethod)
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(subscriver);
    }

    /**
     * 生成Service发起请求(下载)
     */
    public void startBaseRetrofitRequestDownload(final String totalUrl, Subscriber<DownInfo> subscriber, int downLoadFileType) {

        Log.i("文件下载", "无需参数");

        final DownInfo info = DownInfo.getInstance(totalUrl, downLoadFileType);
        // 存储对应的下载subscriber，用于找到对应subscriber停止下载
        downloadSubscriberMap.put(info, subscriber);

        HttpService httpService = retrofitDownload.create(HttpService.class);

        /* 给下载链接加上时间戳 */
        String downLoadUrl = "";
        if (info.getUrl().contains("?"))
            downLoadUrl = info.getUrl() + "&t=" + System.currentTimeMillis();
        else
            downLoadUrl = info.getUrl() + "?t=" + System.currentTimeMillis();
        /* 得到rx对象-上一次下載的位置開始下載 */
        httpService.download("bytes=" + info.getReadLength() + "-", downLoadUrl)
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*读取下载写入文件*/
                .map(new Func1<ResponseBody, DownInfo>() {
                    @Override
                    public DownInfo call(ResponseBody responseBody) {
                        /*下载过程中同步数据*/
                        DownInfo info = DownInfo.getInstance(totalUrl, downLoadFileType);
                        info.saveInstance(totalUrl);
                        Log.i("文件下载相关", "下载过程中写入一次数据 ======= " + totalUrl);
                        DownloadUtil.writeCache(responseBody, new File(info.getSavePath()), info);
                        return info;
                    }
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);
    }

    /**
     * 生成Service发起请求(下载)
     */
    public void startBaseRetrofitRequestDownload(String totalUrl, String fileName, Subscriber<DownInfo> subscriber, int downLoadFileType) {

        Log.i("文件下载", "无需参数");

        final DownInfo info = DownInfo.getInstance(totalUrl, downLoadFileType, fileName);
        // 存储对应的下载subscriber，用于找到对应subscriber停止下载
        downloadSubscriberMap.put(info, subscriber);

        HttpService httpService = retrofitDownload.create(HttpService.class);

        /* 给下载链接加上时间戳 */
        String downLoadUrl = "";
        if (info.getUrl().contains("?"))
            downLoadUrl = info.getUrl() + "&t=" + System.currentTimeMillis();
        else
            downLoadUrl = info.getUrl() + "?t=" + System.currentTimeMillis();
        /* 得到rx对象-上一次下載的位置開始下載 */
        httpService.download("bytes=" + info.getReadLength() + "-", downLoadUrl)
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*读取下载写入文件*/
                .map(new Func1<ResponseBody, DownInfo>() {
                    @Override
                    public DownInfo call(ResponseBody responseBody) {
                        /*下载过程中同步数据*/
                        DownInfo info = DownInfo.getInstance(totalUrl, downLoadFileType);
                        info.saveInstance(totalUrl);
                        Log.i("文件下载相关", "下载过程中写入一次数据 ======= " + totalUrl);
                        DownloadUtil.writeCache(responseBody, new File(info.getSavePath()), info);
                        return info;
                    }
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);
    }

    /**
     * 停止下載
     */
    public static void stopDownload(final String totalUrl, int downLoadFileType) {
        // 停止继续处理数据
        DownInfo downInfo = DownInfo.getInstance(totalUrl, downLoadFileType);
        Subscriber<DownInfo> downLoadSubscriber = downloadSubscriberMap.get(downInfo);
        if (downLoadSubscriber != null && downInfo != null) {
            downloadSubscriberMap.remove(downInfo);
            downLoadSubscriber.unsubscribe();
            downLoadSubscriber = null;
            /*下载进度数据同步*/
            DownInfo info = DownInfo.getInstance(totalUrl, downLoadFileType);
            info.saveInstance(totalUrl);
        }
    }

    /**
     * 停止接口请求
     */
    public void stopNormalRequest() {
        if (normalRequestSubscription != null && !normalRequestSubscription.isUnsubscribed()) {
            normalRequestSubscription.unsubscribe();
        }
    }

/**  ================================================== 证书认证相关 ======================================================  */
    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS"); // 和"SSL"有何不同？
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
