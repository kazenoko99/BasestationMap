package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.basestation.Marker.ShowGsmCellMarkerTask;
import com.wenruisong.basestationmap.basestation.Marker.ShowLteCellMarkerTask;
import com.wenruisong.basestationmap.model.PhoneState;

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
    private TextView netType;
    private TextView lteRSRP;

    private static boolean showGsm = true;
    private static boolean showLte = false;

    private static boolean showPhoteState = true;

    private Context mContext;

    public void setShowPhoteStateFlag(boolean flag){
        showPhoteState = flag;
    }
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
        netType =(TextView)root.findViewById(R.id.net_type);
         lteRSRP =(TextView)root.findViewById(R.id.lte_rsrp);
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
        listenProgress();
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

    public void updatePhoneState(PhoneState phoneState){
        if(phoneState == null)
            return;
        netType.setText(phoneState.netType);
        lteRSRP.setText(phoneState.lteRSRP);
    }

    public void listenProgress(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(showPhoteState){
                    try {
                        Thread.sleep(1000);
                        mHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updatePhoneState(((MainActivity)mContext).getPhoneState());
        }
    };

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
