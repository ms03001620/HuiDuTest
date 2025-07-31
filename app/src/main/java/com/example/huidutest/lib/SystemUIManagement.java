package com.example.huidutest.lib;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method; // 导入反射相关的类

public class SystemUIManagement {

    private static final String TAG = "SystemUIManagement";
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponentName;
    private Context applicationContext;

    public SystemUIManagement(Context context, Class<?> adminReceiverClass) {
        this.applicationContext = context.getApplicationContext();
        this.devicePolicyManager = (DevicePolicyManager) applicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.adminComponentName = new ComponentName(applicationContext, adminReceiverClass);
    }

    public boolean isAppDeviceOwner() {
        return devicePolicyManager.isDeviceOwnerApp(applicationContext.getPackageName());
    }

    /**
     * 启用或禁用系统 UI 的各种功能（如通知栏下拉、Home 键等）。
     * 现在使用反射调用 setSystemUiMessage。
     *
     * @param activity 要进入/退出锁定任务模式的 Activity 实例。
     * @param disable true 为禁用，false 为启用。
     */
    public void setSystemUiDisabled(Activity activity, boolean disable) {
        if (!isAppDeviceOwner()) {
            Log.e(TAG, "当前应用不是 Device Owner，无法设置系统 UI 禁用状态。");
            return;
        }

        try {
            if (disable) {
                // 禁用所有系统 UI
                // 通过反射调用 setSystemUiMessage
                try {
                    Method setSystemUiMessageMethod = devicePolicyManager.getClass().getMethod("setSystemUiMessage", ComponentName.class, CharSequence.class);
                    // 你可以在这里设置你想显示的消息，如果不需要显示消息，可以传入 null 或空字符串
                    // setSystemUiMessageMethod.invoke(devicePolicyManager, adminComponentName, "设备已锁定 by Kiosk App");
                    setSystemUiMessageMethod.invoke(devicePolicyManager, adminComponentName, null); // 不显示特定消息
                    Log.d(TAG, "尝试通过反射调用 setSystemUiMessage。");
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, "setSystemUiMessage 方法在当前设备或 Android 版本上不存在。这很常见。", e);
                }


                devicePolicyManager.setKeyguardDisabled(adminComponentName, true); // 禁用锁屏
                devicePolicyManager.setStatusBarDisabled(adminComponentName, true); // 禁用状态栏，通常指通知栏下拉和快速设置

                // 启动锁定任务模式
                activity.startLockTask();

                Log.d(TAG, "系统 UI 功能已禁用，并进入锁定任务模式。");
            } else {
                // 启用所有系统 UI
                activity.stopLockTask();
                devicePolicyManager.setStatusBarDisabled(adminComponentName, false);
                devicePolicyManager.setKeyguardDisabled(adminComponentName, false);

                // 通过反射清除 setSystemUiMessage 设置
                try {
                    Method setSystemUiMessageMethod = devicePolicyManager.getClass().getMethod("setSystemUiMessage", ComponentName.class, CharSequence.class);
                    setSystemUiMessageMethod.invoke(devicePolicyManager, adminComponentName, null); // 传入 null 清除消息
                    Log.d(TAG, "尝试通过反射清除 setSystemUiMessage。");
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, "setSystemUiMessage 方法在当前设备或 Android 版本上不存在，无需清除。");
                }

                Log.d(TAG, "系统 UI 功能已启用，并退出锁定任务模式。");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "权限不足或操作失败: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "设置系统 UI 禁用状态时发生错误: " + e.getMessage(), e);
        }
    }

    // whitelistAppForLockTask 方法保持不变
    public boolean whitelistAppForLockTask() {
        if (!isAppDeviceOwner()) {
            Log.e(TAG, "当前应用不是 Device Owner，无法将应用添加到锁定任务模式白名单。");
            return false;
        }
        try {
            String[] packages = devicePolicyManager.getLockTaskPackages(adminComponentName);
            boolean alreadyWhitelisted = false;
            String currentPackage = applicationContext.getPackageName();
            for (String pkg : packages) {
                if (pkg.equals(currentPackage)) {
                    alreadyWhitelisted = true;
                    break;
                }
            }
            if (!alreadyWhitelisted) {
                String[] newPackages = new String[packages.length + 1];
                System.arraycopy(packages, 0, newPackages, 0, packages.length);
                newPackages[packages.length] = currentPackage;
                devicePolicyManager.setLockTaskPackages(adminComponentName, newPackages);
                Log.d(TAG, "已将应用添加到锁定任务模式白名单。");
            } else {
                Log.d(TAG, "应用已在锁定任务模式白名单中。");
            }
            return true;
        } catch (SecurityException e) {
            Log.e(TAG, "添加应用到锁定任务模式白名单失败 (权限不足): " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "添加应用到锁定任务模式白名单时发生错误: " + e.getMessage(), e);
            return false;
        }
    }


}