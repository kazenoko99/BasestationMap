package com.wenruisong.basestationmap.offlinemap;

import com.amap.api.maps.offlinemap.OfflineMapCity;

import java.util.List;

/**
 * Created by yinjianhua on 15-6-12.
 */
public interface OfflineMapCityView {
    public void setToDoWorkAynsc(AynscWorkListener aynscWorkListener, boolean showDialogInner);

    public void updateCityList();

    public void updateSearchList(List<OfflineMapCity> result);

    public void setCityListViewVisibility(int visibility);

    public void setSearchListViewVisibility(int visibility);

    public void setSearchEditHint(String text);

    public OfflineMapCity getSearchListItemName(int pos);
}
