package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.CellDetailActivity;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.navi.NaviActivity;
import com.wenruisong.basestationmap.pano.PanoActivity;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DistanceUtils;

/**
 * Created by wen on 2016/2/25.
 */
public class CellInfoGsmView extends CellInfoView {
    private final String Tag = "CellInfoGsmView";
    private TextView bts_name;
    private TextView bts_id;
    private TextView cell_distance;
    private GSMCell cellInfo;
    public CellInfoGsmView(Context context,View root) {
        this.mContext = context;
        cellid = (TextView) root.findViewById(R.id.cell_id);
        cellname = (TextView) root.findViewById(R.id.cell_name);
//        celllat = (TextView) root.findViewById(R.id.cell_lat);
//        celllng = (TextView) root.findViewById(R.id.cell_lng);
        cellazumith = (TextView) root.findViewById(R.id.cell_angel);
        cellhigth = (TextView) root.findViewById(R.id.cell_higth);
        cellisshifen = (TextView) root.findViewById(R.id.cell_isshifen);
        cellDetail = (TextView)root.findViewById(R.id.go_cell_detail);
//        celllac = (TextView) root.findViewById(R.id.cell_lac);
//        cellbcch = (TextView) root.findViewById(R.id.cell_bcch);
//        bts_id = (TextView) root.findViewById(R.id.bts_id);
        goNavi = (TextView)root.findViewById(R.id.map_bottom_navi);
        bts_name = (TextView) root.findViewById(R.id.bts_name);
        cell_distance = (TextView) root.findViewById(R.id.cell_distance);
        goPanorama = (TextView)root.findViewById(R.id.map_bottom_panorama);
        cellAddress = (TextView)root.findViewById(R.id.address);
    }

    @Override
    public void initWidget(final Cell cell) {
        if (cell instanceof GSMCell) {
            cellInfo = (GSMCell) cell;
        }

        cellid.setText(Integer.toString(cellInfo.cellid));
        cellname.setText(cellInfo.cellName);
//        celllat.setText(Double.toString(cellInfo.latLng.latitude));
//        celllng.setText(Double.toString(cellInfo.latLng.longitude));
//        cellbcch.setText("" + cellInfo.bcch);
//        celllac.setText("" + cellInfo.lac);
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

        cellDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CellDetailActivity.class);
                intent.putExtra("CELL",cell);
                mContext.startActivity(intent);
            }
        });

        if (cellInfo.type == 1)
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
