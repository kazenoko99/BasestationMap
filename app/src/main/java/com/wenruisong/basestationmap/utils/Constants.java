package com.wenruisong.basestationmap.utils;

import com.amap.api.maps.model.LatLng;

/**
 * Created by wen on 2016/1/17.
 */
public class Constants {

    public static final String EMPTY_STRING = "";

    public static final int DRAWER_MAP = 0;
    public static final int FRAG_BTS_SETTING =1;
    public static final int DRAWER_COMMON_ADDRESS = 2;
    public static final int DRAWER_OFFlINE_MAP = 3;
    public static final int DRAWER_TOOLS = 4;
    public static final int DRAWER_GROUP = 5;
    public static final int DRAWER_SETTING = 6;

    public static final String APPLICATION_PACKAGE_NAME = "com.wenruisong.basestationmap";
    public static final String ACTION_AR_NAV = "com.wenruisong.basestaionmap.arnavi";
    public static final String ACTION_AR_NAV_GROUP = "com.wenruisong.basestaionmap.argroup";
    public static final String ACTION_BUS_DETAIL = "com.wenruisong.basestaionmap.bus_detail";
    public static final String ACTION_MAP_VIEW = "com.wenruisong.basestaionmap.mapView";
    public static final String ACTION_MAP_SEARCH_A_ADDRESS = "com.wenruisong.basestaionmap.search_a_address";
    public static final String ACTION_MAP_EDIT_A_ADDRESS = "com.wenruisong.basestaionmap.edit_a_address";

    public static final String ACTIVITY_PASS_SEARCH_CITY = "activity_pass_search_city";
    /**
     * **********************用于sharePrefrence*********************
     */
    public static final String MY_LOC_LAT = "my_loc_lat";
    public static final String MY_LOC_LON = "my_loc_lon";
    public static final String MY_BEARING = "my_bearing";
    public static final String MY_CITY_CODE = "my_city_code";
    public static final String KEEP_SCREEN_ON = "keep_screen_on";
    public static final String SHOW_CELL_NAME = "show_cell_name";
    public static final String SHOW_SIGNAL_NOTIFICATION = "show_signal_notfication";
    public static final String SHOW_ZOOM_BUTTON = "show_zoom_button";
    public static final String LOG_ON = "log_on";
    public static final String NEVER_SHOW_GPS_DIALOG = "never_show_gps_dialog";
    public static final String SHOW_CENTER_INDIC = "show_center_indic";//是否显示中间连线
    public static final String TRAFFIC_ENABLED = "traffic_enabled";



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

    public static final String DBNAME= "basestation_db";
    public static final String GSM_TABLE_NAME_= "gsm_cells_";
    public static final String LTE_TABLE_NAME_ = "lte_cells_";

    public static final String GSM_DB_READY = "gsm_db_ready";
    public static final String LTE_DB_READY = "lte_db_ready";
    //"create table gsm_cells(CID integer 0,NAME VARCHAR2(30),BS VARCHAR2(30)2,LAC integer,BCCH integer4,LAT double,LON double6,
    // AZIMUTH integer,TOTAL_DOWNTILT integer8,DOWNTILT integer,HEIGHT integer10,TYPE integer,BAIDULAT double12,
    // BAIDULON double,ADDRESS VARCHAR2(100))14;BSID integer15 ,INDEX Integer16";
    public static final String CREATE_DB_GSM = "create table gsm_cells_%s (CID integer,NAME VARCHAR2(30),BS VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),BSID integer,CELLINDEX integer);";
    //CI	NAME	TAC	PCI	ENB	EARFCN	LAT	LON	AZIMUTH	TOTAL_DOWNTILT	DOWNTILT	HEIGHT	TYPE
    public static final String CREATE_DB_LTE= "create table lte_cells_%s (CI integer,NAME VARCHAR2(30),BS VARCHAR2(30),TAC integer,PCI integer,ENB integer,EARFCN integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),CELLINDEX integer);";
    public static final String DROP_DB_GSM = "drop table gsm_cells_%s;";
    public static final String DROP_DB_LTE= "drop table lte_cells_%s ;";
    public static final String GET_NO_ADDRESS_GSM_CELLS= "select LAT,LON,BAIDULAT,BAIDULON,ADDRESS from gsm_cells_%s where ADDRESS is NULL group by LAT,LON UNION select LAT,LON,BAIDULAT,BAIDULON,ADDRESS from lte_cells_%s where ADDRESS is NULL group by LAT,LON";

