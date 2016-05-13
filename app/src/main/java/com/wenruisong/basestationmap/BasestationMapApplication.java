package com.wenruisong.basestationmap;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
//25:A2:C7:3B:00:48:BC:E3:15:EA:87:70:22:60:4A:4C:DD:C7:89:B6
public class BasestationMapApplication extends Application {
	private static Context mContext;
	public static boolean isFirstLoc = true;
	public static boolean isPhonteStateNotificationShow = true;
	private static BasestationMapApplication mInstance = null;
	public static volatile Handler applicationHandler = null;
	public BMapManager mBMapManager = null;
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		mContext=getApplicationContext();
		SDKInitializer.initialize(this);
		ResourcesUtil.initContext(this);
		mInstance = this;
		applicationHandler = new Handler(mContext.getMainLooper());
		initEngineManager(this);
	}
	public static Context getContext() {
		return mContext;
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(new MyGeneralListener())) {
			Toast.makeText(BasestationMapApplication.getInstance().getApplicationContext(), "BMapManager  初始化错误!",
					Toast.LENGTH_LONG).show();
		}
		Log.d("ljx", "initEngineManager");
	}

	public static BasestationMapApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(mContext,
						"请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mContext, "key认证成功", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

}