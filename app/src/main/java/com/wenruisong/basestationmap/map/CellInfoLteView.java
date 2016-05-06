package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.basestation.LTECell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.map.CellInfoView;
import com.wenruisong.basestationmap.pano.PanoActivity;
import com.wenruisong.basestationmap.utils.DistanceUtils;
import com.wenruisong.basestationmap.utils.Logs;

/**
 * Created by wen on 2016/2/25.
 */
public class CellInfoLteView extends CellInfoView {
    private TextView celltac;
    private TextView cellpci;
    private TextView bts_name;
    private TextView cellearfcn;
    private TextView bts_id;
    private TextView cell_distance;
    private LTECell cellInfo;

    public CellInfoLteView(Context context,View root) {
        this.mContext = context;
        cellid = (TextView) root.findViewById(R.id.cell_id);
        cellname = (TextView) root.findViewById(R.id.cell_name);
        celllat = (TextView) root.findViewById(R.id.cell_lat);
        celllng = (TextView) root.findViewById(R.id.cell_lng);
        cellazumith = (TextView) root.findViewById(R.id.cell_angel);
        cellhigth = (TextView) root.findViewById(R.id.cell_higth);
        cellisshifen = (TextView) root.findViewById(R.id.cell_isshifen);
        celltac = (TextView) root.findViewById(R.id.cell_tac);
        cellpci = (TextView) root.findViewById(R.id.cell_pci);
        bts_id = (TextView) root.findViewById(R.id.bts_id);
        bts_name = (TextView) root.findViewById(R.id.bts_name);
        cell_distance = (TextView) root.findViewById(R.id.cell_distance);
        goPanorama = (TextView)root.findViewById(R.id.map_bottom_panorama);
    }

    @Override
    public void initWidget(Cell cell) {
        if (cell instanceof LTECell) {
            cellInfo = (LTECell) cell;
        }

        cellid.setText(Integer.toString(cellInfo.cellid));
        cellname.setText(cellInfo.cellName);
        celllat.setText(Double.toString(cellInfo.latLng.latitude));
        celllng.setText(Double.toString(cellInfo.latLng.longitude));
        cellpci.setText("" + cellInfo.pci);
       // cellearfcn.setText("" + cellInfo.earfcn);
        celltac.setText("" + cellInfo.tac);
        cellazumith.setText("" +(360-cellInfo.azimuth));
        cellhigth.setText("" + cellInfo.highth);
        bts_name.setText(cellInfo.bsName);
        if (LocationHelper.getInstance().isLocated) {
            BDLocation location = LocationHelper.getInstance().location;
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            cell_distance.setText(DistanceUtils.getDistance(myLatLng, cellInfo.baiduLatLng));
        }

        if (cellInfo.type == 0)
            cellisshifen.setText("否");
        else
            cellisshifen.setText("是");


        final double lat = cell.baiduLatLng.latitude;
        final double lng = cell.baiduLatLng.longitude;
        final String cellName = cell.cellName;
        final int higth = cell.highth;

        goPanorama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("cellName",cellName);
                intent.putExtra("higth",higth);
                intent.setClass(mContext, PanoActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

}

