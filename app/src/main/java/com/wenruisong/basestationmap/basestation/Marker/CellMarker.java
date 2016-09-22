package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.wenruisong.basestationmap.basestation.Cell;

/**
 * Created by wen on 2016/2/28.
 */
public abstract class CellMarker {

     Cell cell;
    public Marker cellMarker;

    public void setCell(Cell cell) {
        this.cell = cell;
    }


    abstract void showInMap(AMap aMap);

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
