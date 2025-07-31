package com.example.huidutest.lib;

import android.os.Build;
import android.text.TextUtils;

import java.io.File;

/* loaded from: classes2.dex */
public class DeviceProperties {
    private static volatile String sBuildVersion = null;
    private static volatile String sDeviceModel = null;
    private static volatile int sDeviceType = -1;
    private static volatile String sMacFile = null;
    private static volatile int sSnVersion = -1;

    public static int getDeviceType() {
        if (sDeviceType == -1) {
            sDeviceType = 12;
        }
        return sDeviceType;
    }


    public static String getRKMacFile() {
        if (sMacFile == null) {
            sMacFile = loadMacFile();
        }
        return sMacFile;
    }


    public static boolean isSupportPrimaryDisplay() {
        int deviceType = getDeviceType();
        return deviceType == 2 || deviceType == 3 || deviceType == 6 || deviceType == 8 || deviceType == 9;
    }

    public static boolean isSupportMcu() {
        int deviceType = getDeviceType();
        return (deviceType == 1 || deviceType == 2) ? false : true;
    }

    public static boolean isSupportIsIgnoreAppOrientation() {
        return getDeviceType() == 6;
    }

    public static boolean isSupportAuxRotation() {
        int deviceType = getDeviceType();
        return (deviceType == 5 || deviceType == 10 || deviceType == 11 || deviceType == 14 || deviceType == 4 || deviceType == 15) ? false : true;
    }

    public static boolean isSupportTouchRotation() {
        return getDeviceType() != 1;
    }

    public static boolean isSupportWallpaperFixSize() {
        int deviceType = getDeviceType();
        return deviceType == 1 || deviceType == 2 || deviceType == 3 || deviceType == 6 || deviceType == 7 || deviceType == 8 || deviceType == 12 || deviceType == 9;
    }

    public static boolean isSupportTelephony() {
        return getDeviceType() != 1;
    }

    public static boolean isSupportWhiteList() {
        int deviceType = getDeviceType();
        return deviceType == 4 || deviceType == 15;
    }

    public static boolean isMcuValid() {
        int deviceType = getDeviceType();
        return deviceType == 4 || deviceType == 5 || deviceType == 6 || deviceType == 7 || deviceType == 8 || deviceType == 12 || deviceType == 9 || deviceType == 10 || deviceType == 11 || deviceType == 13 || deviceType == 14 || deviceType == 15;
    }

    public static int getMcuI2CBus() {
        switch (getDeviceType()) {
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
            case 10:
            case 14:
            default:
                return 0;
            case 7:
            case 8:
            case 11:
            case 12:
                return 1;
            case 9:
                return 7;
            case 13:
                return 8;
            case 15:
                return 4;
        }
    }

    public static int getRtcI2CBus() {
        switch (getDeviceType()) {
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
            case 10:
            case 14:
                return 0;
            case 7:
            case 8:
            case 11:
            case 12:
                return 1;
            case 9:
                return 7;
            case 13:
                return 8;
            case 15:
                return 4;
            default:
                return -1;
        }
    }

    public static boolean isSupportMCUCheck() {
        int deviceType = getDeviceType();
        return deviceType == 11 || deviceType == 15 || deviceType == 13;
    }

    public static boolean isHiPlatform() {
        return getDeviceType() == 15;
    }

    public static boolean isSupportModifyUsbMode() {
        int deviceType = getDeviceType();
        return (deviceType == 1 || deviceType == 14 || deviceType == 15) ? false : true;
    }

    public static boolean isSupportModifyPrimaryDisplay() {
        int deviceType = getDeviceType();
        return (deviceType == 7 || deviceType == 8 || deviceType == 6 || deviceType == 9) ? false : true;
    }


    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0033  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int loadDeviceType() {
        /*
            Method dump skipped, instructions count: 516
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.huidu.toolbox.util.DeviceProperties.loadDeviceType():int");
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static String loadMacFile() {
        /*
            int r0 = getDeviceType()
            r1 = 1
            if (r0 == r1) goto L30
            r1 = 2
            if (r0 == r1) goto L24
            r1 = 3
            if (r0 == r1) goto L18
            r1 = 4
            if (r0 == r1) goto L15
            r1 = 6
            if (r0 == r1) goto L24
            r0 = 0
            goto L32
        L15:
            java.lang.String r0 = "/dev/block/by-name/hddata"
            goto L32
        L18:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 25
            if (r0 != r1) goto L21
            java.lang.String r0 = "/dev/block/platform/fe330000.sdhci/by-name/fbparam"
            goto L32
        L21:
            java.lang.String r0 = "/dev/vendor_storage"
            goto L32
        L24:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 24
            if (r0 >= r1) goto L2d
            java.lang.String r0 = "/dev/block/platform/ff0f0000.rksdmmc/by-name/fbparam"
            goto L32
        L2d:
            java.lang.String r0 = "/dev/block/platform/ff0f0000.dwmmc/by-name/fbparam"
            goto L32
        L30:
            java.lang.String r0 = "/dev/block/platform/emmc/by-name/fbparam"
        L32:
            if (r0 == 0) goto L37
            cn.huidu.toolbox.util.CmdUtils.chmod777(r0)
        L37:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.huidu.toolbox.util.DeviceProperties.loadMacFile():java.lang.String");
    }

    public static boolean isNewPartition() {
        return new File("/hddata/").exists();
    }

    public static boolean isSupportHdmiEnforceOut() {
        int deviceType = getDeviceType();
        return deviceType == 9 || deviceType == 8 || deviceType == 7 || deviceType == 6 || deviceType == 12 || deviceType == 13;
    }

    public static boolean isSupportGpsService() {
        int deviceType = getDeviceType();
        return deviceType == 8 || deviceType == 7;
    }

    public static String getGpsCommand() {
        int deviceType = getDeviceType();
        return deviceType != 7 ? deviceType != 8 ? "/dev/gps" : "/dev/ttyS8" : "/dev/ttyS9";
    }
}