package com.wenruisong.basestationmap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.basestation.LTECell;

public class CellDetailActivity extends AppCompatActivity {
    private TextView cellName;
    private TextView bsName;

    private TextView cellId;
    private TextView cellHeight;
    private TextView cellAzumith;
    private TextView cellDistrict;
    private TextView cellDowntilt;
    private TextView cellTotalDowntilt;
    private TextView cellType;
    private TextView cellAddress;
    private TextView cellLat;
    private TextView cellLng;

    private TextView bsId;
    private TextView gsmBcch;
    private TextView gsmLac;

    private TextView lteTac;
    private TextView ltePci;
    private TextView lteEARFCN;

    private Cell mCell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCell = getIntent().getParcelableExtra("CELL");

        if(mCell.getInstanceType() ==2) {
            setContentView(R.layout.activity_cell_detail_gsm);
           // bindGsmViews();
        }else if (mCell.getInstanceType() ==4) {
            setContentView(R.layout.activity_cell_detail_lte);
           // bindLteViews();
        }


        cellType =(TextView) findViewById(R.id.cell_type);
        cellName =(TextView) findViewById(R.id.cell_name);
        bsName =(TextView) findViewById(R.id.bs_name);
        cellId =(TextView) findViewById(R.id.cell_id);
        cellLat =(TextView) findViewById(R.id.cell_lat);
        cellLng =(TextView) findViewById(R.id.cell_lng);
        cellHeight =(TextView) findViewById(R.id.cell_height);
        cellAzumith =(TextView) findViewById(R.id.cell_azimuth);
        cellDistrict =(TextView) findViewById(R.id.cell_district);
        cellDowntilt =(TextView) findViewById(R.id.cell_downtilt);
        cellTotalDowntilt =(TextView) findViewById(R.id.cell_total_downtilt);
        cellAddress = (TextView) findViewById(R.id.cell_address);

        if(mCell.getInstanceType() ==2)
        cellType.setText("GSM");
        else if (mCell.getInstanceType() ==4)
            cellType.setText("LTE");

        cellName.setText(mCell.cellName);
        bsName.setText(mCell.bsName);
        cellId.setText(""+mCell.cellid);
        cellHeight.setText(mCell.highth+"米");
        cellAzumith.setText(""+mCell.azimuth);
        cellLat.setText(""+mCell.latLng.latitude);
        cellLng.setText(""+mCell.latLng.longitude);
      //  cellDistrict.setText(mCell.cellName);
        cellDowntilt.setText(mCell.downtilt+"°");
        cellTotalDowntilt.setText(mCell.tatal_downtilt+"°");
    }

    private void bindGsmViews(){
        bsId =(TextView) findViewById(R.id.bs_id);
        gsmBcch  =(TextView) findViewById(R.id.cell_bcch);
        gsmLac =(TextView) findViewById(R.id.cell_lac);
        gsmBcch.setText(((GSMCell)mCell).bcch);
        gsmLac.setText(((GSMCell)mCell).lac);
       // bsId.setText(((GSMCell)mCell).);
    }

    private void bindLteViews(){
        ltePci  =(TextView) findViewById(R.id.cell_pci);
        lteTac =(TextView) findViewById(R.id.cell_tac);
        lteEARFCN =(TextView) findViewById(R.id.cell_earfcn);
        ltePci.setText(((LTECell)mCell).pci);
        lteTac.setText(((LTECell)mCell).tac);
        lteEARFCN.setText(((LTECell)mCell).earfcn);
    }
}
