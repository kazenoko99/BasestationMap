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
            + " SearchTable(_id integer primary key autoincrement, keyword text,"
            + " searchtype text，address text,time text, lat double, lng double,)";

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


    public void insertSearchResult(ContentValues values)
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            db.insert(SEARCH_TABLE_NAME, null, values);
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void insertRouteResult(ContentValues values)
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
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
                searchHistoryItems.add(searchHistoryItem);
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
                routeHistoryItem.mStartName = cursor.getString(cursor.getColumnIndex("keyword"));
                routeHistoryItem.mEndName = cursor.getString(cursor.getColumnIndex("searchtype"));
                routeHistoryItem.id = cursor.getString(cursor.getColumnIndex("_id"));
                double lat = cursor.getDouble(cursor.getColumnIndex("startlat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("startlng"));
                routeHistoryItem.mStartLatLng = new LatLng(lat,lng);
                  lat = cursor.getDouble(cursor.getColumnIndex("endlat"));
                  lng = cursor.getDouble(cursor.getColumnIndex("endlng"));
                routeHistoryItem.mEndLatLng = new LatLng(lat,lng);
                routeHistoryItems.add(routeHistoryItem);
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
