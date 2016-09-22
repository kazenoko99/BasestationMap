package com.wenruisong.basestationmap;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.MapsInitializer;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.wenruisong.basestationmap.group.User;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.offlinemap.OfflineMapLoader;
import com.wenruisong.basestationmap.utils.CrashHandler;
import com.wenruisong.basestationmap.utils.DisplayUtil;
import com.wenruisong.basestationmap.utils.NetUtil;
import com.wenruisong.basestationmap.utils.OfflineMapUtils;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

//25:A2:C7:3B:00:48:BC:E3:15:EA:87:70:22:60:4A:4C:DD:C7:89:B6
//DF:70:18:FE:C2:51:22:1C:3A:C8:49:22:80:10:D8:E1:E2:C9:C8:EF PC
public class BasestationMapApplication extends Application {
	private static Context mContext;
	public static long startTime;
	public static boolean isFirstLoc = true;
	public static boolean isPhonteStateNotificationShow = true;
	private static BasestationMapApplication mInstance = null;
	public static volatile Handler applicationHandler = null;
	@Override
	public void onCreate() {
		startTime = System.currentTimeMillis();
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		mContext=getApplicationContext();
		LocationHelper.getInstance().StartLocating();
		ResourcesUtil.initContext(this);
		NetUtil.initContext(this);
		DisplayUtil.initContext(this);
		OfflineMapLoader.initContext(this);
		CrashHandler.getInstance().init(mContext);
		// 设置地图数据的存储目录
		MapsInitializer.sdcardDir = OfflineMapUtils.getSdCacheDir();
		mInstance = this;
		applicationHandler = new Handler(mContext.getMainLooper());
		initEngineManager(this);
		Bmob.initialize(this, "36ff32da034301bf7f55c5d4df2a16b8");
		Log.d("starttime","Application onCreate"+(System.currentTimeMillis()-startTime));
	}
	public static Context getContext() {
		return mContext;
	}

	public User getCurrentUser() {
		User user = BmobUser.getCurrentUser(this, User.class);
		if(user!=null){
			return user;
		}
		return null;
	}


	public static BasestationMapApplication getInstance() {
		return mInstance;
	}

	public BMapManager mBMapManager = null;

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(new MyGeneralListener())) {
			Toast.makeText(getContext(), "BMapManager  初始化错误!",
					Toast.LENGTH_LONG).show();
		}
		Log.d("ljx", "initEngineManager");
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(getContext(),
						"请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getContext(), "key认证成功", Toast.LENGTH_LONG)
						.show();
			}
		}
	}


}