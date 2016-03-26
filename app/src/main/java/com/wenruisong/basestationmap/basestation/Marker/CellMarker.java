package com.wenruisong.basestationmap.basestation.Marker;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;

import java.util.Objects;

/**
 * Created by wen on 2016/2/28.
 */
public abstract class CellMarker {

     Cell cell;
    public Marker cellMarker;

    public void setCell(Cell cell) {
        this.cell = cell;
    }


    abstract void showInMap(BaiduMap baiduMap);

    public void remove()
    {
        if(cellMarker!=null) {
            cell.isShow = false;
            cellMarker.remove();
            cellMarker = null;
        }
    }

    @Override
    protected void finalize()
    {
        remove();
    }
}
