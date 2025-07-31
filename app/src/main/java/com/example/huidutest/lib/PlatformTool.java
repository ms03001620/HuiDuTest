package com.example.huidutest.lib;

import static com.example.huidutest.lib.CommandLine.executeSu;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;


/* loaded from: classes2.dex */
public class PlatformTool {
    private static final String ACTION_AUTO_NAVIGATION_BAR = "cn.huidu.action.AUTO_NAVIGATION_BAR";
    private static final String ACTION_HIDE_NAVIGATION_BAR = "cn.huidu.action.HIDE_NAVIGATION_BAR";
    private static final String ACTION_HIDE_STATUS_BAR = "cn.huidu.action.HIDE_STATUS_BAR";
    private static final String ACTION_SHOW_NAVIGATION_BAR = "cn.huidu.action.SHOW_NAVIGATION_BAR";
    private static final String ACTION_SHOW_STATUS_BAR = "cn.huidu.action.SHOW_STATUS_BAR";
    private static final String ACTION_SWIPE_FROM_BOTTOM = "cn.huidu.action.SWIPE_FROM_BOTTOM";
    public static final String CMD_ITEMS = "ro.player.serials";
    public static final String CMD_NAMES = "ro.player.serials.name";
    private static final int DEVICE_ID_LENGTH = 16;
    private static final int DEVICE_ID_OFFSET = 256;
    private static final String ENABLE_CMD = "persist.player.cmd";


    private static final int MAC_LENGTH = 6;
    private static final int MAC_OFFSET = 64;
    public static final String NAVIGATION_BAR_ENABLED = "navigation_bar_enabled";
    public static final String PULLUP_TO_SHOW_NAVIGATION_BAR_ENABLED = "pullup_to_show_navigation_bar_enabled";

    private static final String SET_CMD_VALUE = "ro.huidu.magic.tty";

    public static final String STATUS_BAR_ENABLED = "status_bar_enabled";
    public static final String SYNC = "sync";
    private static final String TAG = "PlatformTool";





    public static int getNavBarState(Context context) {
        if (DeviceProperties.getDeviceType() == 13) {
            return Math.abs(Settings.Global.getInt(context.getContentResolver(), NAVIGATION_BAR_ENABLED, 1) - 1);
        }
        try {
            return Settings.System.getInt(context.getContentResolver(), "HuiduHideNavigation");
        } catch (Exception e) {
            Log.w(TAG, e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public static void setNavBarState(Context context, int state) {
        Log.e("WindowManager", "state = " + state);
        if (DeviceProperties.getDeviceType() == 13) {
            Settings.Global.putInt(context.getContentResolver(), NAVIGATION_BAR_ENABLED, Math.abs(state - 1));
            executeSu(SYNC);
            return;
        }
        try {
            Settings.System.putInt(context.getContentResolver(), "HuiduHideNavigation", state);
            String str = state == 0 ? ACTION_SHOW_NAVIGATION_BAR : ACTION_HIDE_NAVIGATION_BAR;
            Log.d(TAG, "setNavBarState: broadcast >> " + str);
            context.sendBroadcast(new Intent(str));
            executeSu(SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAutoHideNavBar(Context context, int autoHideDelay) {
        try {
            Intent intent = new Intent(ACTION_AUTO_NAVIGATION_BAR);
            intent.putExtra("AutoHideDelay", autoHideDelay);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getStatusBarState(Context context) {
        if (DeviceProperties.getDeviceType() == 13) {
            return Math.abs(Settings.Global.getInt(context.getContentResolver(), STATUS_BAR_ENABLED, 1) - 1);
        }
        try {
            return Settings.System.getInt(context.getContentResolver(), "HuiduHideStatusBar");
        } catch (Exception e) {
            Log.w(TAG, e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public static void setStatusBarState(Context context, int state) {
        if (DeviceProperties.getDeviceType() == 13) {
            Settings.Global.putInt(context.getContentResolver(), STATUS_BAR_ENABLED, Math.abs(state - 1));
            executeSu(SYNC);
            return;
        }
        try {
            //Settings.System.putInt(context.getContentResolver(), "HuiduHideStatusBar", state);
            setStatusBarState( state);
            String str = state == 0 ? ACTION_SHOW_STATUS_BAR : ACTION_HIDE_STATUS_BAR;
            Log.d(TAG, "setStatusBarState: broadcast >> " + str);
            context.sendBroadcast(new Intent(str));
            executeSu(SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static CommandLine.ExecuteResult setStatusBarState(int state) {
        // 调用我们工具类中的新方法
        CommandLine.ExecuteResult result = putSystemSetting("HuiduHideStatusBar", state);

        return result;

    }

    public static CommandLine.ExecuteResult putSystemSetting(String key, int value) {
        // 1. 构建 settings put system 命令
        String command = "settings put system " + key + " " + value;

        // 2. 打印日志，方便调试
        Log.d("CommandLineExecutor", "Executing root command: " + command);

        // 3. 使用我们已经验证通过的 executeSu 方法来执行这条命令
        return executeSu(command);
    }

}