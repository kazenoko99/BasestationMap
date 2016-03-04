package com.wenruisong.basestationmap.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    private static final String VA_IMEI = "va_device_imei";
    private static final int SLOT_ID_2 = 1;
    private static boolean hasTypeGot;
    private static boolean IS_MX2;
    private static boolean IS_MX3;
    private static boolean IS_MX4;
    private static boolean IS_MX4_Pro;
    private static boolean IS_M1;
    private static boolean IS_M1_NOTE;
    private static boolean IS_M1_NOTEC;
    private static boolean IS_M2;
    private static boolean IS_M2C;
    private static boolean IS_M2_NOTE;
    private static boolean IS_M2_NOTEC;
    private static boolean IS_MX5;
    private static boolean IS_MX5_Pro;

    /**
     * 是否支持闪光灯
     *
     * @param context
     * @return
     */
    public final static boolean isFlashLightSupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public final static String getTopPackageName(Context context) {
        return getRunningPackageNameBy(context, 0);
    }

    public static String getRunningPackageNameBy(Context context, int index) {
        List<RunningAppProcessInfo> runningAppInfos = getRunningAppInfos(context);
        if (!isListEmpty(runningAppInfos) && runningAppInfos.size() > index) {
            RunningAppProcessInfo info = runningAppInfos.get(index);
            return info.processName;
        }
        return "";
    }

    public static void collapseStatusBar(Context context) {
        try {
            @SuppressWarnings("ResourceType")
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static String getPackageNameUnderSelfAndIfSeltIsTop(Context context) {
        if (context == null) {
            return null;
        }
        String topPackageName = getTopPackageName(context);
        String packageNameOfSelt = context.getPackageName();
        if (topPackageName.equals(packageNameOfSelt)) {
            return getRunningPackageNameBy(context, 1);
        }
        return topPackageName;
    }

    public static String getTopActivityName(Context context) {
        return getRunningActivityNameBy(context, 0);
    }

    public static String getRunningActivityNameBy(Context context, int index) {
        List<RunningTaskInfo> infos = getRunningTaskInfos(context);
        if (!isListEmpty(infos) && infos.size() > index) {
            return (infos.get(index).topActivity).getClassName();
        }
        return "";
    }

    /**
     * 当前最顶部的app是否为桌面
     *
     * @param context
     * @param outPackname 输出参数，当当前为桌面时，如果长度大于0，则第一个值被设为该app的包名
     * @return
     */
    public final static boolean isTopRunningAppDesktop(Context context, String[] outPackname) {
        List<String> launchers = getLunchPackages(context);
        String current = getTopPackageName(context);
        if (launchers != null && launchers.size() > 0 && current != null) {
            for (final String launcher : launchers) {
                if (launcher.equals(current)) {
                    if (outPackname != null && outPackname.length > 0) {
                        outPackname[0] = launcher;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static List<RunningAppProcessInfo> getRunningAppInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppInfos = am.getRunningAppProcesses();
        return runningAppInfos;
    }

    public final static List<RunningTaskInfo> getRunningTaskInfos(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        return runningTaskInfos;
    }

    private final static boolean isListEmpty(List<? extends Object> runningAppInfos) {
        return runningAppInfos == null || runningAppInfos.isEmpty();
    }

    public static boolean getIsBlurDisable() {
        Class clsSystemProperties = CompatUtils.getClass("android.os.SystemProperties");
        Method getBoolean = CompatUtils.getMethod(clsSystemProperties,
                "getBoolean", String.class, boolean.class);
        boolean flag = (Boolean) CompatUtils.invoke(clsSystemProperties, false,
                getBoolean, "persist.sys.disable_glass_blur", false);
        return flag;
    }



    public static List<String> getLunchPackages(Context context) {
        if (context == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }


    public final static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public final static boolean isInSplitMode(Activity activity) {
        return isInSplitMode(activity, null);
    }

    public static boolean isInSplitMode(Activity activity, Rect outRect) {
        try {
            Class flymeSplitModeManager = CompatUtils.getClass(
                    "meizu.splitmode.FlymeSplitModeManager");
            if (flymeSplitModeManager == null) {
                return false;
            }
            Method getInstance = null;
            boolean isSplitMode = false;
            try {
                getInstance = flymeSplitModeManager.getMethod("getInstance", Context.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (getInstance != null) {
                Object instance = CompatUtils.invoke(flymeSplitModeManager, null, getInstance, activity);
                if (instance != null) {
                    Method getActivitySplitRect = null;
                    try {
                        getActivitySplitRect = flymeSplitModeManager.getMethod("getActivitySplitRect", Activity.class, Rect.class);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (getActivitySplitRect != null) {
                        outRect = outRect == null ? new Rect() : outRect;
                        isSplitMode = (Boolean) CompatUtils.invoke(instance, false, getActivitySplitRect, activity, outRect);
                    }
                }
            }
            return isSplitMode;
        } catch (Exception e) {
            return false;
        }
    }

    private static void getType() {
        if (hasTypeGot) {
            return;
        }

        Class clsBuildExt = CompatUtils.getClass("android.os.BuildExt");

        try {
            IS_MX2 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX2"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_MX3 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX3"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_MX4 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX4"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_MX4_Pro = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX4_Pro"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M1 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M1"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M1_NOTE = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M1_NOTE"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M1_NOTEC = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M71C"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M2_NOTE = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M2_NOTE"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M2 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M2"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M2C = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M2C"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_M2_NOTEC = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_M2_NOTEC"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            IS_MX5 = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX5"));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            boolean isMx5PRO = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                    CompatUtils.getField(clsBuildExt, "IS_MX5_PRO"));
            boolean isMx5Pro = false;
            if (!isMx5PRO) {
                isMx5Pro = (boolean) CompatUtils.getFieldValue(clsBuildExt, false,
                        CompatUtils.getField(clsBuildExt, "IS_MX5_Pro"));
            }
            IS_MX5_Pro = isMx5PRO || isMx5Pro;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        hasTypeGot = true;
    }

    public static boolean isVoiceAssistantUseInLockScreen(Context context) {
        boolean isChecked;
        Class clsSys = android.provider.Settings.System.class;
        Method getInt = CompatUtils.getMethod(clsSys, "getInt", ContentResolver.class, String.class, int.class);
        Class settingsCls = CompatUtils.getClass("android.provider.MzSettings$System");
        Field vaUseInLockScreenField = CompatUtils.getField(settingsCls, "VOICE_ASSISTENT_USE_IN_LOCKSCREEN");
        String vaUseInLockScreenValue = (String) CompatUtils.getFieldValue(settingsCls, null, vaUseInLockScreenField);
        int checkedValue = (Integer) CompatUtils.invoke(clsSys, 0, getInt, context.getContentResolver(), vaUseInLockScreenValue, 0);
        isChecked = checkedValue == 1;
        return isChecked;
    }

}
