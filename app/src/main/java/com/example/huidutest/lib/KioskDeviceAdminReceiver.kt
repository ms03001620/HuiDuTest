package com.example.huidutest.lib

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


/**
 * KioskDeviceAdminReceiver 是一个 DeviceAdminReceiver 的子类，
 * 用于接收和处理设备管理相关的事件。
 * 在 Kiosk 模式应用中，它扮演着关键角色，用于：
 * 1. 接收设备管理员激活/禁用事件。
 * 2. 接收锁定任务模式进入/退出事件（Android 5.0+）。
 *
 * 需要在 AndroidManifest.xml 中声明并配置 meta-data 指向 device_admin_policy.xml。
 *
 * adb shell dpm set-device-owner com.example.huidutest/.lib.KioskDeviceAdminReceiver
 */
class KioskDeviceAdminReceiver : DeviceAdminReceiver() {
    /**
     * 当设备管理员被激活时调用。
     * 通常在这个时候进行一些初始化设置，或者通知用户激活成功。
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        sendLocalBroadcast(context, DeviceAdminEvent.DEVICE_ADMIN_ENABLED)
    }

    /**
     * 当设备管理员被禁用（解除激活）时调用。
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        sendLocalBroadcast(context, DeviceAdminEvent.DEVICE_ADMIN_DISABLED)
    }

    /**
     * 当设备进入锁定任务模式时调用。
     */
    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        sendLocalBroadcast(context, DeviceAdminEvent.LOCK_TASK_MODE_ENTERING)
    }

    /**
     * 当设备退出锁定任务模式时调用。
     */
    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        sendLocalBroadcast(context, DeviceAdminEvent.LOCK_TASK_MODE_EXITING)
    }

    private fun sendLocalBroadcast(context: Context, event: DeviceAdminEvent) {
        val localIntent = Intent(getAdminAction(context))
        localIntent.putExtra(ADMIN_EVENT, event.name)
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
    }

    companion object {
        private const val ADMIN_EVENT = "admin_event"

        enum class DeviceAdminEvent(val message: String) {
            UNSPECIFIED("未定义0"),
            DEVICE_ADMIN_ENABLED("设备管理员已启用"),
            DEVICE_ADMIN_DISABLED("设备管理员已禁用"),
            LOCK_TASK_MODE_ENTERING("进入管理员模式"),
            LOCK_TASK_MODE_EXITING("退出管理员模式"),
        }

        fun getEvent(intent: Intent): DeviceAdminEvent {
            return DeviceAdminEvent.valueOf(
                intent.getStringExtra(ADMIN_EVENT) ?: DeviceAdminEvent.UNSPECIFIED.name
            )
        }

        fun getAdminAction(context: Context) = context.packageName + ".ACTION_DEVICE_ADMIN_EVENT"
    }
}