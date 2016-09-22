
package com.wenruisong.basestationmap.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil {
    private static final String TAG = "DisplayUtil";
    private static DisplayMetrics mDisplayMetrics;
    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }


    public static void showDisplayDetail() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWManager.getDefaultDisplay().getMetrics(dm);
        Logs.e("dm=" + dm.toString());
    }

    public static void initDisplayDm() {
        DisplayMetrics dm = new DisplayMetrics();
        //context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager mWManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWManager.getDefaultDisplay().getMetrics(dm);
        mDisplayMetrics = dm;
        Logs.d(TAG, "initDisplayDm | dm=" + dm.toString());
    }

    public static Display getDisplay() {
        WindowManager mWManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return mWManager.getDefaultDisplay();
    }

    public static DisplayMetrics getDisplayDm() {
        return mDisplayMetrics;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    private void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public static void clearFullScreen(Activity activity){
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        );
    }

    public static void setFullScreen(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public  int getStatusBarHeight(){
        Resources resources = Resources.getSystem();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        Log.d("wenruisong","getStatusBarHeight"+result);
        return result;
    }


    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .widthPixels ;
    }

}
