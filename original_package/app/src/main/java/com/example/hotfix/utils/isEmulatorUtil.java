package com.example.hotfix.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.hotfix.MyApplication;
import com.example.hotfix.utils.device.deviceOnlyCode.SystemPropertiesProxy;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/8/18       Sunny            是否是模拟器
 */
public class isEmulatorUtil {

    public static boolean isEmulator(Context context) {
        if (notHasLightSensorManager(context) || getCpuTypeIsEmulator(context))
            return true;
        else
            return false;
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    private static boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // 光传感器
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取Cpu类型
     *
     * @param context
     * @return true 为模拟器
     */
    private static boolean getCpuTypeIsEmulator(Context context) {
        String cpuType = SystemPropertiesProxy.get(MyApplication.instance, "ro.product.cpu.abi");
        if (!cpuType.contains("arm"))
            return true;
        else
            return false;
    }

}
