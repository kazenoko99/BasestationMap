package com.wenruisong.basestationmap.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程工具类
 *
 * @author chenshaochuan
 */
public class ThreadUtils {

    private final static String TAG = ThreadUtils.class.getSimpleName();

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(128);

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;


    private static final int KEEP_ALIVE = 1;

    private final static Handler MAIN_LOOPER_HANDLER = new Handler(Looper.getMainLooper());

    private final static Printer Defualt_Printer = new LogsPrinter();

    private static Printer mDebuggerPrinter = Defualt_Printer;

    private final static ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE,
                    TimeUnit.SECONDS,
                    sPoolWorkQueue,
                    new InnerThreadFactory()
            );

    public final static void setPrinter(Printer printer) {
        if (printer == null) {
            throw new IllegalArgumentException("ThreadUtils | printer must not be null");
        }

        mDebuggerPrinter = printer;
    }

    public static void doInBackground(final Runnable r) {
        doInBackground(r, null);
    }

    public static void doInUiThread(Runnable r) {
        doInUiThread(r, null);
    }

    public static void doInUiThread(Runnable r, long delay) {
        doInUiThread(r, delay, null);
    }

    public static void doInBackground(final Runnable r, long delay) {
        doInBackground(r, delay, null);
    }

    public static void doInBackground(final Runnable r, Debugger debugger) {
        EXECUTOR.execute(new DebuggerRunnable(r, new DebuggerWrapper(debugger)));
    }

    public static void doInUiThread(Runnable r, Debugger debugger) {
        MAIN_LOOPER_HANDLER.post(new DebuggerRunnable(r, new DebuggerWrapper(debugger)));
    }

    public static void doInUiThread(Runnable r, long delay, Debugger debugger) {
        MAIN_LOOPER_HANDLER.postDelayed(new DebuggerRunnable(r, new DebuggerWrapper(debugger)), delay);
    }

    public static void doInBackground(final Runnable r, long delay, final Debugger debugger) {
        MAIN_LOOPER_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                doInBackground(new DebuggerRunnable(r, new DebuggerWrapper(debugger)));
            }
        }, delay);
    }

    public static String getName() {
        return Thread.currentThread().getName();
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class InnerThreadFactory implements ThreadFactory {

        private long mThreadId = 0;

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ThreadUtils#<" + (mThreadId++) + ">");
        }
    }

    private static class DebuggerRunnable implements Runnable {

        Runnable mRunnable = null;

        private Debugger mDebugger;

        private DebuggerRunnable(Runnable r, Debugger debugger) {
            if (r == null) {
                throw new IllegalArgumentException("Runnable must not be null");
            }

            mRunnable = r;
            mDebugger = debugger;
        }

        @Override
        public void run() {
            if (mDebugger != null)
                mDebugger.onPreExecute();

            mRunnable.run();

            if (mDebugger != null)
                mDebugger.onPostExecute();
        }

    }

    private static interface Printer {
        void println(String msg);
    }


    private static class LogsPrinter implements Printer {

        @Override
        public void println(String msg) {
            Logs.d(TAG, msg);
        }
    }

    public static abstract class Debugger {
        /**
         * execurte in execute thread
         */
        protected void onPreExecute() {

        }

        /**
         * execurte in execute thread
         */
        protected void onPostExecute() {

        }

        /**
         * execurte in execute thread
         */
        protected abstract String getMsg();
    }

    private static class DebuggerWrapper extends Debugger {

        private Debugger mDebugger;

        private long mThreadStartTime;

        Printer mPrinter;

        private DebuggerWrapper(Debugger debugger) {
            this.mDebugger = debugger;

            mPrinter = mDebuggerPrinter == null ? new LogsPrinter() : mDebuggerPrinter;
        }

        @Override
        protected void onPreExecute() {
            mThreadStartTime = System.currentTimeMillis();

            if (mDebugger != null) {
                mDebugger.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute() {
            if (mDebugger != null) {
                mDebugger.onPostExecute();
            }

            mPrinter.println(getMsg() + " useTime= " + (System.currentTimeMillis() - mThreadStartTime)
                    + "ms " + " currentTime= " + System.currentTimeMillis());
        }

        @Override
        protected String getMsg() {
            String msg = "ThreadName= " + getName();
            return mDebugger == null ? msg : mDebugger.getMsg() + "  " + msg;
        }
    }
}
