package com.wenruisong.basestationmap.common;

import android.content.Context;
import android.content.SharedPreferences;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;


public class Settings {
    private static Settings mSettings = new Settings();
    private static Context mContext;
    private ScreenOnListener screenOnListener;
    private ZoomSettingListener zoomSettingListener;
    private CenterIndicSettingListener centerIndicSettingListener;

    public static Settings getInstance() {
        if (mSettings == null) {
            mSettings = new Settings();
        }
        return mSettings;
    }

    public static void initContext(Context context) {
        mContext = context;
    }

    public boolean isKeepScreenOn() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.KEEP_SCREEN_ON, false);
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.KEEP_SCREEN_ON, keepScreenOn);
        editor.apply();
        if (screenOnListener != null) {
            screenOnListener.onKeepScreenChange(keepScreenOn);
        }
    }

    public void setScreenOnListener(ScreenOnListener screenOnListener) {
        this.screenOnListener = screenOnListener;
    }

    public void setZoomSettingListener(ZoomSettingListener zoomSettingListener) {
        this.zoomSettingListener = zoomSettingListener;
    }

    public interface ScreenOnListener {
        void onKeepScreenChange(boolean keepSreenOn);
    }

    public boolean isShowZoomButton() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.SHOW_ZOOM_BUTTON, true);
    }

    public void setShowZoomButton(boolean showZoomButton) {
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.SHOW_ZOOM_BUTTON, showZoomButton);
        editor.apply();
        if (zoomSettingListener != null) {
            zoomSettingListener.onZoomSettingChange(showZoomButton);
        }
    }

    public interface ZoomSettingListener {
        void onZoomSettingChange(boolean showZoomButton);
    }

    public boolean isNeverShowGpsDialog() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.NEVER_SHOW_GPS_DIALOG, false);
    }

    public void setNeverShowGpsDialog(boolean neverShowGpsDialog) {
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.NEVER_SHOW_GPS_DIALOG, neverShowGpsDialog);
        editor.apply();
    }

    public boolean isShowCenterIndicButton() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.SHOW_CENTER_INDIC, false);
    }

    public void setShowCenterIndicButton(boolean showCenterIndic) {
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.SHOW_CENTER_INDIC, showCenterIndic);
        editor.apply();
        if (centerIndicSettingListener != null) {
            centerIndicSettingListener.onCenterIndicSettingChange(showCenterIndic);
        }
    }

    public boolean isLogOn() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.LOG_ON, false);
    }

    public void setLogOn(boolean logOn) {
        Logs.setLogOn(logOn);

        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.LOG_ON, logOn);
        editor.apply();
    }

    public interface CenterIndicSettingListener {
        void onCenterIndicSettingChange(boolean showCenterIndic);
    }

    public static boolean isDatabaseReady(String str) {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(str+"isDatabaseReady", false);
    }

    public static void setDatabaseReady(String str,boolean isReady) {

        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(str+"isDatabaseReady", isReady);
        editor.apply();
    }

    public static boolean isCellAddressReady(String str) {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean("isCellAddressReady"+str, false);
    }

    public static void setCellAddressReady(String str,boolean isReady) {

        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean("isCellAddressReady"+str, isReady);
        editor.apply();
    }

    public static boolean isTableExsit(String str) {
        str = str + "isTableExsit";
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(str, false);
    }

    public static void setTableExsit(String str,boolean isReady) {
        str = str + "isTableExsit";
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(str, isReady);
        editor.apply();
    }


    public boolean isTrafficEnabled() {
        SharedPreferences sharedPreferences = SharedPrefer.getInstance().open().read();
        return sharedPreferences.getBoolean(Constants.TRAFFIC_ENABLED, false);
    }

    public void setTrafficEnabled(boolean isTrafficEnabled) {
        SharedPreferences.Editor editor = SharedPrefer.getInstance().open().edit();
        editor.putBoolean(Constants.TRAFFIC_ENABLED, isTrafficEnabled);
        editor.apply();
    }
}
