package com.wenruisong.basestationmap.basestation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.database.CsvToSqliteHelper;
import com.wenruisong.basestationmap.map.poi.CellAddressCoderManager;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/16.
 */
public class BasestationManager {
    private static String Tag = "BasestationManager";
    public long numberOfGsmCells;
    public long numberOfLteCells;
    public static SQLiteDatabase basestationDB;

    private static BasestationManager instance;

    public static String getCurrentShowCity() {
        return currentShowCity;
    }

    public static void setCurrentShowCity(String currentShowCity) {
        BasestationManager.currentShowCity = currentShowCity;
        Settings.setMarkerCity(currentShowCity);
    }

    private static String currentShowCity;

    public static BasestationManager getInstance() {
        if (instance == null)
            instance = new BasestationManager();
        return instance;
    }

    public void initCity(){
//             gsmCells.clear();
//             gsmBSs.clear();
//             lteCells.clear();
//             lteBSs.clear();

        if (!Settings.isTableExsit(currentShowCity,"GSM")) {
            Logs.d(Tag, "GSM is Exsit");
            basestationDB.execSQL(String.format(Constants.CREATE_DB_GSM , currentShowCity));
            Settings.setTableExsit(currentShowCity,"GSM", true);
        }

        if (!Settings.isTableExsit(currentShowCity,"LTE")) {
            basestationDB.execSQL(String.format(Constants.CREATE_DB_LTE, currentShowCity));
            Settings.setTableExsit(currentShowCity,"LTE", true);
        }

//        if (Settings.isDatabaseReady(currentShowCity,"GSM")) {
//            initGsmCells(currentShowCity);
//        }
//        if (Settings.isDatabaseReady(currentShowCity,"LTE")) {
//            initLteCells(currentShowCity);
//        }
    }
    public BasestationManager() {
        CsvToSqliteHelper dbhelp = new CsvToSqliteHelper(BasestationMapApplication.getContext(), Constants.DBNAME, "GSM", 1);
        basestationDB = dbhelp.getReadableDatabase();
        currentShowCity = Settings.getMarkerCity();
        initCity();
        updateCellAddress(getNoAddressCells(currentShowCity));

    }


//    void initGsmCells(String city) {
//        Logs.d(Tag, "begin load GSM cells ");
//        gsmCells.clear();
//        Cursor cursor;
//        cursor = basestationDB.rawQuery("select * from "+Constants.GSM_TABLE_NAME_+city, null);
//        cursor.moveToFirst();
//        numberOfGsmCells = cursor.getCount();
////"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
//        for (int i = 0; i < numberOfGsmCells; i++) {
//            GSMCell gsmCell = db2GsmCell(cursor, i);
//            gsmCells.add(gsmCell);
//            GSMBasestation gsmBS = db2GsmBs(cursor, i);
//            gsmBSs.add(gsmBS);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        Logs.d(Tag, "GSM cells load ok!! size is" + numberOfGsmCells);
//    }
//
//    void initLteCells(String city) {
//        Logs.d(Tag, "begin load LTE cells ");
//        lteCells.clear();
//        Cursor cursor = basestationDB.rawQuery("select * from "+Constants.LTE_TABLE_NAME_+city, null);
//        cursor.moveToFirst();
//        numberOfLteCells = cursor.getCount();
////"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
//        for (int i = 0; i < numberOfLteCells; i++) {
//            LTECell lteCell = db2LteCell(cursor, i);
//            lteCells.add(lteCell);
//            LTEBasestation lteBS = db2LteBs(cursor, i);
//            lteBSs.add(lteBS);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        Logs.d(Tag, "LTE cells load ok!! size is" + numberOfLteCells);
//    }

  public static  Basestation getGsmBsbyIndex(int index){
        String sqlString = new String("select * from lte_cells_"+currentShowCity+" where CELLINDEX = " + index + ";");
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
      cursor.moveToFirst();
            GSMBasestation bs = db2GsmBs(cursor);

            cursor.close();
       return bs;
    }

    public static Basestation getLteBsbyIndex(int index){
        String sqlString = new String("select * from lte_cells_"+currentShowCity+" where CELLINDEX = " + index + ";");
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
        LTEBasestation bs = db2LteBs(cursor);
        cursor.close();
        return bs;
    }

    public static Cell getGsmCellbyIndex(int index){
        String sqlString = new String("select * from gsm_cells_"+currentShowCity +" where CELLINDEX = " + index + ";");
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
            GSMCell gsmCell = db2GsmCell(cursor );
        cursor.close();
        return gsmCell;
    }

    public static Cell getLteCellbyIndex(int index){
        String sqlString = new String("select * from lte_cells_"+currentShowCity +" where CELLINDEX = " + index + ";");
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
        LTECell lteCell = db2LteCell(cursor );
        cursor.close();
        return lteCell;
    }

