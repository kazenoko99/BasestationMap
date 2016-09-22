package com.wenruisong.basestationmap.offlinemap;

import android.view.View;

import com.amap.api.maps.offlinemap.OfflineMapCity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yinjianhua on 15-6-12.
 */
public interface OfflineMapDownloadViewModel extends OfflineMapLoader.OfflineMapDownloadListener,
        android.widget.AdapterView.OnItemLongClickListener,
        OfflineMapLoader.OfflineMapPauseAllListener{

    public List<OfflineMapCity> getAllDownloadCitys();

//    public String getDownloadingCityName();
//
//    public int getDownloadingComplete();
//
//    public String getUnzipingCityName();
//
//    public int getUnzipingComplete();

    public HashMap<String,Integer> getCityDownloadCompleteMap();

    public HashMap<String,Integer> getCityUnzipCompleteMap();

    public List<String> getCanUpdateCityList();

    public View.OnClickListener getDownloadCtrlClickListener();

    public View.OnClickListener getStopClickListener();

    public void onDestory();

    public boolean isShouldShowRecommendView(); // 是否应该显示下载推荐

    public List<OfflineMapCity> getRecommendCitys();

}
