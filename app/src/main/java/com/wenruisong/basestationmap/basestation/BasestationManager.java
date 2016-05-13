package com.wenruisong.basestationmap.basestation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.database.CsvToSqliteHelper;
import com.wenruisong.basestationmap.map.poi.PoiCoderManager;
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
    public static ArrayList<GSMCell> gsmCells = new ArrayList<>();
    public static ArrayList<GSMBasestation> gsmBSs = new ArrayList<>();

    public static ArrayList<LTECell> lteCells = new ArrayList<>();
    public static ArrayList<LTEBasestation> lteBSs = new ArrayList<>();

    private static BasestationManager instance;

    public static BasestationManager getInstance() {
        if (instance == null)
            instance = new BasestationManager();
        return instance;
    }

    public BasestationManager() {
        CsvToSqliteHelper dbhelp = new CsvToSqliteHelper(BasestationMapApplication.getContext(), Constants.DBNAME, Cell.CellType.GSM, 1);
        basestationDB = dbhelp.getReadableDatabase();
        if (!Settings.isTableExsit("GSM")) {
            Logs.d(Tag, "GSM is Exsit");
            basestationDB.execSQL(Constants.CREATE_DB_GSM);
            Settings.setTableExsit("GSM", true);
        }

        if (!Settings.isTableExsit("LTE")) {
            basestationDB.execSQL(Constants.CREATE_DB_LTE);
            Settings.setTableExsit("LTE", true);
        }

        if (Settings.isDatabaseReady("GSM")) {
            initGsmCells();
        }
        if (Settings.isDatabaseReady("LTE")) {
            initLteCells();
        }

        updateCellAddress(getNoAddressCells());

    }


    void initGsmCells() {
        Logs.d(Tag, "begin load GSM cells ");
        gsmCells.clear();
        Cursor cursor;
        cursor = basestationDB.rawQuery("select * from gsm_cells", null);
        cursor.moveToFirst();
        numberOfGsmCells = cursor.getCount();
//"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
        for (int i = 0; i < numberOfGsmCells; i++) {
            GSMCell gsmCell = db2GsmCell(cursor, i);
            gsmCells.add(gsmCell);
            GSMBasestation gsmBS = db2GsmBs(cursor, i);
            gsmBSs.add(gsmBS);
            cursor.moveToNext();
        }
        cursor.close();
        Logs.d(Tag, "GSM cells load ok!! size is" + numberOfGsmCells);
    }

    void initLteCells() {
        Logs.d(Tag, "begin load LTE cells ");
        lteCells.clear();
        Cursor cursor = basestationDB.rawQuery("select * from lte_cells", null);
        cursor.moveToFirst();
        numberOfLteCells = cursor.getCount();
//"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
        for (int i = 0; i < numberOfLteCells; i++) {
            LTECell lteCell = db2LteCell(cursor, i);
            lteCells.add(lteCell);
            LTEBasestation lteBS = db2LteBs(cursor, i);
            lteBSs.add(lteBS);
            cursor.moveToNext();
        }
        cursor.close();
        Logs.d(Tag, "LTE cells load ok!! size is" + numberOfLteCells);
    }

    ArrayList getNoAddressCells() {
        Logs.d(Tag, "getNoAddressGsmCells");
        ArrayList<Cell> cells = new ArrayList<>();
        Cursor cursor = basestationDB.rawQuery("select LAT,LON,BAIDULAT,BAIDULON,ADDRESS from gsm_cells where ADDRESS is NULL group by LAT,LON UNION select LAT,LON,BAIDULAT,BAIDULON,ADDRESS from lte_cells where ADDRESS is NULL group by LAT,LON", null);
        cursor.moveToFirst();
        numberOfGsmCells = cursor.getCount();
        for (int i = 0; i < numberOfGsmCells; i++) {
            Cell cell = new Cell();
            cell.latLng = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            cell.baiduLatLng = new LatLng(cursor.getDouble(2), cursor.getDouble(3));
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
        PoiCoderManager poiCoderManager = PoiCoderManager.getInstance();
        for (Cell cell : cells) {
            if (cell.baiduLatLng != null)
                PoiCoderManager.addPoint(cell);
        }
        poiCoderManager.start();
    }


    //UPDATE Person SET FirstName = 'Fred' WHERE LastName = 'Wilson'
    //isAddressGet integer,ADDRESS VARCHAR2(100)
    public static synchronized void updateDatebaseAddress(LatLng point, String addrress) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE gsm_cells SET ADDRESS ='")
                .append(addrress).append("' where LAT =")
                .append(point.latitude).append(" AND ").append("LON=").append(point.longitude);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("UPDATE lte_cells SET ADDRESS ='")
                .append(addrress).append("' where LAT =")
                .append(point.latitude).append(" AND ").append("LON=").append(point.longitude);

        basestationDB.execSQL(sb.toString());
        basestationDB.execSQL(sb2.toString());
    }


    public static ArrayList<Cell> getCellsFromBS(Cell cell) {
        ArrayList cells = new ArrayList<>();
        String sqlString;
        if (cell instanceof GSMCell) {
            sqlString = new String("select * from gsm_cells where BS = '" + cell.bsName + "';");
            Cursor cursor = basestationDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                GSMCell gsmCell = db2GsmCell(cursor, i);
                cells.add(gsmCell);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            sqlString = new String("select * from lte_cells where BS = '" + cell.bsName + "';");
            Cursor cursor = basestationDB.rawQuery(sqlString, null);
            cursor.moveToFirst();
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                LTECell lteCell = db2LteCell(cursor, i);
                cells.add(lteCell);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return cells;
    }


    public static ArrayList<Cell> searchCellsByName(String cellName) {
        String sqlString = new String("select * from lte_cells where NAME like \"%" + cellName + "%\"");
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag, "get lte search count" + count);
        for (int i = 0; i < count; i++) {
            LTECell lteCell = db2LteCell(cursor, i);
            cells.add(lteCell);
            cursor.moveToNext();
        }
        cursor.close();

        sqlString = new String("select * from gsm_cells where NAME like \"%" + cellName + "%\"");
        Logs.d(Tag, sqlString);
        cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
        count = cursor.getCount();
        Logs.d(Tag, "get gsm search count" + count);
        for (int i = 0; i < count; i++) {
            GSMCell gsmCell = db2GsmCell(cursor, i);
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();

        return cells;
    }

    public static ArrayList<Basestation> searchBsByName(String bsName) {

        String sqlString = new String("select * from lte_cells where NAME like \"%" + bsName + "%\"" + " group by BS");
        Logs.d(Tag, sqlString);
        Cursor cursor = basestationDB.rawQuery(sqlString, null);
        ArrayList bses = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            LTEBasestation bs = db2LteBs(cursor, i);
            bses.add(bs);
            cursor.moveToNext();
        }

         sqlString = new String("select * from gsm_cells where NAME like \"%" + bsName + "%\"" + " group by BS");
        Logs.d(Tag, sqlString);
         cursor = basestationDB.rawQuery(sqlString, null);
        cursor.moveToFirst();
         count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            GSMBasestation bs = db2GsmBs(cursor, i);
            bses.add(bs);
            cursor.moveToNext();
        }
        cursor.close();
        return bses;
    }

    public static GSMCell db2GsmCell(Cursor cursor, int index) {
        GSMCell gsmCell = new GSMCell();
        gsmCell.cellName = new String(cursor.getString(1));
        gsmCell.bsName = new String(cursor.getString(2));
        gsmCell.latLng = new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        gsmCell.baiduLatLng = new LatLng(cursor.getDouble(12), cursor.getDouble(13));
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

    public static LTECell db2LteCell(Cursor cursor, int index) {
        LTECell lteCell = new LTECell();
        lteCell.cellName = new String(cursor.getString(1));
        lteCell.bsName = new String(cursor.getString(2));
        lteCell.latLng = new LatLng(cursor.getDouble(7), cursor.getDouble(8));
        lteCell.baiduLatLng = new LatLng(cursor.getDouble(14), cursor.getDouble(15));
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

    public static LTEBasestation db2LteBs(Cursor cursor, int index) {
        LTEBasestation bs = new LTEBasestation();
        bs.bsName = new String(cursor.getString(2));
        bs.basestationIndex = cursor.getInt(17);
        bs.latLng = new LatLng(cursor.getDouble(7), cursor.getDouble(8));
        bs.baiduLatLng = new LatLng(cursor.getDouble(14), cursor.getDouble(15));
        bs.type = cursor.getInt(13);
        if (cursor.getString(16) != null)
            bs.address = new String(cursor.getString(16));
        return bs;
    }


    public static GSMBasestation db2GsmBs(Cursor cursor, int index) {
        GSMBasestation bs = new GSMBasestation();
        bs.bsName = new String(cursor.getString(2));
        bs.basestationIndex = cursor.getInt(16);
        bs.latLng = new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        bs.baiduLatLng = new LatLng(cursor.getDouble(12), cursor.getDouble(13));
        bs.type = cursor.getInt(11);
        if (cursor.getString(14) != null)
            bs.address = new String(cursor.getString(14));
        return bs;
    }


}
