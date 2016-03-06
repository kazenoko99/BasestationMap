package com.wenruisong.basestationmap.utils;

/**
 * Created by wen on 2016/1/17.
 */
public class Constants {

    public static final int DRAWER_MAP = 0;
    public static final int FRAG_BTS_SETTING =1;
    public static final int DRAWER_COMMON_ADDRESS = 2;
    public static final int DRAWER_OFFlINE_MAP = 3;
    public static final int DRAWER_TOOLS = 4;
    public static final int DRAWER_SETTING = 5;

    public static final String APPLICATION_PACKAGE_NAME = "com.wenruisong.basestationmap";
    public static final String ACTION_AR_NAV = "com.wenruisong.basestaionmap.arnavi";
    public static final String ACTION_AR_NAV_GROUP = "com.wenruisong.basestaionmap.argroup";
    public static final String ACTION_BUS_DETAIL = "com.wenruisong.basestaionmap.bus_detail";
    public static final String ACTION_MAP_VIEW = "com.wenruisong.basestaionmap.mapView";

    /**
     * **********************用于sharePrefrence*********************
     */
    public static final String MY_LOC_LAT = "my_loc_lat";
    public static final String MY_LOC_LON = "my_loc_lon";
    public static final String MY_BEARING = "my_bearing";
    public static final String MY_CITY_CODE = "my_city_code";
    public static final String KEEP_SCREEN_ON = "keep_screen_on";
    public static final String SHOW_ZOOM_BUTTON = "show_zoom_button";
    public static final String LOG_ON = "log_on";
    public static final String NEVER_SHOW_GPS_DIALOG = "never_show_gps_dialog";
    public static final String SHOW_CENTER_INDIC = "show_center_indic";//是否显示中间连线
    public static final String TRAFFIC_ENABLED = "traffic_enabled";

    public static final String DBNAME= "basestation_db";
    public static final String GSMTABLENAME= "gsm_cells";
    public static final String LTETABLENAME= "lte_cells";

    public static final String GSM_DB_READY = "gsm_db_ready";
    public static final String LTE_DB_READY = "lte_db_ready";

    public static final String NAME= "NAME";
    public static final String BS= "BS";
    public static final String LAT= "LAT";
    public static final String LON= "LON";
    public static final String AZIMUTH= "AZIMUTH";
    public static final String TOTAL_DOWNTILT= "TOTAL_DOWNTILT";
    public static final String DOWNTILT= "DOWNTILT";
    public static final String HEIGHT= "HEIGHT";
    public static final String TYPE= "TYPE";

    public static final String GSM_CID = "CID";
    public static final String GSM_LAC= "LAC";
    public static final String GSM_BCCH= "BCCH";
    public static final String GSM_ISFARAWAY= "ISFARAWAY";

    public static final String LTE_CI = "CI";
    public static final String LTE_TAC= "TAC";
    public static final String LTE_PCI= "PCI";
    public static final String LTE_ENB= "ENB";
    public static final String LTE_EARFCN= "EARFCN";

    //"create table gsm_cells(CID integer 0,NAME VARCHAR2(30),BS VARCHAR2(30)2,LAC integer,BCCH integer4,LAT double,LON double6,
    // AZIMUTH integer,TOTAL_DOWNTILT integer8,DOWNTILT integer,HEIGHT integer10,TYPE integer,BAIDULAT double12,
    // BAIDULON double,ADDRESS VARCHAR2(100))14;BSID integer15 ,INDEX Integer16";
    public static final String CREATE_DB_GSM = "create table gsm_cells(CID integer,NAME VARCHAR2(30),BS VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),BSID integer,CELLINDEX integer);";
    //CI	NAME	TAC	PCI	ENB	EARFCN	LAT	LON	AZIMUTH	TOTAL_DOWNTILT	DOWNTILT	HEIGHT	TYPE
    public static final String CREATE_DB_LTE= "create table lte_cells(CI integer,NAME VARCHAR2(30),BS VARCHAR2(30),TAC integer,PCI integer,ENB integer,EARFCN integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),CELLINDEX integer);";
    public static final String DROP_DB_GSM = "drop table gsm_cells;";
    public static final String DROP_DB_LTE= "drop table lte_cells;";




}