    ArrayList getNoAddressCells(String city) {
        Logs.d(Tag, "getNoAddressGsmCells");
        ArrayList<Cell> cells = new ArrayList<>();
        Cursor cursor = basestationDB.rawQuery(String.format(Constants.GET_NO_ADDRESS_GSM_CELLS,city,city),null);
        cursor.moveToFirst();
        numberOfGsmCells = cursor.getCount();
        for (int i = 0; i < numberOfGsmCells; i++) {
            Cell cell = new Cell();
            cell.latLng = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            cell.aMapLatLng = new LatLng(cursor.getDouble(2), cursor.getDouble(3));
            if (cursor.getString(4) != null)
                cell.address = new String(cursor.getString(14));
            cells.add(cell);
            cursor.moveToNext();
        }
        cursor.close();
        Logs.d(Tag, "getNoAddressGsmCells ok!!" + cells.size());
        return cells;
    }

    public static void updateCellAddress(ArrayList<Cell> cells) {
        CellAddressCoderManager cellAddressCoderManager = CellAddressCoderManager.getInstance();
        for (Cell cell : cells) {
            if (cell.aMapLatLng != null)
                CellAddressCoderManager.addPoint(cell);
        }
        cellAddressCoderManager.start();
    }

    public static synchronized void  updateDatebaseAddress(LatLng point, String addrress) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE gsm_cells_"+ currentShowCity +" SET ADDRESS ='")
                .append(addrress).append("' where LAT =")
                .append(point.latitude).append(" AND ").append("LON=").append(point.longitude);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("UPDATE lte_cells_"+ currentShowCity + " SET ADDRESS ='")
                .append(addrress).append("' where LAT =")
                .append(point.latitude).append(" AND ").append("LON=").append(point.longitude);

