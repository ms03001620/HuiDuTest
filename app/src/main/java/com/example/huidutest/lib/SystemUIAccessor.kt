package com.example.huidutest.lib

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * 设置系统 UI 的沉浸模式。
 *
 * 1.需要app声明该权限
 *   <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"/>
 *
 *
 * 2.授予权限，命令行形式，也有其他形式
 * adb shell pm grant com.example.huidutest android.permission.WRITE_SECURE_SETTINGS
 *
 * 撤销授权
 * adb shell pm revoke com.example.huidutest android.permission.WRITE_SECURE_SETTINGS
 *
 * - "immersive.full" (隐藏状态栏和导航栏)
 * - "immersive.status=*" (仅隐藏状态栏)
 * - "immersive.navigation=*" (仅隐藏导航栏)
 * - null 或 "" (恢复系统 UI 默认状态)
 */
object SystemUIAccessor {
    private const val POLICY_CONTROL = "policy_control"

    fun hasPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_SECURE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 隐藏状态栏和导航栏。
     * @param context Context 对象。
     */
    fun hideSystemBars(context: Context) {
        setSystemImmersiveMode(context, "immersive.full=com.example.huidutest")
    }

    /**
     * 仅隐藏状态栏。
     */
    fun hideStatusBar(context: Context) {
        setSystemImmersiveMode(context, "immersive.status=*")
    }

    /**
     * 仅隐藏导航栏。
     */
    fun hideNavigationBar(context: Context) {
        setSystemImmersiveMode(context, "immersive.navigation=*")
    }

    /**
     * 恢复状态栏和导航栏显示。
     */
    fun showSystemBars(context: Context) {
        setSystemImmersiveMode(context, null)
    }

    private fun setSystemImmersiveMode(context: Context, mode: String?) {
        try {
            val contentResolver = context.contentResolver
            if (mode.isNullOrEmpty()) {
                // 如果传入 null 或空字符串，则删除此设置，恢复默认
                Settings.Global.putString(contentResolver, POLICY_CONTROL, null)
            } else {
                Settings.Global.putString(contentResolver, POLICY_CONTROL, mode)
            }
        }  catch (e: Exception) {
            e.printStackTrace()
        }
    }
}