
package com.wenruisong.basestationmap.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;


import com.wenruisong.basestationmap.R;

import java.util.ArrayList;
import java.util.List;

public class NetUtil {
    private final static String TAG = "NetUtil";
    private static Context mContext;
    private static NetUtil mInstance;
    private List<NetworkChangeListener> mListeners = new ArrayList<>();
    private static NetType mLastNetworkState;


    public static enum NetType {
        NETWORK_TYPE_NONE, // 断网情况
        NETWORK_TYPE_WIFI, // WiFi模式
        NETWOKR_TYPE_MOBILE // gprs模式
    };

    public static void initContext(Context context) {
        mContext = context;
    }

    private NetUtil(Context context) {
        mContext = context;
        mLastNetworkState = getCurrentNetType();
    }

    public static NetUtil getInstance() {
        if (mInstance == null)
            mInstance = new NetUtil(mContext);
        return mInstance;
    }

    /**
     * 获取当前网络状态的类型
     * @return 返回网络类型
     */
    public NetType getCurrentNetType() {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
        NetworkInfo gprs = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs

        if (wifi != null && wifi.getState() == State.CONNECTED) {
             Logs.d(TAG, "getCurrentNetType | Current net type:  WIFI.");
            return NetType.NETWORK_TYPE_WIFI;
        } else if (gprs != null && gprs.getState() == State.CONNECTED) {
             Logs.d(TAG, "getCurrentNetType | Current net type:  MOBILE.");
            return NetType.NETWOKR_TYPE_MOBILE;
        } else {
             Logs.d(TAG, "getCurrentNetType | Current net type:  NONE.");
            return NetType.NETWORK_TYPE_NONE;
        }
    }

    public static boolean isNetAvailable(Context context) {
        if (NetUtil.getInstance().getCurrentNetType() == NetUtil.NetType.NETWORK_TYPE_NONE) {
            ToastUtil.show(context, "网络不可用");
            return false;
        }
        return true;
    }

    public void nodifyNetworkChangeListener(){
        NetType type =  getInstance().getCurrentNetType();

        if(type != mLastNetworkState){
            for (NetUtil.NetworkChangeListener listener : mListeners) {
                listener.onNetworkStatusChanged(type,mLastNetworkState);
            }
        }

        mLastNetworkState = type;
    }

    /**
     * 注册网络状态监听
     *
     * @param listener
     */
    public void registerNetworkChangeListener(NetworkChangeListener listener) {
        if(!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }

    /**
     * 取消注册网络状态监听
     *
     * @param listener
     */
    public void unRegisterNetworkChangeListener(NetworkChangeListener listener) {
        if(mListeners.contains(listener)){
            mListeners.remove(listener);
        }
    }

    public interface NetworkChangeListener {
        /**
         *
         * @param curNetType 当前网络情况
         * @param lastNetType 上一次的网络情况
         */
        public void onNetworkStatusChanged(NetUtil.NetType curNetType, NetUtil.NetType lastNetType);
    }
}
