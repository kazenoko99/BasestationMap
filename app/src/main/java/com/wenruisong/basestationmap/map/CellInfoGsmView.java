package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.fragment.PanoFragment;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.pano.PanoActivity;
import com.wenruisong.basestationmap.utils.DistanceUtils;
import com.wenruisong.basestationmap.utils.ImageUtils;
import com.wenruisong.basestationmap.utils.Logs;

/**
 * Created by wen on 2016/2/25.
 */
public class CellInfoGsmView extends CellInfoView {
    private final String Tag = "CellInfoGsmView";
    private TextView celllac;
    private TextView cellbcch;
    private TextView bts_name;
    private TextView bts_id;
    private TextView cell_distance;
    private GSMCell cellInfo;
    public CellInfoGsmView(Context context,View root) {
        this.mContext = context;
        cellid = (TextView) root.findViewById(R.id.cell_id);
        cellname = (TextView) root.findViewById(R.id.cell_name);
        celllat = (TextView) root.findViewById(R.id.cell_lat);
        celllng = (TextView) root.findViewById(R.id.cell_lng);
        cellazumith = (TextView) root.findViewById(R.id.cell_angel);
        cellhigth = (TextView) root.findViewById(R.id.cell_higth);
        cellisshifen = (TextView) root.findViewById(R.id.cell_isshifen);
        celllac = (TextView) root.findViewById(R.id.cell_lac);
        cellbcch = (TextView) root.findViewById(R.id.cell_bcch);
        bts_id = (TextView) root.findViewById(R.id.bts_id);
        bts_name = (TextView) root.findViewById(R.id.bts_name);
        cell_distance = (TextView) root.findViewById(R.id.cell_distance);
        goPanorama = (TextView)root.findViewById(R.id.map_bottom_panorama);
    }

    @Override
    public void initWidget(Cell cell) {
        if (cell instanceof GSMCell) {
            cellInfo = (GSMCell) cell;
        }

        cellid.setText(Integer.toString(cellInfo.cellid));
        cellname.setText(cellInfo.cellName);
        celllat.setText(Double.toString(cellInfo.latLng.latitude));
        celllng.setText(Double.toString(cellInfo.latLng.longitude));
        cellbcch.setText("" + cellInfo.bcch);
        celllac.setText("" + cellInfo.lac);
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
