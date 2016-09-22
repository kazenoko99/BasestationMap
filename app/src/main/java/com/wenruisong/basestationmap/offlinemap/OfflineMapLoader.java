package com.wenruisong.basestationmap.offlinemap;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.NetUtil;
import com.wenruisong.basestationmap.utils.OfflineMapUtils;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 高德离线地图下载Loader
 * <p/>
 * *************************************************************************************
 * V2.5.1版高德地图SDK离线模块使用经验和陷阱
 * <p/>
 * Author：yinjianhua
 * Time：2015-09-09
 * <p/>
 * 使用经验：
 * 1.OfflineMapManager维护4个队列：1.下载中的城市；2.下载中的省；3.下载完成的城市；4.下载完成的省；
 * <p/>
 * 2.等待队列里面的城市／省，包含再下载中的队列里；
 * <p/>
 * 3.直辖市，港澳和全国概要图，同时含有省和城市的属性，所以下载直辖市，港澳和全国概要图的时候，
 * 在城市的下载列表和省的下载列表都会同时出现；
 * <p/>
 * 4.假如调用下载省的接口，是把省当作一个整体来下载，看不到每个城市的下载进度；
 * <p/>
 * 5.由于城市与城市之间存在重复数据，所以单独下载一个省所占用的存储空间，会比分别下载这个省里面所有城市所
 * 占用的存储空间要小，但是实际功能是一样的；
 * <p/>
 * 6.默认没有进行下载的状态是CHECKUPDATE
 * <p/>
 * 7.检测更新之后，离线地图的城市的状态不会发生变化，只有它的版本号变化而已
 * <p/>
 * 8.删除离线城市的时候，会遍历寻找存储目录，所以已下载的城市越多，等待删除的时间就会约久
 * <p/>
 * 9.删除城市和取消下载队列等待的城市，都是调用delete()
 * <p/>
 * 10.假如对正在下载的城市调用delete()，delete()会先把城市暂停，然后再进行删除。所以不需要自己调用暂停接口
 * <p/>
 * 11.下载模块使用HttpUrlConnection实现，支持断点下载，线程池设置为最多5个线程
 * <p/>
 * <p/>
 * 使用陷阱：
 * 1.创建OfflineMapManage对象时候，会扫描存储文件夹看已经下载了什么城市，所以构造函数很耗时，必须放在子线程处理
 * <p/>
 * 2.某个城市的离线地图退出是什么状态，再次进来也就是什么状态，所以每次创建OfflineMapManager都必须要调用stop()
 * <p/>
 * 3.假如刚刚new完OfflineMapManage，不能马上调用stop(),必须要延时10ms之后才行，不然那些被stop()暂停了的城市，
 * 都不能继续下载，会一直处于等待状态
 * <p/>
 * 4.OfflineMapManage内部维护的队列混乱，getItemByCityName()和getOfflineMapProvinceList().get(省).get(市)
 * 分别获得的OfflineMapCity的对象都不是同一个的。更严重的是，下载的时候这两种方法获得的OfflineMapCity对象都不会被
 * 更新。
 * 所以目前的做法是自己创建了一个mCityMap，保存了所有城市的状态，所有页面用到的OfflineMapCity都引用这个Map里面的对象，
 * 然后下载的时候自己更新这个Map里面对应的OfflineMapCity
 * <p/>
 * 5.当正在下载的城市被暂停，出错或者取消，是不会自动继续等待列表的城市进行下载的。高德可能考虑有时候可能不让下一个继续，
 * 这时候自己可以再onDownload()里面判断，然后再调用restart()进行下一个城市的下载
 * <p/>
 * 6.onDownload()回调函数竟然是从子线程里面回调的，所以千万不能在这里直接进行UI更新，必须使用Handler更新。
 * 但是又由于OfflineMapManager的构造函数是在子线程创建的，所以handler假如是类似我现在这样(作为私有成员，在构造函数
 * 里创建)，new Handler后面就必须加上：new Handler(Looper.getMainLooper()),否则也是被仍为在子线程进行UI更新
 * <p/>
 * 7.调用下载接口时，会判断当前下载包的大小是否小于手机的剩余空间，假如不足会报异常，取消该下载。
 * 但是问题是：它只对单个城市进行判断，没有加上整个下载列表加起来进行判断，而且进行下一个城市的下载时也没有再次判断，
 * 导致的问题是，存储空间不足时，还不停地写文件，可能导致程序崩溃或者Anr异常。
 * 目前的解决：在添加下载时，会加上整个下载列表进行判断；在进行下一个城市下载时，再次进行一次判断；
 * <p/>
 * *************************************************************************************
 * <p/>
 * <p/>
 * Created by yinjianhua on 15-5-28.
 */
