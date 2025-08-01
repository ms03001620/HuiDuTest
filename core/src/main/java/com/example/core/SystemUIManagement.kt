package com.example.core

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

public class SystemUIManagement(context: Context, adminReceiverClass: Class<*>) {
    private val devicePolicyManager: DevicePolicyManager
    private val adminComponentName: ComponentName
    private val applicationContext: Context = context.applicationContext

    init {
        this.devicePolicyManager =
            applicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        this.adminComponentName = ComponentName(applicationContext, adminReceiverClass)
    }

    fun isAppDeviceOwner() = devicePolicyManager.isDeviceOwnerApp(applicationContext.packageName)

    fun removeDeviceOwner(): Boolean {
        try {
            devicePolicyManager.clearDeviceOwnerApp(applicationContext.packageName)
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    fun setSystemUiDisabled(activity: Activity, disable: Boolean) {
        try {
            cleanSystemUiMessage()
            devicePolicyManager.setKeyguardDisabled(adminComponentName, disable)
            // 禁用状态栏，通常指通知栏下拉和快速设置
            devicePolicyManager.setStatusBarDisabled(adminComponentName, disable)
            if (disable) {
                activity.startLockTask()
            } else {
                activity.stopLockTask()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cleanSystemUiMessage() {
        try {
            val setSystemUiMessageMethod = devicePolicyManager.javaClass.getMethod(
                "setSystemUiMessage",
                ComponentName::class.java,
                CharSequence::class.java
            )
            setSystemUiMessageMethod.invoke(devicePolicyManager, adminComponentName, null)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    fun isLockTaskMode(): Boolean {
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        when (activityManager.lockTaskModeState) {
            //当前未处于锁定任务模式
            ActivityManager.LOCK_TASK_MODE_NONE -> {
                return false
            }
            //当前处于屏幕固定模式 (Screen Pinning)
            ActivityManager.LOCK_TASK_MODE_PINNED -> {
                return true
            }
            //当前处于锁定任务模式 (Kiosk Mode / startLockTask)
            ActivityManager.LOCK_TASK_MODE_LOCKED -> {
                return true
            }

            else -> {
                //未知锁定任务模式状态
                return false
            }
        }
    }

    // 将app加入锁屏任务白名单
    // 如果不加入那么startLockTask 只是普通的固定屏幕的做法是可以退出的
    // 加入后只有stopLockTask才能退出
    fun whitelistAppForLockTask(): Boolean {
        try {
            val packages = devicePolicyManager.getLockTaskPackages(adminComponentName)
            var alreadyWhitelisted = false
            val currentPackage = applicationContext.packageName
            for (pkg in packages) {
                if (pkg == currentPackage) {
                    alreadyWhitelisted = true
                    break
                }
            }
            if (!alreadyWhitelisted) {
                val newPackages = arrayOfNulls<String>(packages.size + 1)
                System.arraycopy(packages, 0, newPackages, 0, packages.size)
                newPackages[packages.size] = currentPackage
                devicePolicyManager.setLockTaskPackages(adminComponentName, newPackages)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}