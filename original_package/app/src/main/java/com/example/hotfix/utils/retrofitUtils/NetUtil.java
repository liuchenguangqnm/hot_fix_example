package com.example.hotfix.utils.retrofitUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.example.hotfix.MyApplication;

/**
 * 判断网络连接是否成功是否是wifi
 */
public class NetUtil {

    private NetUtil() {
    }

    public static boolean isNetworkConnected() {
        if (MyApplication.instance != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.instance
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected() {
        if (MyApplication.instance != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.instance
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected() {
        if (MyApplication.instance != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.instance
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType() {
        if (MyApplication.instance != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.instance
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取网络连接方式
     */
    public static String getNet_way(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net_info = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null == net_info)
            return "";
        NetworkInfo.State state = net_info.getState();
        if (null != state)
            return state.toString();
        net_info = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null == net_info)
            return "";
        state = net_info.getState();
        if (null != state)
            return state.toString();
        return "";
    }

    /**
     * 判断设备 是否使用代理上网
     */
    public static boolean isWifiProxy() {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(MyApplication.instance);
            proxyPort = android.net.Proxy.getPort(MyApplication.instance);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

}
