package com.wenruisong.basestationmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.model.RouteHistoryItem;
import com.wenruisong.basestationmap.model.SearchHistoryItem;

import java.util.ArrayList;

/**
 * Created by wen on 2016/5/6.
 */
public class SearchHistorySqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "SearchResult.db";
    private static final String SEARCH_TABLE_NAME = "SearchTable";
    private static final String ROUTE_TABLE_NAME = "RouteTable";
    private static final String CREATE_SEARCH_TABLE= " create table "
            + " SearchTable(_id integer primary key autoincrement, keyword text,cellindex integer,nettype text,"
            + " searchtype text, address VARCHAR2(50),time text, lat double, lng double)";

    private static final String CREATE_ROUTE_TABLE= " create table "
            + " RouteTable(_id integer primary key autoincrement, startname text,"
            + " endname text, startlat double, startlng double,endlat double,endlng double,time text)";

    private SQLiteDatabase db;

    public SearchHistorySqliteHelper(Context c)
    {
        super(c, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;
        db.execSQL(CREATE_SEARCH_TABLE);
        db.execSQL(CREATE_ROUTE_TABLE);
    }


    public void insertSearchResult(SearchHistoryItem searchHistoryItem)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+SEARCH_TABLE_NAME+ " where keyword = '"
                + searchHistoryItem.keyword + "';", null);
        if(cursor.getCount()>0){
            return;
        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("keyword",searchHistoryItem.keyword);
            values.put("address",  searchHistoryItem.address);
            values.put("lat", searchHistoryItem.latLng.latitude );
            values.put("lng", searchHistoryItem.latLng.longitude );
            values.put("nettype", searchHistoryItem.nettype);
            values.put("cellindex", searchHistoryItem.cellindex );
            values.put("searchtype", searchHistoryItem.searchtype );
            values.put("time",     searchHistoryItem.time );
            db.insert(SEARCH_TABLE_NAME, null, values);
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void insertRouteResult(RouteHistoryItem routeHistoryItem)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ROUTE_TABLE_NAME+ " where startname = '"
                + routeHistoryItem.mStartName + "'and endname = '"+ routeHistoryItem.mStartName+"';", null);
       if(cursor.getCount()>0){
           return;
       }

        try
        {
            ContentValues values = new ContentValues();
            values.put("startname",routeHistoryItem.mStartName);
            values.put("endname",  routeHistoryItem.mEndName);
            values.put("startlat", routeHistoryItem.mStartLatLng.latitude );
            values.put("startlng", routeHistoryItem.mStartLatLng.longitude );
            values.put("endlat",   routeHistoryItem.mEndLatLng.latitude);
            values.put("endlng",   routeHistoryItem.mEndLatLng.longitude );
            values.put("time",     routeHistoryItem.time );
            db.insert(ROUTE_TABLE_NAME, null, values);
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public ArrayList<SearchHistoryItem> querySearchResult()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+SEARCH_TABLE_NAME+ ";", null);
        ArrayList<SearchHistoryItem> searchHistoryItems = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            for(int i=0; i<cursor.getCount();i++) {

                SearchHistoryItem searchHistoryItem = new SearchHistoryItem();
                searchHistoryItem.keyword = cursor.getString(cursor.getColumnIndex("keyword"));
                searchHistoryItem.searchtype = cursor.getString(cursor.getColumnIndex("searchtype"));
                searchHistoryItem.id = cursor.getString(cursor.getColumnIndex("_id"));
                searchHistoryItem.address  = cursor.getString(cursor.getColumnIndex("address"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                searchHistoryItem.latLng = new LatLng(lat,lng);
                searchHistoryItem.cellindex = cursor.getInt(cursor.getColumnIndex("cellindex"));
                searchHistoryItem.nettype  = cursor.getInt(cursor.getColumnIndex("nettype"));
                searchHistoryItems.add(searchHistoryItem);
                cursor.moveToNext();
            }
            return searchHistoryItems;
        }
        else return null;
    }


    public ArrayList<RouteHistoryItem> queryRouteResult()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+ ROUTE_TABLE_NAME +";", null);
        ArrayList<RouteHistoryItem> routeHistoryItems = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            for(int i=0; i<cursor.getCount();i++) {
                RouteHistoryItem routeHistoryItem = new RouteHistoryItem();
                routeHistoryItem.mStartName = cursor.getString(cursor.getColumnIndex("startname"));
                routeHistoryItem.mEndName = cursor.getString(cursor.getColumnIndex("endname"));
                routeHistoryItem.id = cursor.getString(cursor.getColumnIndex("_id"));
                routeHistoryItem.time  = cursor.getString(cursor.getColumnIndex("time"));
                double startLat = cursor.getDouble(cursor.getColumnIndex("startlat"));
                double startLng = cursor.getDouble(cursor.getColumnIndex("startlng"));
                double endLat = cursor.getDouble(cursor.getColumnIndex("endlat"));
                double endLng = cursor.getDouble(cursor.getColumnIndex("endlng"));
                routeHistoryItem.mStartLatLng = new LatLng(startLat,startLng);
                routeHistoryItem.mEndLatLng = new LatLng(endLat,endLng);
                routeHistoryItems.add(routeHistoryItem);
                cursor.moveToNext();
            }

            return routeHistoryItems;
        }
        else return null;

    }


    public void delRouteResult(String id)
    {
        if (db == null) db = getWritableDatabase();
        db.delete(ROUTE_TABLE_NAME, "_id=?", new String[] { id });
    }


    public void delSearchResult(String id)
    {
        if (db == null) db = getWritableDatabase();
        db.delete(SEARCH_TABLE_NAME, "_id=?", new String[] { id });
    }


    public void close()
    {
        if (db != null) db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
