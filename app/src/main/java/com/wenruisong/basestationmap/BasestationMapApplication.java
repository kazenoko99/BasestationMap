package com.wenruisong.basestationmap;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

public class BasestationMapApplication extends Application {
	private static Context mcontext;
	public static boolean isFirstLoc = true;
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		mcontext=getApplicationContext();
		SDKInitializer.initialize(this);
		ResourcesUtil.initContext(this);
	}
	public static Context getContext() {
		return mcontext;
	}

}