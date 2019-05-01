package com.example.hotfix.utils.device.deviceOnlyCode;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.example.hotfix.MyApplication;
import com.example.hotfix.utils.device.deviceOnlyCode.CoreUtils.ISettingUtils;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/3/2        Sunshine         获取设备唯一DeviceCode
 */
public class DeviceCodeUtil {
    // 获取VersionCode相关
    private static String buildSerial = "";   // Serial Number
    private static String androidID = "";     // androidID
    private static String machineModel = "";  // 机器型号
    private static String cpuOS = "";         // 处理器系统
    // 获取最后的手机DeviceCode
    private static String deviceCode = "";

    /**
     * 设备唯一标识说明：
     * 由英文逗号分隔，判别可靠程度从前往后排列，它们分别是
     * 1）比较稳定的设备硬件标识符，在及少数的一些设备上，会返回垃圾数据
     * 2）在设备首次启动时，系统会随机生成一个64位的数字作为唯一标识，如果用户刷机了，它会变
     * 3）手机型号
     * 4）手机CPU系统名称
     */

    public static String getDeviceCode(Context context) {
        // 获取Serial Number
        buildSerial = Build.SERIAL;
        // 获取androidID
        androidID = getAndroidId(context);
        // 生成设备唯一码
        getDeviceCodeCore();
        return deviceCode;
        // 获取Mac地址（不开wifi会获取不到，不用它做唯一标识） MacAddressUtils.getMacAddress(splashActivity);
    }

    public static void getDeviceCodeCore() {
        if (buildSerial == null)
            buildSerial = "";
        if (androidID == null)
            androidID = "";
        machineModel = SystemPropertiesProxy.get(MyApplication.instance, "ro.product.model");
        cpuOS = SystemPropertiesProxy.get(MyApplication.instance, "ro.board.platform");
        deviceCode = buildSerial + "," + androidID + "," + machineModel + "," + cpuOS;
    }

    /**
     * 获取AndroidID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId;
        if (!TextUtils.isEmpty(androidId = ISettingUtils.getAndroidPropertyLevel1(context, Settings.Secure.ANDROID_ID))
                || !TextUtils.isEmpty(androidId = ISettingUtils.getAndroidProperty(context, Settings.Secure.ANDROID_ID))) {
            return androidId;
        }
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
