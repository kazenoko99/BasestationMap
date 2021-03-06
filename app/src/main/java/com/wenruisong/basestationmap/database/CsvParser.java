package com.wenruisong.basestationmap.database;


import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.csvreader.CsvReader;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by wen on 2016/1/24.
 */
public class CsvParser {
    private static String Tag = "CsvPaser";
    private static String dot = ",";
    private static String inset = "INSERT INTO ";
    private static String values = " VALUES(";
    private static String _dot = "',";
    private static String dot_ = ",'";
    private static String _dot_ = "','";
    private static String sqlend = ");";
    private static  CoordinateConverter converter  = new CoordinateConverter(BasestationMapApplication.getContext());
    public static int csvGetRows(String path) {
        CsvReader r = null;
        int rows = 0;
        try {
            r = new CsvReader(path, ',', Charset.forName("GBK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //读取表头
        try {
            r.readHeaders();
        //逐条读取记录，直至读完
        while (r.readRecord()) {
            //读取一条记录
            rows++;
        }
        r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public static void csvToDatebaseGSM(SQLiteDatabase db,String city,CsvReader r,int index) {
        StringBuilder sb = new StringBuilder();
        //读取表头
        try {
            r.readRecord();
                //读取一条记录
                String gsm_cid = getString(r, Constants.GSM_CID);
                String name = getString(r, Constants.NAME);
                String bs = getString(r, Constants.BS);
                String gsm_lac = getString(r, Constants.GSM_LAC);
                String gsm_bcch = getString(r, Constants.GSM_BCCH);
                String lat = getString(r, Constants.LAT);
                String lon = getString(r, Constants.LON);
                String azimuth = getString(r, Constants.AZIMUTH);
                String total_downtilt = getString(r, Constants.TOTAL_DOWNTILT);
                String downtilt = getString(r, Constants.DOWNTILT);
                String height = getString(r, Constants.HEIGHT);
                String type = getString(r, Constants.TYPE);
             //   String baidu_latlng= g2bLatLngParser(lat, lon);

            converter.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标
            converter.coord(new LatLng(Double.valueOf(lat),Double.valueOf(lon)));
            LatLng baiduLatLng = converter.convert();
            //按列名读取这条记录的值
                // INSERT INTO gsm_cells VALUES(60251,'台连室分','',27031,1,113.9498,27.1302,08,8,30,1);
            //CID	NAME BS	LAC	BCCH	LAT	LON	AZIMUTH	TOTAL_DOWNTILT	DOWNTILT	HEIGHT	TYPE
            String getGSMCellSQL =  sb.append(inset).append(Constants.GSM_TABLE_NAME_).append(city).append(values).append(gsm_cid).append(dot_)
                    .append(name).append(_dot_).append(bs).append(_dot).append(gsm_lac).append(dot)
                    .append(gsm_bcch).append(dot).append(lat)
                    .append(dot).append(lon).append(dot).append(azimuth).append(dot).append(total_downtilt)
                        .append(dot).append(downtilt).append(dot).append(height).append(dot).append(type).append(dot)
                        .append(baiduLatLng.latitude).append(dot).append(baiduLatLng.longitude)
                        .append(dot).append("NULL").append(dot).append(0).append(dot).append(index).append(sqlend).toString();
                Logs.d(Tag,getGSMCellSQL);
                db.execSQL(getGSMCellSQL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void csvToDatebaseLTE(SQLiteDatabase db,String city,CsvReader r,int index) {
        StringBuilder sb = new StringBuilder();
        //读取表头
        try {
            r.readRecord();
            //读取一条记录
            String lte_ci = getString(r, Constants.LTE_CI);
            String name = getString(r, Constants.NAME);
            String bs = getString(r, Constants.BS);
            String lte_tac = getString(r, Constants.LTE_TAC);
            String lte_pci = getString(r, Constants.LTE_PCI);
            String lte_enb = getString(r, Constants.LTE_ENB);
            String lte_earfcn = getString(r, Constants.LTE_EARFCN);
            String lat = getString(r, Constants.LAT);
            String lon = getString(r, Constants.LON);
            String azimuth = getString(r, Constants.AZIMUTH);
            String total_downtilt = getString(r, Constants.TOTAL_DOWNTILT);
            String downtilt = getString(r, Constants.DOWNTILT);
            String height = getString(r, Constants.HEIGHT);
            String type = getString(r, Constants.TYPE);
            //   String baidu_latlng= g2bLatLngParser(lat, lon);
            converter.from(CoordinateConverter.CoordType.GPS);
// sourceLatLng待转换坐标
            converter.coord(new LatLng(Double.valueOf(lat),Double.valueOf(lon)));
            LatLng baiduLatLng = converter.convert();
            //按列名读取这条记录的值
            //(CI integer,NAME VARCHAR2(30),BS VARCHAR2(30),TAC integer,PCI integer,ENB integer,EARFCN integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),CELLINDEX integer);";
            String getLTECellSQL =  sb.append(inset).append(Constants.LTE_TABLE_NAME_).append(city).append(values).append(lte_ci).append(dot_)
                    .append(name).append(_dot_).append(bs).append(_dot).append(lte_tac).append(dot)
                    .append(lte_pci).append(dot).append(lte_enb).append(dot).append(lte_earfcn).append(dot).append(lat)
                    .append(dot).append(lon).append(dot).append(azimuth).append(dot).append(total_downtilt)
                    .append(dot).append(downtilt).append(dot).append(height).append(dot).append(type).append(dot)
                    .append(baiduLatLng.latitude).append(dot).append(baiduLatLng.longitude)
                    .append(dot).append("NULL").append(dot).append(index).append(sqlend).toString();
            Logs.d(Tag,getLTECellSQL);
            db.execSQL(getLTECellSQL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static String getString(CsvReader r ,String str) throws IOException {
        String rusult = r.get(str);
        if(TextUtils.isEmpty(rusult))
        {
            return "NULL";
        }
            return rusult;
    }


}
