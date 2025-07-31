package com.example.huidutest.lib

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.util.Log

object DeviceOwnerChecker {
    private const val TAG = "DeviceOwnerChecker"

    /**
     * 检查当前应用是否是设备的 Device Owner。
     *
     * @param context 应用的 Context。
     * @return 如果是 Device Owner，则返回 true；否则返回 false。
     */
    fun isAppDeviceOwner(context: Context): Boolean {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        // 获取当前应用的包名
        val packageName = context.packageName

        if (devicePolicyManager != null && packageName != null) {
            val isDeviceOwner = devicePolicyManager.isDeviceOwnerApp(packageName)
            Log.d(
                TAG,
                "当前应用 ($packageName) 是否是 Device Owner: $isDeviceOwner"
            )
            return isDeviceOwner
        } else {
            Log.e(TAG, "无法获取 DevicePolicyManager 或包名为 null。")
            return false
        }
    }

    fun removeDeviceOwner(context: Context): Boolean {
        try {
            val devicePolicyManager =
                context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            devicePolicyManager.clearDeviceOwnerApp(context.packageName)
            return true
        } catch (e: SecurityException) {
            //  java.lang.SecurityException: clearDeviceOwner can only be called by the device owner
            return false
        }
    }
}