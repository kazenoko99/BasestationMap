package com.wenruisong.basestationmap.basestation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.map.MapStatus;
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
    public static SQLiteDatabase basestationDB;
    public static ArrayList<GSMCell> gsmCells= new ArrayList<>();
    public static ArrayList<Basestation> gsmBSs= new ArrayList<>();
    private static BasestationManager instance;
    public static  BasestationManager getInstance()
    {
        if (instance==null)
            instance=new BasestationManager();
        return instance;
    }
    public BasestationManager()
    {
        CsvToSqliteHelper dbhelp=new CsvToSqliteHelper(BasestationMapApplication.getContext(), Constants.DBNAME, Cell.CellType.GSM,1);
        basestationDB= dbhelp.getReadableDatabase();

        if(Settings.isDatabaseReady("GSM")) {
            initGsmCells();
            updateCellAddress(getNoAddressGsmCells());
        }

    }



    void initGsmCells() {
        Logs.d(Tag, "begin load GSM cells ");
        gsmCells.clear();
        Cursor cursor;
        cursor= basestationDB.rawQuery("select * from gsm_cells",null);
        cursor.moveToFirst();
        numberOfGsmCells=cursor.getCount();
//"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
        for ( int i= 0; i<numberOfGsmCells; i++ )
        {
            GSMCell gsmCell = db2GsmCell(cursor, i);
            gsmCells.add(gsmCell);
            Basestation gsmBS = new Basestation();
            gsmBS.bsName = new String(cursor.getString(2));
            gsmBS.cellIndex = cursor.getInt(16);
            gsmBS.latLng=new LatLng(cursor.getDouble(5), cursor.getDouble(6));
            gsmBS.baiduLatLng=new LatLng(cursor.getDouble(12), cursor.getDouble(13));
            gsmBSs.add(gsmBS);
            cursor.moveToNext();
        }
        cursor.close();
        Logs.d(Tag, "GSM cells load ok!!");
    }


    ArrayList getNoAddressGsmCells() {
        Logs.d(Tag, "getNoAddressGsmCells");
        ArrayList<Cell> cells = new ArrayList<>();
        Cursor cursor = basestationDB.rawQuery("select * from gsm_cells where ADDRESS is NULL group by LAT,LON",null);
        cursor.moveToFirst();
        numberOfGsmCells=cursor.getCount();
//"create table gsm_cells(CID integer,NAME VARCHAR2(30)1,BS VARCHAR2(30) 2,LAC integer3,BCCH integer4,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer11,BAIDULAT double12,BAIDULON double13);";
        for ( int i= 0; i<numberOfGsmCells; i++ )
        {
            GSMCell gsmCell = db2GsmCell(cursor, i);
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();
        Logs.d(Tag, "getNoAddressGsmCells ok!!" + cells.size());
        return cells;
    }

    public static void updateCellAddress(ArrayList<GSMCell> cells)
    {
        PoiCoderManager poiCoderManager = PoiCoderManager.getInstance();
        for (GSMCell cell: cells) {
            if(cell.baiduLatLng!=null)
                poiCoderManager.addPoint(cell);
        }
        poiCoderManager.start();
    }


    //UPDATE Person SET FirstName = 'Fred' WHERE LastName = 'Wilson'
    //isAddressGet integer,ADDRESS VARCHAR2(100)
    public static synchronized void updateDatebaseAddress(LatLng point,String addrress)
    {
        StringBuilder sb=new StringBuilder();
        sb.append("UPDATE gsm_cells SET ADDRESS ='")
                .append(addrress).append("' where LAT =")
                .append(point.latitude).append(" AND ").append("LON=").append(point.longitude);
        String SqlString = sb.toString();
        Logs.d(Tag,SqlString);
        basestationDB.execSQL(SqlString);
    }


    public static ArrayList<GSMCell> getCellsFromBS(String bsName)
    {
        ArrayList cells = new ArrayList<>();
       String sqlString = new String( "select * from gsm_cells where BS = '"+bsName+"';");
        Cursor cursor= basestationDB.rawQuery(sqlString,null);
        cursor.moveToFirst();
        int count = cursor.getCount();
//"create table gsm_cells(CID integer,NAME VARCHAR2(30),BS VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON  double6,AZIMUTH integer7,TOTAL_DOWNTILT integer8,DOWNTILT integer9,HEIGHT integer10,TYPE integer,BAIDULAT double,BAIDULON double);";
        for ( int i= 0; i<count; i++ )
        {
            GSMCell gsmCell = db2GsmCell(cursor,i);
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();
        return cells;
    }


    public static ArrayList<Cell> searchCellsByName(String cellName)
    {
        String sqlString = new String("select * from gsm_cells where NAME like \"%"+cellName+"%\"");
        Logs.d(Tag, sqlString);
        Cursor cursor= basestationDB.rawQuery(sqlString, null);
         ArrayList cells = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag,"get search count"+count);
        for ( int i= 0; i<count; i++ )
        {
            GSMCell gsmCell = db2GsmCell(cursor,i);
            cells.add(gsmCell);
            cursor.moveToNext();
        }
        cursor.close();
        return cells;
    }

    public static ArrayList<Basestation> searchBsByName(String bsName)
    {
        String sqlString = new String("select * from gsm_cells where NAME like \"%"+bsName+"%\""+" group by BS");
        Logs.d(Tag, sqlString);
        Cursor cursor= basestationDB.rawQuery(sqlString, null);
        ArrayList bses = new ArrayList();
        cursor.moveToFirst();
        int count = cursor.getCount();
        Logs.d(Tag,"get search count"+count);
        for ( int i= 0; i<count; i++ )
        {
            Basestation bs = db2GsmBs(cursor, i);
            bses.add(bs);
            cursor.moveToNext();
        }
        cursor.close();
        return bses;
    }

    public static GSMCell db2GsmCell(Cursor cursor,int index)
    {
        GSMCell gsmCell = new GSMCell();
        gsmCell.cellName=new String(cursor.getString(1));
        gsmCell.bsName=new String(cursor.getString(2));
        gsmCell.latLng=new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        gsmCell.baiduLatLng=new LatLng(cursor.getDouble(12), cursor.getDouble(13));
        gsmCell.azimuth = cursor.getInt(7);
        gsmCell.cellid = cursor.getInt(0);
        gsmCell.index = cursor.getInt(16);
        gsmCell.highth = cursor.getInt(10);
        gsmCell.downtilt = cursor.getInt(9);
        gsmCell.type = cursor.getInt(11);
        gsmCell.lac = cursor.getInt(3);
        gsmCell.bcch = cursor.getInt(4);
        if(cursor.getString(14)!=null)
        gsmCell.address =new String(cursor.getString(14));

        return gsmCell;
    }

    public static Basestation db2GsmBs(Cursor cursor,int index)
    {
        Basestation bs = new Basestation();
        bs.bsName=new String(cursor.getString(2));
        bs.cellIndex = cursor.getInt(16);
        bs.latLng=new LatLng(cursor.getDouble(5), cursor.getDouble(6));
        bs.baiduLatLng=new LatLng(cursor.getDouble(12), cursor.getDouble(13));
        bs.type = cursor.getInt(11);
        if(cursor.getString(14)!=null)
            bs.address =new String(cursor.getString(14));
        return bs;
    }


}
