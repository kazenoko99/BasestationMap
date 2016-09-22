package com.wenruisong.basestationmap.map.poi;


import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.Logs;
import java.util.ArrayList;

/**
 * Created by wen on 2016/2/20.
 */
public class CellAddressCoderManager implements GeocodeSearch.OnGeocodeSearchListener {
    private static String TAG = "CellAddressCoderManager";
    private static CellAddressCoderManager instance;
    private static GeocodeSearch geocoderSearch;

    static ArrayList<Cell> cellsToSearch = new ArrayList<>();
    private static int searchTaskNum =0;
    private static int searchIndex = 0;
    private static boolean isStarted = false;
    private RegeocodeQuery mRegeocodeQuery;

    public static CellAddressCoderManager getInstance()
    {
        if (instance==null)
            instance=new CellAddressCoderManager();
        return instance;
    }

    CellAddressCoderManager(){
        geocoderSearch = new GeocodeSearch(BasestationMapApplication.getContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    public static void addPoint(Cell cell)
    {
        if(!isStarted)
        cellsToSearch.add(cell);
    }


    public void start()
    {
        if(cellsToSearch==null ||cellsToSearch.size()==0)
            return;

        isStarted = true;
        searchTaskNum = cellsToSearch.size();
       LatLonPoint latLonPoint  = AMapUtil.convertToLatLonPoint(cellsToSearch.get(0).aMapLatLng);
        mRegeocodeQuery = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mRegeocodeQuery.setLatLonType(GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);// 设置同步逆地理编码请求

    }



    @Override
    public void onRegeocodeSearched(RegeocodeResult reverseGeoCodeResult, int i) {
        if(searchIndex == searchTaskNum-1)
        {
            Settings.setCellAddressReady("GSM", true);
        }
        else
        {
            BasestationManager.updateDatebaseAddress(cellsToSearch.get(searchIndex).latLng, reverseGeoCodeResult.getRegeocodeAddress().getFormatAddress());
            searchIndex++;
            LatLonPoint latLonPoint  = AMapUtil.convertToLatLonPoint(cellsToSearch.get(searchIndex).aMapLatLng);
            mRegeocodeQuery.setPoint(latLonPoint);
            geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);// 设置同步逆地理编码请求
            Logs.d(TAG, "onGetReverseGeoCodeResult " + searchIndex + reverseGeoCodeResult.getRegeocodeAddress().getFormatAddress());
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
