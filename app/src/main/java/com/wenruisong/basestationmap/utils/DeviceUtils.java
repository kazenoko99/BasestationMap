package com.wenruisong.basestationmap.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
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



    public static DisplayMetrics getScreenWH(Context context) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
    }

    /**
     * 计算焦点及测光区域
     *
     * @param focusWidth
     * @param focusHeight
     * @param areaMultiple
     * @param x
     * @param y
     * @param previewleft
     * @param previewRight
     * @param previewTop
     * @param previewBottom
     * @return Rect(left,top,right,bottom) : left、top、right、bottom是以显示区域中心为原点的坐标
     */
    public static Rect calculateTapArea(int focusWidth, int focusHeight,
                                        float areaMultiple, float x, float y, int previewleft,
                                        int previewRight, int previewTop, int previewBottom) {
        int areaWidth = (int) (focusWidth * areaMultiple);
        int areaHeight = (int) (focusHeight * areaMultiple);
        int centerX = (previewleft + previewRight) / 2;
        int centerY = (previewTop + previewBottom) / 2;
        double unitx = ((double) previewRight - (double) previewleft) / 2000;
        double unity = ((double) previewBottom - (double) previewTop) / 2000;
        int left = clamp((int) (((x - areaWidth / 2) - centerX) / unitx),
                -1000, 1000);
        int top = clamp((int) (((y - areaHeight / 2) - centerY) / unity),
                -1000, 1000);
        int right = clamp((int) (left + areaWidth / unitx), -1000, 1000);
        int bottom = clamp((int) (top + areaHeight / unity), -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    public static int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x < min)
            return min;
        return x;
    }

    /**
     * 检测摄像头设备是否可用
     * Check if this device has a camera
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * @param context
     * @return app_cache_path/dirName
     */
    public static String getDBDir(Context context) {
        String path = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "bbk" + File.separator + "cloudteacher" + File.separator + "db";
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                path = externalCacheDir.getPath();
            }
        }
        if (path == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                path = cacheDir.getPath();
            }
        }
        return path;
    }

    /**
     * bitmap旋转
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
            }
        }
        return b;
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }
}
