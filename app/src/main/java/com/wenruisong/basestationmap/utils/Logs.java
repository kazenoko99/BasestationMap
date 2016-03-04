package com.wenruisong.basestationmap.utils;

import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Logs {
    public final static String DEFAULT_TAG = "BasestationMap.";
    private static boolean mLogOn = false;
    private static long mLogtime = 0;
    private static boolean isInit = false;

    private static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory()
            + "/Android/data/com.meizu.net.map/error.txt";
    private static final long MAX_LOG_FILE_SIZE = 4L * 1024 * 1024;

    public static void setLogOn(boolean val) {
        mLogOn = val;
    }

    /**
     * 读取Log开关信息
     */
    private static void initLog() {
        isInit = true;
        mLogOn = true;
    }

    public static void v(String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.v(DEFAULT_TAG, log);
    }

    public static void i(String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.i(DEFAULT_TAG, log);
    }

    public static void d(String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.d(DEFAULT_TAG, log);
    }

    public static void e(String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.e(DEFAULT_TAG, log);
    }

    public static void i(String tag, String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.i(DEFAULT_TAG + tag, log);
    }

    public static void d(String tag, String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.d(DEFAULT_TAG + tag, log);
    }

    public static void w(String tag, String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.w(DEFAULT_TAG + tag, log);
    }

    public static void e(String tag, String log) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.e(DEFAULT_TAG + tag, log);
    }

    public static void resetTime() {
        mLogtime = SystemClock.elapsedRealtime();
    }

    private static String addTimeMsg(String msg) {
        return msg + " T:" + (SystemClock.elapsedRealtime() - mLogtime);
    }

    public static void time_i(String tag, String msg) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.i(DEFAULT_TAG + tag, addTimeMsg(msg));
    }

    public static void time_d(String tag, String msg) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.d(DEFAULT_TAG + tag, addTimeMsg(msg));
    }

    public static void time_w(String tag, String msg) {
        Log.w(DEFAULT_TAG + tag, addTimeMsg(msg));
        writeLogFile(DEFAULT_TAG + tag, addTimeMsg(msg));
    }

    public static void time_v(String tag, String msg) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.v(DEFAULT_TAG + tag, addTimeMsg(msg));
    }

    public static void time_e(String tag, String msg) {
        if (!isInit) {
            initLog();
        }
        if (mLogOn)
            Log.e(DEFAULT_TAG + tag, addTimeMsg(msg));
    }

    private static String currentTimeToString() {
        return DateFormat.format("yyyy-MM-dd kk:mm", System.currentTimeMillis()).toString();
    }

    public static void writeLogFile(String tag, String log) {
        writeSingleLog(tag, log, LOG_FILE_PATH, MAX_LOG_FILE_SIZE);
    }

    private static void writeSingleLog(String tag, String log, String filePath, long maxSize) {
        String logString = "" + currentTimeToString() + "  " + tag + " : " + log + "\n";
        writeLogToFile(logString, filePath, maxSize);
    }

    private static void writeLogToFile(String logString, String filePath, long maxSize) {
        try {
            File file = new File(filePath);
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            if (file.exists() && file.length() >= maxSize) {
                file.delete();
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                }
            }
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(logString);
            writer.flush();
            out.flush();
            writer.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
