package com.wenruisong.basestationmap.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

/**
 * Created by juanwu on 15-8-27.
 */
public class ResourcesUtil {
    private static Context sContext;

    public static void initContext(Context context) {
        sContext = context;
    }

    public static String getString(int resourceId) {
        return sContext.getString(resourceId);
    }

    public static int getDimensionPixelSize(int resId) {
        return  sContext.getResources().getDimensionPixelSize(resId);
    }

    public static float getDimension(int resId) {
        return  sContext.getResources().getDimension(resId);
    }

    public static int getDimensionPixelOffset(int resId) {
        return  sContext.getResources().getDimensionPixelOffset(resId);
    }

    public static Drawable getDrawable(int resId) {
        return  sContext.getResources().getDrawable(resId);
    }

    public static int getColor(int resId) {
        return  sContext.getResources().getColor(resId);
    }

    public static String[] getStringArray(int resId) {
        return  sContext.getResources().getStringArray(resId);
    }

    public static XmlResourceParser getXml(int resId) {
        return  sContext.getResources().getXml(resId);
    }

    public static InputStream openRawResource(int resId) {
        return  sContext.getResources().openRawResource(resId);
    }

    public static Resources getResources() {
        return  sContext.getResources();
    }
}
