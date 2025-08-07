package com.example.huidutest.lib

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.example.huidutest.lib.CommandLine.ExecuteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/* loaded from: classes2.dex */
object PlatformTool {
    private const val ACTION_AUTO_NAVIGATION_BAR = "cn.huidu.action.AUTO_NAVIGATION_BAR"
    private const val ACTION_HIDE_NAVIGATION_BAR = "cn.huidu.action.HIDE_NAVIGATION_BAR"
    private const val ACTION_HIDE_STATUS_BAR = "cn.huidu.action.HIDE_STATUS_BAR"
    private const val ACTION_SHOW_NAVIGATION_BAR = "cn.huidu.action.SHOW_NAVIGATION_BAR"
    private const val ACTION_SHOW_STATUS_BAR = "cn.huidu.action.SHOW_STATUS_BAR"
    private const val ACTION_SWIPE_FROM_BOTTOM = "cn.huidu.action.SWIPE_FROM_BOTTOM"
    const val CMD_ITEMS: String = "ro.player.serials"
    const val CMD_NAMES: String = "ro.player.serials.name"
    private const val DEVICE_ID_LENGTH = 16
    private const val DEVICE_ID_OFFSET = 256
    private const val ENABLE_CMD = "persist.player.cmd"


    private const val MAC_LENGTH = 6
    private const val MAC_OFFSET = 64
    const val NAVIGATION_BAR_ENABLED: String = "navigation_bar_enabled"
    const val PULLUP_TO_SHOW_NAVIGATION_BAR_ENABLED: String =
        "pullup_to_show_navigation_bar_enabled"

    private const val SET_CMD_VALUE = "ro.huidu.magic.tty"

    const val STATUS_BAR_ENABLED: String = "status_bar_enabled"
    const val SYNC: String = "sync"
    private const val TAG = "PlatformTool"

    fun getNavBarState(context: Context): Int {
        try {
            return Settings.System.getInt(context.getContentResolver(), "HuiduHideNavigation")
        } catch (e: Exception) {
            Log.w(TAG, e.javaClass.getName() + ": " + e.message)
            return 0
        }
    }


    fun setAutoHideNavBar(context: Context, autoHideDelay: Int) {
        try {
            val intent = Intent(ACTION_AUTO_NAVIGATION_BAR)
            intent.putExtra("AutoHideDelay", autoHideDelay)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getStatusBarState(context: Context): Int {
        try {
            return Settings.System.getInt(context.getContentResolver(), "HuiduHideStatusBar")
        } catch (e: Exception) {
            Log.w(TAG, e.javaClass.getName() + ": " + e.message)
            return 0
        }
    }

    fun setStatusBarState(context: Context, state: Int) {
        try {
            val str = if (state == 0) ACTION_SHOW_STATUS_BAR else ACTION_HIDE_STATUS_BAR
            context.sendBroadcast(Intent(str))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setNavBarState(context: Context, state: Int) {
        try {
            val str = if (state == 0) ACTION_SHOW_NAVIGATION_BAR else ACTION_HIDE_NAVIGATION_BAR
            context.sendBroadcast(Intent(str))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setStatusBarState(state: Int): ExecuteResult {
        // 调用我们工具类中的新方法
        val result = putSystemSetting("HuiduHideStatusBar", state)

        return result
    }

    fun putSystemSetting(key: String, value: Int): ExecuteResult {
        // 1. 构建 settings put system 命令
        val command = "settings put system " + key + " " + value

        // 2. 打印日志，方便调试
        Log.d("CommandLineExecutor", "Executing root command: " + command)

        // 3. 使用我们已经验证通过的 executeSu 方法来执行这条命令
        return CommandLine.executeSu(command)
    }
}