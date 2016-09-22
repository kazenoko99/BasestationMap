package com.wenruisong.basestationmap.utils;

import android.content.Context;
import android.os.PowerManager;

import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.common.Settings;


/**
 * Created by wuxuexiang on 15-9-7.
 */
public class WakeLockUtils implements Settings.ScreenOnListener {
    private static final String TAG = WakeLockUtils.class.getSimpleName();
    /**
     * 休眠管理
     */
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPowerManager;

    public void acquireScreen() {
        Logs.d(TAG, "acquireScreen");
        Settings.getInstance().setScreenOnListener(this);
        if (mWakeLock != null) {
            try {
                if (!mWakeLock.isHeld()) {
                    Logs.e(TAG, "wakeLock acquire");
                    mWakeLock.acquire();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseScreen() {
        Logs.d(TAG, "releaseScreen");
        Settings.getInstance().setScreenOnListener(null);
        if (mWakeLock != null) {
            try {
                if (mWakeLock.isHeld()) {
                    Logs.e(TAG, "wakeLock release");
                    mWakeLock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onKeepScreenChange(boolean keepSreenOn) {
        if (keepSreenOn) {
            acquireScreen();
        } else {
            releaseScreen();
        }
    }

    public void onCreate(String wakeName, int type) {
        try {
            mPowerManager = (PowerManager) BasestationMapApplication.getContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(type, wakeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