final public class OfflineMapLoader implements OfflineMapManager.OfflineMapDownloadListener,
        NetUtil.NetworkChangeListener {

    private static OfflineMapLoader mInstance;

    private OfflineMapManager mOfflineMapManager = null;// 离线地图下载控制器

    private HashMap<String, OfflineMapCity> mCityMap = new HashMap<>();
    private HashMap<String, Integer> mCityDownloadCompleteMap = new HashMap<>();
    private HashMap<String, Integer> mCityUnzipCompleteMap = new HashMap<>();

    private List<OfflineMapDownloadListener> mDownloadListeners = new ArrayList<>();
    private List<OfflineMapPauseAllListener> mPauseAllListeners = new ArrayList<>();
    private List<OfflineMapRemoveListener> mRemoveListeners = new ArrayList<>();

    private CopyOnWriteArrayList<String> mCanUpdateCity = new CopyOnWriteArrayList<>();
    private List<String> mErrorCitys = new ArrayList<>();

    private OnDownloadStateBean mCurDownloadState = null;

    private static boolean mIsInited = false;
    private static boolean mIsIniting = false; // 是否正在创建OfflineMapManager
    private boolean mIsHasError = false;    // 是否在出现下载进度错误
    private static Context mContext;

    final private int DOWNLOAD_CONPLETE_ERROR_RESUME_TIME_MS = 3000;
    final private long CHECK_DOWNLOADING_LIST_COUNT_TIME_MS = Long.MAX_VALUE;   // 约等于292471201年
    final private long CHECK_DOWNLOADING_LIST_TICK_TIME_MS = 15 * 1000;       // 15秒检查一次

    final private int HANDLE_WHAT_ON_DOWNLOAD = 201;
    final private int HANDLE_WHAT_ON_DOWNLOAD_FAIL = 202;
    final private int HANDLE_WHAT_ON_LIST_CHANGE = 203;
    final private int HANDLE_WHAT_ON_REMOVE_START = 204;
    final private int HANDLE_WHAT_ON_REMOVE_END = 205;
    final private int HANDLE_WHAT_ON_CAN_UPDATE = 206;
    final private int HANDLE_WHAT_ON_DOWNLOAD_ERROR = 207;


    final private String HANDLE_MSG_STATUS = "HANDLE_MSG_STATUS";
    final private String HANDLE_MSG_COMPLETE_CODE = "HANDLE_MSG_COMPLETE_CODE";
    final private String HANDLE_MSG_CITYNAME = "HANDLE_MSG_CITYNAME";
    final private String HANDLE_MSG_AMAP_EXCEPTION = "HANDLE_MSG_AMAP_EXCEPTION";


    final private Handler mDownloadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_WHAT_ON_DOWNLOAD:
                    int status = msg.getData().getInt(HANDLE_MSG_STATUS);
                    int completeCode = msg.getData().getInt(HANDLE_MSG_COMPLETE_CODE);
                    String cityName = msg.getData().getString(HANDLE_MSG_CITYNAME);

                    for (OfflineMapDownloadListener listener : mDownloadListeners) {
                        listener.onDownload(status, completeCode, cityName);
                    }
                    break;

                case HANDLE_WHAT_ON_LIST_CHANGE:
                    for (OfflineMapDownloadListener listener : mDownloadListeners) {
                        listener.onOfflineMapListChange();
                    }
                    break;

                case HANDLE_WHAT_ON_DOWNLOAD_FAIL:
                    String city = msg.getData().getString(HANDLE_MSG_CITYNAME);
                    String errorMessage = msg.getData().getString(HANDLE_MSG_AMAP_EXCEPTION);

                    for (OfflineMapDownloadListener listener : mDownloadListeners) {
                        listener.onDownloadFail(city, errorMessage);
                    }
                    break;

                case HANDLE_WHAT_ON_CAN_UPDATE:
                    String updateCityName = msg.getData().getString(HANDLE_MSG_CITYNAME);
                    for (OfflineMapDownloadListener listener : mDownloadListeners) {
                        listener.onCityCanUpdate(updateCityName);
                    }
            }
        }
    };

    final private Handler mRemoveHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_WHAT_ON_REMOVE_START:
                    for (OfflineMapRemoveListener listener : mRemoveListeners) {
                        listener.onRemoveStart();
                    }
                    break;

                case HANDLE_WHAT_ON_REMOVE_END:
                    for (OfflineMapRemoveListener listener : mRemoveListeners) {
                        listener.onRemoveEnd();
                    }
                    break;
            }
        }
    };

    final private Handler mErrorHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLE_WHAT_ON_DOWNLOAD_ERROR) {
                Logs.d("OfflineMapModule", "OfflineMapLoader mErrorHandler");

                mIsHasError = false;
                String cityName = msg.getData().getString(HANDLE_MSG_CITYNAME);
                mOfflineMapManager.restart();
            }
        }
    };


    private CountDownTimer timer;
    private boolean mIsDownloadingListAllWaiting = false;

    public static void initContext(Context context) {
        mContext = context;
    }

    /**
     * 由于高德SDK在创建OfflineMapManager会很耗时,所以new操作要在子线程进行
     * 所以在第一次调用getInstance之前,必须先调用initOfflineMapLoader()
     *
     * @return
     */
    public static OfflineMapLoader getInstance() {
//        if (null == mInstance) {
//            mInstance = new OfflineMapLoader(context);
//        }
//        Logs.d("OfflineMapModule", "OfflineMapLoader getInstance");

        return mInstance;
    }

    synchronized public static void initOfflineMapLoader() {
        if (null == mInstance) {

            if (!mIsIniting) {   // 避免多次初始化OfflineMapLoader
                Logs.d("OfflineMapModule", "OfflineMapLoader initOfflineMapLoader");
                mIsIniting = true;
                mInstance = new OfflineMapLoader(mContext);

            }
        }
    }

    public void finishInit() {
        if (!mIsInited) {
            mIsInited = true;
            pauseAllDownload();
            startCheckDownloadingListStatusTimer();
        }
    }

    private void startCheckDownloadingListStatusTimer() {
        Logs.d("OfflineMapModule", "OfflineMapLoader startCheckDownloadingListStatusTimer");

        timer = new CountDownTimer(CHECK_DOWNLOADING_LIST_COUNT_TIME_MS, CHECK_DOWNLOADING_LIST_TICK_TIME_MS) {

            @Override
            public void onTick(long millisUntilFinished) {
                Logs.d("OfflineMapModule", "OfflineMapLoader CountDownTimer onTick");

                if (OfflineMapLoader.getInstance() != null) {
                    OfflineMapLoader.getInstance().checkDownloadingListStatus();
                }
            }

            @Override
            public void onFinish() {
                Logs.d("OfflineMapModule", "OfflineMapLoader CountDownTimer onFinish");

            }
        };
        timer.start();
    }

    private void stopCheckDownloadingListStatusTimer() {
        Logs.d("OfflineMapModule", "OfflineMapLoader stopCheckDownloadingListStatusTimer");

    }

    /**
     * 检查下载队列是否全部城市都是处于等待中的状态
     */
    public void checkDownloadingListStatus() {
        final List<OfflineMapCity> downloadingCityList = mOfflineMapManager.getDownloadingCityList();
        boolean isNeedToRestartLoader = false;

        for (OfflineMapCity city : downloadingCityList) {
            int state = city.getState();

            // 如果发现下载队列有城市正常在下载，则认为下载正常，马上退出循环
            if (OfflineMapStatus.LOADING == state) {
                isNeedToRestartLoader = false;
                break;
            }

            // 记下有城市在等待
            if (OfflineMapStatus.WAITING == state) {
                isNeedToRestartLoader = true;
            }

        }

        Logs.d("OfflineMapModule", "OfflineMapLoader checkDownloadingListStatus isNeedToRestartLoader: " + isNeedToRestartLoader
                + " mIsDownloadingListAllWaiting: " + mIsDownloadingListAllWaiting);

        if (isNeedToRestartLoader) {
            // 如果上一次监测，下载队列也是全部都是等待，没有下载状态，则重启OfflineMapManager
            if (mIsDownloadingListAllWaiting) {
                Logs.d("OfflineMapModule", "OfflineMapLoader checkDownloadingListStatus restart");

                mIsDownloadingListAllWaiting = false;
                mOfflineMapManager.restart();

            } else {
                mIsDownloadingListAllWaiting = isNeedToRestartLoader;
            }

        } else {
            mIsDownloadingListAllWaiting = isNeedToRestartLoader;
        }
    }

    private OfflineMapLoader(Context context) {
        mOfflineMapManager = new OfflineMapManager(context, this);

        initCityMap();
        // 目的是让上次等待中的城市变成暂停状态,现在改在OfflineMapFragment里面调用pauseAll
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mOfflineMapManager.stop();
//            }
//        },10);

        try {
            checkDownloadedCityUpdate();
        } catch (AMapException e) {
            Logs.d("OfflineMapModule", "OfflineMapLoader checkDownloadedCityUpdate error: " + e.getErrorMessage());
            e.printStackTrace();
        }

        NetUtil.getInstance().registerNetworkChangeListener(this);
    }

    private void initCityMap() {
        List<OfflineMapProvince> provinceList = mOfflineMapManager.getOfflineMapProvinceList();

        mCityMap.clear();
        for (OfflineMapProvince province : provinceList) {
            for (OfflineMapCity city : province.getCityList()) {
                String cityName = city.getCity();
                mCityMap.put(cityName, mOfflineMapManager.getItemByCityName(cityName));
            }
        }
    }

    /**
     * 检测已经下载的城市的
     */
    private void checkDownloadedCityUpdate() throws AMapException {
        Logs.d("OfflineMapModule", "checkDownloadedCityUpdate");

        List<OfflineMapCity> downloadedCity = mOfflineMapManager.getDownloadOfflineMapCityList();
        mCanUpdateCity.clear();

        for (OfflineMapCity city : downloadedCity) {
            mOfflineMapManager.updateOfflineCityByName(city.getCity());
        }
    }

    @Override
    public void onDownload(int status, int completeCode, String downName) {
        Logs.d("OfflineMapModule", "onDownload status: " + status
                + " completeCode: " + completeCode + " downName " + downName);

//        if ((OfflineMapStatus.LOADING == status) && (100 == completeCode)) {
//            // 避免解压时加载地图造成数据错误
//            ArrayList<AMap> aMapList = OfflineMapUtils.getAMapList();
//            if ((aMapList != null) && (aMapList.size() > 0)) {
//                for (AMap aMap : aMapList) {
//                    if (aMap != null) {
//                        aMap.setLoadOfflineData(true);
//                    }
//                }
//            }
//        }else if((OfflineMapStatus.SUCCESS == status)
//                || (OfflineMapStatus.EXCEPTION_SDCARD == status)){
//            // 解压完成时，让地图能正常读取缓存数据
//            ArrayList<AMap> aMapList = OfflineMapUtils.getAMapList();
//            if ((aMapList != null) && (aMapList.size() > 0)) {
//                for (AMap aMap : aMapList) {
//                    if (aMap != null) {
//                        aMap.setLoadOfflineData(false);
//                    }
//                }
//            }
//        }


        if (!mIsHasError) {
            if ((mCurDownloadState != null)) {

                // 避免下载不稳定，跳动问题
                if ((mCurDownloadState.getCityName().equals(downName))
                        && (mCurDownloadState.getStatus() == status)) {

                    // 如果出现下载时候进度条不稳定，来回跳动，则暂停当前下载
                    if (completeCode < mCurDownloadState.getCompleteCode()) {
                        if (status == OfflineMapStatus.LOADING) {
                            Logs.d("OfflineMapModule", "OfflineMapLoader onDownload has complete error");
                            pauseCurDownload(downName); // 暂停当前下载
                            mIsHasError = true;
                            mCurDownloadState = null;

                            // FIXME：需要测试：换一种新方法去暂停下载中途错误的城市
                            Message msg = new Message();
                            msg.what = HANDLE_WHAT_ON_DOWNLOAD_ERROR;
                            Bundle bundle = new Bundle();
                            bundle.putString(HANDLE_MSG_CITYNAME, downName);
                            msg.setData(bundle);
                            mErrorHandler.sendMessageDelayed(msg, DOWNLOAD_CONPLETE_ERROR_RESUME_TIME_MS);

                            return;
                        }
                    }
                }

                mCurDownloadState.setCityName(downName);
                mCurDownloadState.setCompleteCode(completeCode);
                mCurDownloadState.setStatus(status);
            } else {
                mCurDownloadState = new OnDownloadStateBean(downName, status, completeCode);
            }
        }


        if (OfflineMapStatus.LOADING == status) {
            mCityDownloadCompleteMap.put(downName, new Integer(completeCode));

        } else if (OfflineMapStatus.UNZIP == status) {
            mCityUnzipCompleteMap.put(downName, new Integer(completeCode));
        }

        if (OfflineMapStatus.EXCEPTION_NETWORK_LOADING == status) { // 网络异常
            pauseCurDownload(downName); // 暂停当前下载
        } else if (OfflineMapStatus.EXCEPTION_SDCARD == status) {    // SD卡容量不足
            pauseCurDownload(downName); // 暂停当前下载
        } else if (OfflineMapStatus.EXCEPTION_AMAP == status) {      // 认证错误
            pauseCurDownload(downName); // 暂停当前下载
        } else if (OfflineMapStatus.START_DOWNLOAD_FAILD == status) {
            // 已经下载过了
            pauseCurDownload(downName); // 暂停当前下载
        } else {
            if (OfflineMapStatus.PAUSE == status) {
                if (!mIsHasError) {
                    mOfflineMapManager.restart();
                }

            } else if (OfflineMapStatus.SUCCESS == status) {
                onDownloadSuccess(downName);
            }

            // 更新CityMap
            updateCity(downName);
        }

        nodifyListenersOnDownload(status, completeCode, downName);
    }

    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
        if (hasNew) {
            mCanUpdateCity.add(name);

            nodifyListenersOnCityCanUpdate(name);
        }
    }

    @Override
    public void onRemove(boolean result, String cityName, String s2) {
        // 判断是否进行更新操作，删除后进行重新下载
        if ((mCanUpdateCity != null) && (mCanUpdateCity.contains(cityName))) {

            mCanUpdateCity.remove(cityName);

            // 通知监听：删除完成
            nodifyListenersOnRemoveEnd();

            downloadOfflineCityMap(cityName);

        } else if ((mErrorCitys != null) && (mErrorCitys.contains(cityName))) { // 对下载错误的重新下载
            mErrorCitys.remove(cityName);

            // 通知监听：删除完成
            nodifyListenersOnRemoveEnd();

            downloadOfflineCityMap(cityName);
        } else {
            // 通知监听：删除完成
            nodifyListenersOnRemoveEnd();

            // 更新CityMap对应的城市
            updateCity(cityName);

            // 通知各下载监听
            nodifyListenersOnOfflineMapListChange();
        }
    }


    /**
     * 第二重：在调度下一个城市下载时,对于存储空间是否满的判断
     */
    private void onDownloadSuccess(String cityName) {

        if (mCityDownloadCompleteMap.containsKey(cityName)) {
            mCityDownloadCompleteMap.remove(cityName);
        }

        if (mCityUnzipCompleteMap.containsKey(cityName)) {
            mCityUnzipCompleteMap.remove(cityName);
        }

        long space = OfflineMapUtils.getSDCardSurplusSpace();
        List<OfflineMapCity> downloadingList = mOfflineMapManager.getDownloadingCityList();

        if ((downloadingList != null) && (downloadingList.size() > 0)) {
            for (OfflineMapCity city : downloadingList) {

                if (OfflineMapStatus.WAITING == city.getState()) {
                    long size = city.getSize();
                    // 如果要下载的城市的N倍,超过手机剩余存储的大小,就有可能出现风险,所以暂停所有下载
                    if (size * Constants.DOWNLOAD_SAFE_ALIVE_SIZE_SCALE > space) {
                        Logs.d("OfflineMapModule", "OfflineMapLoader space no enough,will stop all downloading");
                        pauseAllDownload();
//                        mOfflineMapManager.restart();
                        // 通知监听器,下载失败
                        nodifyListenersonDownloadFail(city.getCity(), AMapException.ERROR_NOT_ENOUGH_SPACE);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 暂停当前下载
     * <p/>
     * 暂停当前下载后,假如要调度等待队列,改在onDownload回调里面接收到Pause状态在进行
     */
    public void pauseCurDownload(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader pauseCurDownload: " + cityName);
        mOfflineMapManager.pause();

        // 假如下载队列只有一个城市时,快速调用暂停和下载按钮,可能会导致该城市一直处于下载状态,所以这里要强制设置
        updateCity(cityName, OfflineMapStatus.PAUSE);

        // 通知各下载监听
        nodifyListenersOnOfflineMapListChange();
    }

    /**
     * 暂停所有下载
     */
    public void pauseAllDownload() {
        Logs.d("OfflineMapModule", "OfflineMapLoader pauseAllDownload");
        mOfflineMapManager.stop();

        // 通知各下载监听
        nodifyListenersOnOfflineMapListChange();
    }


    /**
     * 继续暂停的下载
     *
     * @param cityName
     */
    public void continueDownload(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader continueDownload: " + cityName);

        downloadOfflineCityMap(cityName);
        // 通知各下载监听
        nodifyListenersOnOfflineMapListChange();
    }

    /**
     * 停止正在下载的城市或取消等待下载
     *
     * @param cityName
     */
    public void cancelDownload(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader cancelDownload: " + cityName);

        deleteOfflineMap(cityName);
    }

    /**
     * 重新下载,用于下载状态显示失败的时候,重新下载
     *
     * @param cityName
     */
    public void reDownload(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader reDownload: " + cityName);

        mErrorCitys.add(cityName);

        // 通知监听：删除开始
        nodifyListenersOnRemoveStart();

        mOfflineMapManager.remove(cityName);

        // 重新加入到下载队列的逻辑放入检测删除完成后调用
    }

    /**
     * 对城市进行更新
     *
     * @param cityName
     */
    public void updateDownloadedCity(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader updateDownloadedCity: " + cityName);
        // 通知监听：删除开始
        nodifyListenersOnRemoveStart();

        mOfflineMapManager.remove(cityName);

        // 重新加入到下载队列的逻辑放入检测删除完成后调用
    }

    /**
     * 删除已下载城市
     *
     * @param cityName
     */
    public void deleteOfflineMap(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader deleteOfflineMap: " + cityName);

        // 通知监听：删除开始
        nodifyListenersOnRemoveStart();

        mOfflineMapManager.remove(cityName);

        // 更新CityMap
        updateCity(cityName);

        // 通知各下载监听
        nodifyListenersOnOfflineMapListChange();
    }

    /**
     * 第一重：判断是否有足够的空间存储进行下载
     * 高德目前判断是否有足够空间,只是考虑单个城市的大小是否大于剩余存储空间,而没有考虑整个下载队列总大小
     *
     * @return
     */
    public boolean isHavEnoughSpace(String cityName) {
        return isHavEnoughSpace(cityName, false);
    }


    public boolean isHavEnoughSpace(String name, boolean isProvince) {

        long space = OfflineMapUtils.getSDCardSurplusSpace();
        long sizeCount = 0;


        if (!isProvince) {    // 如果是城市
            long targerCitySize = getItemByCityName(name).getSize();
            List<OfflineMapCity> downloadList = mOfflineMapManager.getDownloadingCityList();

            if (downloadList != null) {
                for (OfflineMapCity city : downloadList) {
                    if (OfflineMapStatus.WAITING == city.getState()) {
                        sizeCount += city.getSize();
                    }
                }
            }

            // 加上目标城市的大小乘以安全倍数
            sizeCount += targerCitySize * Constants.DOWNLOAD_SAFE_ALIVE_SIZE_SCALE;

            if (sizeCount < space) {
                return true;
            }

        } else {              // 如果是省份
            OfflineMapProvince province = mOfflineMapManager.getItemByProvinceName(name);
            long targerSize = 0;

            if (null != province) {
                List<OfflineMapCity> cityList = province.getCityList();

                for (OfflineMapCity city : cityList) {
                    // 统计大小时，除去已下载的
                    if (getItemByCityName(city.getCity()).getState() != OfflineMapStatus.SUCCESS) {
                        targerSize += city.getSize();
                    }
                }
            }

            List<OfflineMapCity> downloadList = mOfflineMapManager.getDownloadingCityList();

            if (downloadList != null) {
                for (OfflineMapCity city : downloadList) {
                    if (OfflineMapStatus.WAITING == city.getState()) {
                        sizeCount += city.getSize();
                    }
                }
            }

            // 加上目标城市的大小乘以安全倍数
            sizeCount += targerSize + Constants.DOWNLOAD_PROVINEC_SAFE_ALIVE_SIZE_MB;

            if (sizeCount < space) {
                return true;
            }
        }

        return false;
    }

    /**
     * 加入下载队列
     * 直辖市,港澳和全国概要图下载虽然是被列入省,但是可以当做城市来下载
     *
     * @param cityName
     */
    public void downloadOfflineCityMap(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader downloadOfflineCityMap: " + cityName);

        // 判断是否有足够的存储空间
        if (!isHavEnoughSpace(cityName)) {
            // 通知监听器,下载失败
            nodifyListenersonDownloadFail(cityName, AMapException.ERROR_NOT_ENOUGH_SPACE);
            return;
        }

        try {
            mOfflineMapManager.downloadByCityName(cityName);
        } catch (AMapException e) {
            e.printStackTrace();

            if (e.getErrorMessage().equals(AMapException.ERROR_CONNECTION)) {
                Logs.d("OfflineMapModule", "OfflineMapLoader downloadOfflineCityMap: AMapException.ERROR_CONNECTION " + cityName);
//                pauseCurDownload(cityName);
                nodifyListenersonDownloadFail(cityName, e.getErrorMessage());
                return;

            } else if (e.getErrorMessage().equals(AMapException.ERROR_NOT_ENOUGH_SPACE)) {
                Logs.d("OfflineMapModule", "OfflineMapLoader downloadOfflineCityMap: AMapException.ERROR_NOT_ENOUGH_SPACE " + cityName);
//                pauseCurDownload(cityName);
//                updateCity(cityName, OfflineMapStatus.PAUSE);
                nodifyListenersonDownloadFail(cityName, e.getErrorMessage());
                return;

            } else if (e.getErrorMessage().equals(AMapException.ERROR_FAILURE_AUTH)) {
                Logs.d("OfflineMapModule", "OfflineMapLoader downloadOfflineCityMap: AMapException.ERROR_FAILURE_AUTH " + cityName);
//                pauseCurDownload(cityName);
                nodifyListenersonDownloadFail(cityName, e.getErrorMessage());
                return;
            }
        }

        int state = mOfflineMapManager.getItemByCityName(cityName).getState();
        if (OfflineMapStatus.WAITING == state) {
            mOfflineMapManager.restart();
        }

        // 更新CityMap
        updateCity(cityName);

        // 通知各下载监听
        nodifyListenersOnOfflineMapListChange();
    }


    /**
     * 下载一个省
     *
     * @param province
     */
    public void downloadOfflineProvinceMap(String province) {
        Logs.d("OfflineMapModule", "OfflineMapLoader downloadOfflineProvinceMap: " + province);

        List<OfflineMapCity> cityList = mOfflineMapManager.getItemByProvinceName(province).getCityList();

        for (OfflineMapCity city : cityList) {
            if ((OfflineMapStatus.CHECKUPDATES == getItemByCityName(city.getCity()).getState())
                    || (OfflineMapStatus.PAUSE == getItemByCityName(city.getCity()).getState())
                    || (OfflineMapStatus.ERROR == getItemByCityName(city.getCity()).getState())) {
                downloadOfflineCityMap(city.getCity());
            }
        }
    }


    /**
     * 获取所有正在下载和已经下载好的城市列表
     *
     * @return
     */
    public List<OfflineMapCity> getAllDownloadCityList() {
        Logs.d("OfflineMapModule", "OfflineMapLoader getAllDownloadCityList");

        List<OfflineMapCity> allDownloadCityList = new ArrayList<>();
        List<OfflineMapCity> downloadingCityList = mOfflineMapManager.getDownloadingCityList();
        List<OfflineMapCity> downloadedCityList = mOfflineMapManager.getDownloadOfflineMapCityList();

        for (OfflineMapCity city : downloadingCityList) {
            // 下载中的城市位于最顶部
            if ((OfflineMapStatus.LOADING == city.getState())
                    || (OfflineMapStatus.UNZIP == city.getState())) {
                allDownloadCityList.add(getItemByCityName(city.getCity()));
            }
        }

        for (OfflineMapCity city : downloadingCityList) {
            // 加入非下载中的城市（等待,暂停和错误状态的城市）
            if (!((OfflineMapStatus.LOADING == city.getState())
                    || (OfflineMapStatus.UNZIP == city.getState()))) {

                allDownloadCityList.add(getItemByCityName(city.getCity()));
            }
        }

        for (OfflineMapCity city : downloadedCityList) {
            allDownloadCityList.add(getItemByCityName(city.getCity()));
        }

//        allDownloadCityList.addAll(mOfflineMapManager.getDownloadingCityList());
//        allDownloadCityList.addAll(mOfflineMapManager.getDownloadOfflineMapCityList());

        return allDownloadCityList;
    }

    /**
     * 获取下载完成的城市列表
     *
     * @return
     */
    public List<OfflineMapCity> getDownloadedCityList() {
        Logs.d("OfflineMapModule", "OfflineMapLoader getDownloadedCityList");

        return mOfflineMapManager.getDownloadOfflineMapCityList();
    }


    public List<OfflineMapProvince> getOfflineMapProvinceList() {
        Logs.d("OfflineMapModule", "OfflineMapLoader getOfflineMapProvinceList");

        return mOfflineMapManager.getOfflineMapProvinceList();
    }

    public boolean isProvince(String name) {
        Logs.d("OfflineMapModule", "OfflineMapLoader isProvince: " + name);

        if ((name.equals(ResourcesUtil.getString(R.string.offline_map_city_gaiyaotu)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_beijing)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_shanghai)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_tianjin)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_chongqing)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_hongkong)))
                || (name.equals(ResourcesUtil.getString(R.string.offline_map_city_macau)))) {

            return false;
        } else if (mOfflineMapManager.getItemByProvinceName(name) != null) {
            return true;
        }
        return false;

    }

    public OfflineMapProvince getItemByProvinceName(String province) {
        return mOfflineMapManager.getItemByProvinceName(province);
    }

    public OfflineMapCity getItemByCityName(String cityName) {
        if (mCityMap.containsKey(cityName)) {
            return mCityMap.get(cityName);
        }

        return null;

//        return mOfflineMapManager.getItemByCityName(cityName);
    }


    public HashMap<String, Integer> getCityDownloadCompleteMap() {
        return mCityDownloadCompleteMap;
    }

    public HashMap<String, Integer> getCityUnzipCompleteMap() {
        return mCityUnzipCompleteMap;
    }

    /**
     * 更新CityMap对应的城市
     *
     * @param cityName
     */
    private void updateCity(String cityName) {
        if (mCityMap.containsKey(cityName)) {
            mCityMap.get(cityName).setState(mOfflineMapManager.getItemByCityName(cityName).getState());
        }
    }

    private void updateCity(String cityName, int status) {
        if (mCityMap.containsKey(cityName)) {
            mCityMap.get(cityName).setState(status);
        }
    }


    public List<String> getCanUpdateCityList() {
        Logs.d("OfflineMapModule", "OfflineMapLoader getCanUpdateCityList");

//        if (mCanUpdateCityList == null) {
//            mCanUpdateCityList = new ArrayList<>();
//            List<OfflineMapCity> downloadedCityList = getDownloadedCityList();
//
//            for (OfflineMapCity city : downloadedCityList) {
//                boolean update = mOfflineMapManager.updateOfflineCityByName(city.getCity());
//                if (update) {
//                    mCanUpdateCityList.add(city.getCity());
//                }
//            }
//        }

//        return mCanUpdateCityList;

        return mCanUpdateCity;
    }

    public final boolean isDownloading() {
        Logs.d("OfflineMapModule", "OfflineMapLoader isDownloading");

        if (mOfflineMapManager != null) {
            // FIXME:该功能待测试
            int downloadintCityCount = mOfflineMapManager.getDownloadingCityList().size();

            if (downloadintCityCount > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 网络状态监听
     *
     * @param curNetType
     */
    @Override
    public void onNetworkStatusChanged(NetUtil.NetType curNetType, NetUtil.NetType lastNetType) {
        Logs.d("OfflineMapModule", "OfflineMapLoader onNetworkStatusChanged: curNetType: " + curNetType
                + " lastNetType: " + lastNetType);

        if ((lastNetType == NetUtil.NetType.NETWORK_TYPE_WIFI) && (curNetType != NetUtil.NetType.NETWORK_TYPE_WIFI)) {

            if (mInstance != null) {
                if ((mPauseAllListeners != null) && (mInstance.isDownloading())) {
                    for (OfflineMapPauseAllListener listener : mPauseAllListeners) {
                        listener.pauseAllDownloadCauseWifiStop();
                    }
                }

                mInstance.pauseAllDownload();
            }
        }
    }

    public void destory() {
        Logs.d("OfflineMapModule", "OfflineMapLoader onDestory");
        mOfflineMapManager.destroy();
        stopCheckDownloadingListStatusTimer();
        NetUtil.getInstance().unRegisterNetworkChangeListener(this);
    }

    /**
     * 注册下载监听
     *
     * @param listener
     */
    public void registerOfflineMapDownloadListener(OfflineMapDownloadListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader registerOfflineMapDownloadListener");

        if (!mDownloadListeners.contains(listener)) {
            mDownloadListeners.add(listener);
        }
    }

    /**
     * 取消注册下载监听
     *
     * @param listener
     */
    public void unregisterOfflineMapDownloadListener(OfflineMapDownloadListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader unregisterOfflineMapDownloadListener");

        if (mDownloadListeners.contains(listener)) {
            mDownloadListeners.remove(listener);
        }
    }

    private void nodifyListenersOnDownload(int status, int completeCode, String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnDownload");

        Message msg = new Message();
        msg.what = HANDLE_WHAT_ON_DOWNLOAD;
        Bundle bundle = new Bundle();
        bundle.putInt(HANDLE_MSG_STATUS, status);
        bundle.putInt(HANDLE_MSG_COMPLETE_CODE, completeCode);
        bundle.putString(HANDLE_MSG_CITYNAME, cityName);

        msg.setData(bundle);

        mDownloadHandler.sendMessage(msg);
    }


    private void nodifyListenersOnCityCanUpdate(String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnCityCanUpdate");

        Message msg = new Message();
        msg.what = HANDLE_WHAT_ON_CAN_UPDATE;
        Bundle bundle = new Bundle();
        bundle.putString(HANDLE_MSG_CITYNAME, cityName);

        msg.setData(bundle);

        mDownloadHandler.sendMessage(msg);
    }

    private void nodifyListenersonDownloadFail(String cityName, String errorMessage) {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnDownload");

        Message msg = new Message();
        msg.what = HANDLE_WHAT_ON_DOWNLOAD_FAIL;
        Bundle bundle = new Bundle();
        bundle.putString(HANDLE_MSG_CITYNAME, cityName);
        bundle.putString(HANDLE_MSG_AMAP_EXCEPTION, errorMessage);

        msg.setData(bundle);

        mDownloadHandler.sendMessage(msg);
    }


    private void nodifyListenersOnOfflineMapListChange() {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnOfflineMapListChange");

        mDownloadHandler.sendEmptyMessage(HANDLE_WHAT_ON_LIST_CHANGE);
    }

    public void registerOfflineMapPauseAllListener(OfflineMapPauseAllListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader registerOfflineMapPauseAllListener");

        if (!mPauseAllListeners.contains(listener)) {
            mPauseAllListeners.add(listener);
        }
    }

    public void unregisterOfflineMapPauseAllListener(OfflineMapPauseAllListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader unregisterOfflineMapPauseAllListener");

        if (mPauseAllListeners.contains(listener)) {
            mPauseAllListeners.remove(listener);
        }
    }


    /**
     * 注册删除监听
     *
     * @param listener
     */
    public void registerOfflineMapRemoveListener(OfflineMapRemoveListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader registerOfflineMapRemoveListener");

        if (!mRemoveListeners.contains(listener)) {
            mRemoveListeners.add(listener);
        }
    }

    /**
     * 取消注册删除监听
     *
     * @param listener
     */
    public void unregisterOfflineMapRemoveListener(OfflineMapRemoveListener listener) {
        Logs.d("OfflineMapModule", "OfflineMapLoader unregisterOfflineMapRemoveListener");

        if (mRemoveListeners.contains(listener)) {
            mRemoveListeners.remove(listener);
        }
    }


    private void nodifyListenersOnRemoveStart() {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnRemoveStart");

        mRemoveHandler.sendEmptyMessage(HANDLE_WHAT_ON_REMOVE_START);
    }

    private void nodifyListenersOnRemoveEnd() {
        Logs.d("OfflineMapModule", "OfflineMapLoader nodifyListenersOnRemoveEnd");

        mRemoveHandler.sendEmptyMessage(HANDLE_WHAT_ON_REMOVE_END);
    }


    public interface OfflineMapDownloadListener {
        public void onDownloadStart(OfflineMapCity city);

        public void onDownloadFail(String city, String errorMessage);

        public void onDownload(int status, int completeCode, String cityName);

        public void onCityCanUpdate(String cityName);

        public void onOfflineMapListChange();
    }

    public interface OfflineMapRemoveListener {
        public void onRemoveStart();

        public void onRemoveEnd();
    }

    public interface OfflineMapPauseAllListener {
        public void pauseAllDownloadCauseWifiStop();
    }

    private class OnDownloadStateBean {
        private String cityName;
        private int status;
        private int completeCode;

        public OnDownloadStateBean(String cityName, int status, int completeCode) {
            this.cityName = cityName;
            this.status = status;
            this.completeCode = completeCode;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCompleteCode() {
            return completeCode;
        }

        public void setCompleteCode(int completeCode) {
            this.completeCode = completeCode;
        }
    }

}