    /**
     * **********************用于route*********************
     */


    public static final String LOCATION_CITY = "CITY";
    public static final String ROUTE_BUNDLE = "ROUTE";
    public static final String ROUTE_FROM_NAME = "FROM_NAME";
    public static final String ROUTE_FROM_LAT = "FROM_LAT";
    public static final String ROUTE_FROM_LNG= "FROM_LNG";

    public static final String ROUTE_TARGET_NAME = "TARGET_NAME";
    public static final String ROUTE_TARGET_LAT = "TARGET_LAT";
    public static final String ROUTE_TARGET_LNG= "TARGET_LNG";


    /**
     * **********************用于search********************
     */


    public static final String SEARCH_TYPE_CELL = "CELL";
    public static final String SEARCH_TYPE_POI = "POI";
    public static final String SEARCH_TYPE_BASESTATION = "BASESTATION";

    public static final int GSM = 2;
    public static final int LTE = 4;
    public static final int TYPE_UNKNOWN = 99;

    public final static int NormalSearch = 0;
    public final static int RoutePicPlace = 1;
    public final static int CommonPlacePic = 2;


    /******************************软件目录***********************************/
    // 软件主目录名
    public static final String MAP_MAIN_PATH_NAME = "basestationmap";
    // 离线地图下载子目录名
    public static final String MAP_OFFLINE_MAP_PATH_NAME = "offlinemap";
    /****************************离线地图下载**********************************/
    // 下载城市的大小的N倍，必须大于当手机剩余的存储空间 才被认为安全
    public static final int DOWNLOAD_SAFE_ALIVE_SIZE_SCALE = 3;
    // 下载省份时，省份大小加上该值必须大于当手机剩余的存储空间 才被认为安全
    public static final int DOWNLOAD_PROVINEC_SAFE_ALIVE_SIZE_MB = 100;

    /**
     * **********************用于bmob********************
     */


    public static final int NUMBERS_PER_PAGE = 10;


    /**
     * **********************用于收藏夹********************
     */

    public static final int COMMON_ADDRESS_SUM = 2;// 常用地址的数目
    public static final String COMMON_ADDRESS_TYPE_BS = "BASESTATION";
    public static final String COMMON_ADDRESS_TYPE_CELL = "CELL";
    public static final String COMMON_ADDRESS_TYPE_POI = "POI";
    public static final String COMMON_ADDRESS_TYPE_HOME = "HOME";
    public static final String COMMON_ADDRESS_TYPE_COMPANY = "COMPANY";


    /**
     * **********************用于地图选点********************
     */
    public static final int COMMON_ADDRESS_PICK_PLACE_CODE = 0x0010;
    public static final String COMMON_ADDRESS_PICK_PLACE = "COMMON_ADDRESS_PICK_PLACE";

    /**
     * **********************常用地址********************
     */
    public static final LatLng GEO_PINGXIANG = new LatLng(27.64096, 113.8677);

    public static final int PAGE_POI_NUMBER = 10; // 每一页的数目

    /**
     * **********************用于导航********************
     */
    public static String NAVI_BUNDLE = "NAVI_BUNDLE";
    public static String START_LAT = "START_LAT";
    public static String START_LNG = "START_LNG";
    public static String END_LAT = "END_LAT";
    public static String END_LNG = "END_LNG";

    /**
     * **********************用于工具箱********************
     */

    public final static int TOOL_RULER = 0;
    public final  static int TOOL_COMPASS = 1;
    public final  static int TOOL_LEVEL = 2;
    public final  static int TOOL_INCLINE = 3;
    public final  static int TOOL_ANGLE = 4;
    public final  static int TOOL_GPS_POINT = 5;
    public final  static int TOOL_CAMERA = 6;
    public final  static int TOOL_FAKE_BS = 7;
    public final  static int TOOL_SPEED_TEST = 8;

    private String[] toolsName={"地图标尺","指南针","水平仪","下倾角测量","地图量角","经纬度找点","网络勘察相机","伪基站捕获","网速测试"};

}
