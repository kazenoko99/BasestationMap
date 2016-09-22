package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.MarkerCitySelectActivity;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.model.PhoneState;
import com.wenruisong.basestationmap.service.SingalAnalyzeService;

/**
 * Created by wen on 2016/3/5.
 */
public class BottomCellView implements View.OnClickListener,SingalAnalyzeService.OnCellInfoChange{
    CheckBox gsmCheckBox;
    CheckBox lteCheckBox;
    CheckBox bsNameCheckBox;

    private TextView gsmcount;
    private TextView ltecount;
    private TextView netType;
    private TextView signalStrength;
    private TextView currentCell;
    private TextView cellDistance;
    private TextView changeMakerCity;
    private TextView signalDetail;
    private TextView cellId;
    private ViewGroup rootLayout;
    private static boolean showGsm = true;
    private static boolean showLte = false;
    private  PhoneState mPhoneState;
    private static boolean showPhoteState = true;

    private Context mContext;

    public void setShowPhoteStateFlag(boolean flag){
        showPhoteState = flag;
    }
   public View initView(View root,Context context)
    {
        mContext = context;
        rootLayout =(ViewGroup)root.findViewById(R.id.map_bottom_cell);
        gsmCheckBox = (CheckBox)root.findViewById(R.id.show_gsm);
        bsNameCheckBox = (CheckBox)root.findViewById(R.id.show_bs_name);
        SingalAnalyzeService.setOnCellInfoChange(this);
//       gsmcount =(TextView)root.findViewById(R.id.cell_size);
//        ltecount =(TextView)root.findViewById(R.id.lte_size);
        netType =(TextView)root.findViewById(R.id.net_type);
        signalStrength =(TextView)root.findViewById(R.id.signal_db);
        cellId = (TextView)root.findViewById(R.id.cell_id);
        signalDetail =(TextView)root.findViewById(R.id.signal_detail);
        signalDetail.setOnClickListener(this);
        currentCell = (TextView)root.findViewById(R.id.current_cell);
        changeMakerCity =(TextView)root.findViewById(R.id.current_select_city);
        changeMakerCity.setOnClickListener(this);
        cellDistance =(TextView)root.findViewById(R.id.currentCellDistance);


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

        bsNameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.getInstance().setShowCellName(isChecked);
                MarkerManager.getInstance().setMarkerType(getMakerType());
            }
        });
        return root;
    }

    public void setVisibility(int visibility){

        rootLayout.setVisibility(visibility);
        changeMakerCity.setText(BasestationManager.getCurrentShowCity());
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
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            netType.setText(mPhoneState.netType);
            if(mPhoneState.netType.equals("LTE")) {
                signalStrength.setText(mPhoneState.lteRSRP+"dbm");
                currentCell.setText(mPhoneState.lteCellname);
                cellId.setText(mPhoneState.lteCI);
                cellDistance.setText(mPhoneState.lteCellDistance);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.current_select_city:
                Intent intent = new Intent(mContext, MarkerCitySelectActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }


    @Override
    public void onChanged(PhoneState phoneState) {
        mPhoneState = phoneState;
        if(mPhoneState == null)
            return;

        mHandler.sendEmptyMessage(0);

    }
}
