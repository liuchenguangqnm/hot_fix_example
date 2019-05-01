package com.example.hotfix.utils.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.example.hotfix.utils.StringUtils;

/*
 * 设备应用相关信息获取
 */
@SuppressLint("MissingPermission")
public class DeviceAppInfoUtil {

	/*
     * 获取application下meta-data值
	 */
	public static String getChannelName(Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString("UMENG_CHANNEL");
			return msg;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * 获得手机品牌型号 deviceModel
	 */
	public static String getDeviceModel(Context context) {
		String deviceModel = Build.BRAND + "-" + Build.MODEL;
		return deviceModel;
	}

	/*
	 * 获得手机系统SDK版本 sysVersion
	 */
	public static String getSysVersion(Context context) {
		String sysVersion = Build.VERSION.RELEASE;
		return sysVersion;
	}

	/*
	 * 获取应用版本号
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			if(null==info)
				return "";
			String versionName = info.versionName;
			if(!StringUtils.isEmpty(versionName)){
				return "V"+versionName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * 获取手机号码
	 */
	public static String getPhoneNum(Context context){
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}

	/*运营商名
	 * phone operator
	 */
	public static String getPhoneOperatorName(Context context){
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = telephonyManager.getDeviceId();
        if(StringUtils.isEmpty(IMSI))
        	return "";
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。  
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}
}