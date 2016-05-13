package com.wenruisong.basestationmap.map.poi;

import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.utils.Logs;
import java.util.ArrayList;

/**
 * Created by wen on 2016/2/20.
 */
public class PoiCoderManager implements OnGetGeoCoderResultListener{
    private static String TAG = "PoiCoderManager";
    private static PoiCoderManager instance;
    private static GeoCoder GeoCoderSearcher = GeoCoder.newInstance();

    static ArrayList<Cell> cellsToSearch = new ArrayList<>();
    private static int searchTaskNum =0;
    private static int searchIndex = 0;
    private static boolean isStarted = false;

    public static  PoiCoderManager getInstance()
    {
        if (instance==null)
            instance=new PoiCoderManager();
        return instance;
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
        GeoCoderSearcher.reverseGeoCode(new ReverseGeoCodeOption()
                .location(cellsToSearch.get(0).baiduLatLng));
        GeoCoderSearcher.setOnGetGeoCodeResultListener(this);

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if(searchIndex == searchTaskNum-1)
        {
            Settings.setCellAddressReady("GSM", true);
        }
        else
        {
            BasestationManager.updateDatebaseAddress(cellsToSearch.get(searchIndex).latLng, reverseGeoCodeResult.getAddress());
            searchIndex++;
            GeoCoderSearcher.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(cellsToSearch.get(searchIndex).baiduLatLng));
            Logs.d(TAG, "onGetReverseGeoCodeResult " + searchIndex + reverseGeoCodeResult.getAddress());
        }
    }

}
