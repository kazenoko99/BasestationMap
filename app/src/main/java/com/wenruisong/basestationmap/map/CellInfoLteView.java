package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.LTECell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.navi.NaviActivity;
import com.wenruisong.basestationmap.pano.PanoActivity;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DistanceUtils;

/**
 * Created by wen on 2016/2/25.
 */
public class CellInfoLteView extends CellInfoView {
//    private TextView celltac;
//    private TextView cellpci;
    private TextView bts_name;
//    private TextView cellearfcn;
    private TextView bts_id;
    private TextView cell_distance;
    private LTECell cellInfo;

    public CellInfoLteView(Context context,View root) {
        this.mContext = context;
        cellid = (TextView) root.findViewById(R.id.cell_id);
        cellname = (TextView) root.findViewById(R.id.cell_name);
//        celllat = (TextView) root.findViewById(R.id.cell_lat);
//        celllng = (TextView) root.findViewById(R.id.cell_lng);
        cellazumith = (TextView) root.findViewById(R.id.cell_angel);
        cellhigth = (TextView) root.findViewById(R.id.cell_higth);
        cellisshifen = (TextView) root.findViewById(R.id.cell_isshifen);
//        celltac = (TextView) root.findViewById(R.id.cell_tac);
//        cellpci = (TextView) root.findViewById(R.id.cell_pci);
        bts_id = (TextView) root.findViewById(R.id.bts_id);
        bts_name = (TextView) root.findViewById(R.id.bts_name);
        cell_distance = (TextView) root.findViewById(R.id.cell_distance);
        goPanorama = (TextView)root.findViewById(R.id.map_bottom_panorama);
        cellAddress = (TextView)root.findViewById(R.id.address);
        goNavi = (TextView)root.findViewById(R.id.map_bottom_navi);
    }

    @Override
    public void initWidget(final Cell cell) {
        if (cell instanceof LTECell) {
            cellInfo = (LTECell) cell;
        }

        cellid.setText(Integer.toString(cellInfo.cellid));
        cellname.setText(cellInfo.cellName);
//        celllat.setText(Double.toString(cellInfo.latLng.latitude));
//        celllng.setText(Double.toString(cellInfo.latLng.longitude));
//        cellpci.setText("" + cellInfo.pci);
//       // cellearfcn.setText("" + cellInfo.earfcn);
//        celltac.setText("" + cellInfo.tac);
        cellazumith.setText("" +(360-cellInfo.azimuth));
        cellhigth.setText("" + cellInfo.highth);
        bts_name.setText(cellInfo.bsName);
        cellAddress.setText(cellInfo.address);
        if (LocationHelper.getInstance().isLocated) {
            AMapLocation location = LocationHelper.getInstance().location;
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            cell_distance.setText(DistanceUtils.getDistance(myLatLng, cellInfo.aMapLatLng));
            goNavi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, NaviActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putDouble(Constants.START_LAT,LocationHelper.location.getLatitude());
                    bundle.putDouble(Constants.START_LNG,LocationHelper.location.getLongitude());

            bundle.putDouble(Constants.END_LAT, cell.aMapLatLng.latitude);
            bundle.putDouble(Constants.END_LNG,cell.aMapLatLng.longitude);
            intent.putExtra(Constants.NAVI_BUNDLE,bundle);
                    mContext.startActivity(intent);
                }
            });
        }


        if (cellInfo.type == 0)
            cellisshifen.setText("否");
        else
            cellisshifen.setText("是");


        final double lat = cell.aMapLatLng.latitude;
        final double lng = cell.aMapLatLng.longitude;
        final String cellName = cell.cellName;
        final int higth = cell.highth;

        goPanorama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CELL",cell);
                intent.setClass(mContext, PanoActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

}

