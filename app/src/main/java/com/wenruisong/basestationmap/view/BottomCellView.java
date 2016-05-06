package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.basestation.Marker.ShowGsmCellMarkerTask;
import com.wenruisong.basestationmap.basestation.Marker.ShowLteCellMarkerTask;

/**
 * Created by wen on 2016/3/5.
 */
public class BottomCellView {
    CheckBox gsmCheckBox;
    CheckBox lteCheckBox;
    private TextView maxlon;
    private TextView maxlat;
    private TextView minon;
    private TextView minlat;
   private TextView zoom;
    private TextView gsmcount;
    private TextView ltecount;

    private static boolean showGsm = true;
    private static boolean showLte = false;
    private Context mContext;
   public View initView(Context context)
    {
        mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.map_bottom_cell, null);
        gsmCheckBox = (CheckBox)root.findViewById(R.id.show_gsm);
        maxlon =(TextView)root.findViewById(R.id.max_lon);
        maxlat =(TextView)root.findViewById(R.id.max_lat);
        minon =(TextView)root.findViewById(R.id.min_lon);
        minlat =(TextView)root.findViewById(R.id.min_lat);
         zoom =(TextView)root.findViewById(R.id.map_zoom);
       gsmcount =(TextView)root.findViewById(R.id.cell_size);
        ltecount =(TextView)root.findViewById(R.id.lte_size);
        gsmCheckBox.setChecked(true);
        lteCheckBox = (CheckBox)root.findViewById(R.id.show_lte);
        lteCheckBox.setChecked(false);
        gsmCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showGsm =isChecked;
                MarkerManager.getInstance().setMarkerType(getMakerType());
            }
        });

        lteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               showLte = isChecked;
                MarkerManager.getInstance().setMarkerType(getMakerType());
            }
        });

        return root;
    }


    public void updateView(BaiduMap map){
        MapStatus ms = map.getMapStatus();
        zoom.setText(""+ms.zoom);
        maxlon.setText(""+ms.bound.northeast.longitude);
        maxlat.setText(""+ms.bound.northeast.latitude);
        minon.setText(""+ms.bound.southwest.longitude);
        minlat.setText("" + ms.bound.southwest.latitude);

        gsmcount.setText("" + ShowGsmCellMarkerTask.cellCount);
        ltecount.setText("" + ShowLteCellMarkerTask.cellCount);

    }

    private MarkerManager.MarkerType getMakerType()
    {
        if (showGsm ==true && showLte==false) {
            return MarkerManager.MarkerType.GSM;
        }
        else if (showGsm ==false && showLte==true) {
            return MarkerManager.MarkerType.LTE;
        }
        else if (showGsm ==false && showLte==false) {
            return MarkerManager.MarkerType.NONE;
        }
        else {
            return MarkerManager.MarkerType.GSM_LTE;
        }

    }

}