        basestationDB.execSQL(sb.toString());
        basestationDB.execSQL(sb2.toString());
    }


    public static ArrayList<Cell> getCellsFromBS(Cell cell) {
        ArrayList cells = new ArrayList<>();
        String sqlString;
        if (cell instanceof GSMCell) {
            sqlString = new String("select * from gsm_cells_"+currentShowCity +" where BS = '" + cell.bsName + "';");
            Cursor cursor = basestationDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                GSMCell gsmCell = db2GsmCell(cursor);
                cells.add(gsmCell);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            sqlString = new String("select * from lte_cells_"+currentShowCity+" where BS = '" + cell.bsName + "';");
            Cursor cursor = basestationDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                LTECell lteCell = db2LteCell(cursor );
                cells.add(lteCell);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return cells;
    }


    public static ArrayList<Cell> searchCellsByName(String cellName) {
        String sqlString = new String("select * from lte_cells_"+ currentShowCity + " where NAME like \"%" + cellName + "%\"");
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag, "get lte search count" + count);
        for (int i = 0; i < count; i++) {
            LTECell lteCell = db2LteCell(cursor );
            cells.add(lteCell);
            cursor.moveToNext();
        }
        cursor.close();

        sqlString = new String("select * from gsm_cells_"+ currentShowCity + " where NAME like \"%" + cellName + "%\"");
        Logs.d(Tag, sqlString);
        cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
        count = cursor.getCount();
        Logs.d(Tag, "get gsm search count" + count);
        for (int i = 0; i < count; i++) {
            GSMCell gsmCell = db2GsmCell(cursor );
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();

        return cells;
    }

    public static ArrayList<Basestation> searchBsByName(String bsName) {

        String sqlString = new String("select * from lte_cells_"+currentShowCity+ " where NAME like \"%" + bsName + "%\"" + " group by BS");
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList bses = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            LTEBasestation bs = db2LteBs(cursor );
            bses.add(bs);
            cursor.moveToNext();
        }

         sqlString = new String("select * from gsm_cells_"+currentShowCity+ " where NAME like \"%" + bsName + "%\"" + " group by BS");
        Logs.d(Tag, sqlString);
         cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
         count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            GSMBasestation bs = db2GsmBs(cursor );
            bses.add(bs);
            cursor.moveToNext();
        }
        cursor.close();
        return bses;
    }

    public static GSMCell db2GsmCell(Cursor cursor) {
        GSMCell gsmCell = new GSMCell();
        gsmCell.cellName = new String(cursor.getString(1));
        gsmCell.bsName = new String(cursor.getString(2));
        gsmCell.latLng = new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        gsmCell.aMapLatLng = new LatLng(cursor.getDouble(12), cursor.getDouble(13));
        gsmCell.azimuth = 360-cursor.getInt(7);
        gsmCell.cellid = cursor.getInt(0);
        gsmCell.index = cursor.getInt(16);
        gsmCell.highth = cursor.getInt(10);
        gsmCell.downtilt = cursor.getInt(9);
        gsmCell.type = cursor.getInt(11);
        gsmCell.lac = cursor.getInt(3);
        gsmCell.bcch = cursor.getInt(4);
        if (cursor.getString(14) != null)
            gsmCell.address = new String(cursor.getString(14));

        return gsmCell;
    }

    public static LTECell db2LteCell(Cursor cursor ) {
        LTECell lteCell = new LTECell();
        lteCell.cellName = new String(cursor.getString(1));
        lteCell.bsName = new String(cursor.getString(2));
        lteCell.latLng = new LatLng(cursor.getDouble(7), cursor.getDouble(8));
        lteCell.aMapLatLng = new LatLng(cursor.getDouble(14), cursor.getDouble(15));
        lteCell.azimuth = 360-cursor.getInt(9);
        lteCell.cellid = cursor.getInt(0);
        lteCell.index = cursor.getInt(17);
        lteCell.highth = cursor.getInt(12);
        lteCell.downtilt = cursor.getInt(11);
        lteCell.type = cursor.getInt(13);
        lteCell.tac = cursor.getInt(3);
        lteCell.pci = cursor.getInt(4);
        lteCell.enb = cursor.getInt(5);
        lteCell.earfcn = cursor.getInt(6);
        if (cursor.getString(16) != null)
            lteCell.address = new String(cursor.getString(16));

        return lteCell;
    }

    public static LTEBasestation db2LteBs(Cursor cursor ) {
        LTEBasestation bs = new LTEBasestation();
        bs.bsName = new String(cursor.getString(2));
        bs.basestationIndex = cursor.getInt(17);
        bs.latLng = new LatLng(cursor.getDouble(7), cursor.getDouble(8));
        bs.amapLatLng = new LatLng(cursor.getDouble(14), cursor.getDouble(15));
        bs.type = cursor.getInt(13);
        if (cursor.getString(16) != null)
            bs.address = new String(cursor.getString(16));
        return bs;
    }


    public static GSMBasestation db2GsmBs(Cursor cursor ) {
        GSMBasestation bs = new GSMBasestation();
        bs.bsName = new String(cursor.getString(2));
        bs.basestationIndex = cursor.getInt(16);
        bs.latLng = new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        bs.amapLatLng = new LatLng(cursor.getDouble(12), cursor.getDouble(13));
        bs.type = cursor.getInt(11);
        if (cursor.getString(14) != null)
            bs.address = new String(cursor.getString(14));
        return bs;
    }


    public static ArrayList<Cell> searchNearbyCells(LatLng latLng) {
        String minlat = Double.toString(latLng.latitude-0.002);
        String maxlat = Double.toString(latLng.latitude+0.002);
        String minlng = Double.toString(latLng.longitude-0.002);
        String maxlng = Double.toString(latLng.longitude+0.002);
        String sqlString = new String("select * from lte_cells_"+ currentShowCity + " where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? ");
        Cursor cursor = basestationDB.rawQuery(sqlString, new String[]{minlat, maxlat, minlng, maxlng});
        ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag, "get lte search count" + count);
        for (int i = 0; i < count; i++) {
            LTECell lteCell = db2LteCell(cursor );
            cells.add(lteCell);
            cursor.moveToNext();
        }
        cursor.close();

        sqlString = new String("select * from gsm_cells_"+currentShowCity+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? ");
        Logs.d(Tag, sqlString);
        cursor = basestationDB.rawQuery(sqlString, new String[]{minlat, maxlat, minlng, maxlng});
        cursor.moveToFirst();
        count = cursor.getCount();
        Logs.d(Tag, "get gsm search count" + count);
        for (int i = 0; i < count; i++) {
            GSMCell gsmCell = db2GsmCell(cursor);
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();

        return cells;
    }


    public static ArrayList<Basestation> searchNearbyBs(LatLng latLng) {
        String minlat = Double.toString(latLng.latitude-0.002);
        String maxlat = Double.toString(latLng.latitude+0.002);
        String minlng = Double.toString(latLng.longitude-0.002);
        String maxlng = Double.toString(latLng.longitude+0.002);
        String sqlString = new String("select * from lte_cells_"+currentShowCity+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS");
        Cursor cursor = basestationDB.rawQuery(sqlString, new String[]{minlat, maxlat, minlng, maxlng});
        ArrayList bses = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag, "get lte search count" + count);
        for (int i = 0; i < count; i++) {
            LTEBasestation bs = db2LteBs(cursor );
            bses.add(bs);
            cursor.moveToNext();
        }
        cursor.close();

        sqlString = new String("select * from gsm_cells_"+currentShowCity + " where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS");
        Logs.d(Tag, sqlString);
        cursor = basestationDB.rawQuery(sqlString, new String[]{minlat, maxlat, minlng, maxlng});
        cursor.moveToFirst();
        count = cursor.getCount();
        Logs.d(Tag, "get gsm search count" + count);
        for (int i = 0; i < count; i++) {
            GSMBasestation bs = db2GsmBs(cursor );
            bses.add(bs);
            cursor.moveToNext();
        }
        cursor.close();

        return bses;
    }


    public static ArrayList<Cell> searchGSMCellsByCity(String city) {
        String sqlString = new String("select * from gsm_cells_"+ city);
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            GSMCell gsmCell = db2GsmCell(cursor );
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();

        return cells;
    }


    public static ArrayList<Cell> searchLTECellsByCity(String city) {
        String sqlString = new String("select * from lte_cells_"+ city);
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();

        for (int i = 0; i < count; i++) {
            LTECell lteCell = db2LteCell(cursor );
            cells.add(lteCell);
            cursor.moveToNext();
        }
        cursor.close();

        return cells;
    }

}
