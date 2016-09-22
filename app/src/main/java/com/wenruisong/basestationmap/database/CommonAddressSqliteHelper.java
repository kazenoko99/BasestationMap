package com.wenruisong.basestationmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.model.CommonAddress;

import java.util.ArrayList;

/**
 * Created by wen on 2016/5/6.
 */
public class CommonAddressSqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "CommonAddress.db";
    private static final String COMMON_ADDRESS_TABLE_NAME = "CommonAddressTable";
    private static final String CREATE_COMMON_ADDRESS_TABLE= " create table "
            + " CommonAddressTable(_id integer primary key autoincrement, name text,cellindex integer,nettype text,"
            + " addresstype text, address VARCHAR2(50),time text, lat double, lng double ,gpslat double,gpslng double)";

    private SQLiteDatabase db;

    public CommonAddressSqliteHelper(Context c)
    {
        super(c, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;
        db.execSQL(CREATE_COMMON_ADDRESS_TABLE);
    }


    public void insertCommonAddress(CommonAddress commonAddress)
    {
        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = db.rawQuery("select * from "+COMMON_ADDRESS_TABLE_NAME+ " where name = '"
//                + commonAddress.name + "';", null);
//        if(cursor.getCount()>0){
//            return;
//        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("name",commonAddress.name);
            values.put("address",  commonAddress.address);
            values.put("lat", commonAddress.latLng.latitude );
            values.put("lng", commonAddress.latLng.longitude );
            if( commonAddress.gpsLatLng !=null) {
                values.put("gpslat", commonAddress.gpsLatLng.latitude);
                values.put("gpslng", commonAddress.gpsLatLng.longitude);
            }
            values.put("nettype", commonAddress.nettype);
            values.put("cellindex", commonAddress.cellindex );
            values.put("addresstype", commonAddress.addressType );
            values.put("time",     commonAddress.time );
            db.insert(COMMON_ADDRESS_TABLE_NAME, null, values);
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public ArrayList<CommonAddress> queryCommonAddress()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+COMMON_ADDRESS_TABLE_NAME+ ";", null);
        ArrayList<CommonAddress> commonAddresses = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            for(int i=0; i<cursor.getCount();i++) {

                CommonAddress commonAddress = new CommonAddress();
                commonAddress.name = cursor.getString(cursor.getColumnIndex("name"));
                commonAddress.addressType = cursor.getString(cursor.getColumnIndex("addresstype"));
                commonAddress.id = cursor.getString(cursor.getColumnIndex("_id"));
                commonAddress.address  = cursor.getString(cursor.getColumnIndex("address"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                commonAddress.latLng = new LatLng(lat,lng);

                 lat = cursor.getDouble(cursor.getColumnIndex("gpslat"));
                 lng = cursor.getDouble(cursor.getColumnIndex("gpslng"));
                commonAddress.gpsLatLng = new LatLng(lat,lng);
                commonAddress.cellindex = cursor.getInt(cursor.getColumnIndex("cellindex"));
                commonAddress.nettype  = cursor.getInt(cursor.getColumnIndex("nettype"));
                commonAddresses.add(commonAddress);
                cursor.moveToNext();
            }
            return commonAddresses;
        }
        else return null;
    }


    public CommonAddress queryCommonAddressByName(String name)
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+COMMON_ADDRESS_TABLE_NAME+ " where name = '"+name+"';", null);
        cursor.moveToFirst();
        if(cursor.getCount()>0) {

            CommonAddress commonAddress = new CommonAddress();
            commonAddress.name = cursor.getString(cursor.getColumnIndex("name"));
            commonAddress.addressType = cursor.getString(cursor.getColumnIndex("addresstype"));
            commonAddress.id = cursor.getString(cursor.getColumnIndex("_id"));
            commonAddress.address = cursor.getString(cursor.getColumnIndex("address"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
            commonAddress.latLng = new LatLng(lat, lng);

            lat = cursor.getDouble(cursor.getColumnIndex("gpslat"));
            lng = cursor.getDouble(cursor.getColumnIndex("gpslng"));
            commonAddress.gpsLatLng = new LatLng(lat, lng);
            commonAddress.cellindex = cursor.getInt(cursor.getColumnIndex("cellindex"));
            commonAddress.nettype = cursor.getInt(cursor.getColumnIndex("nettype"));

            return commonAddress;
        } else {
            return null;
        }
    }

    public void delCommonAddress(String id)
    {
        if (db == null) db = getWritableDatabase();
        db.delete(COMMON_ADDRESS_TABLE_NAME, "_id=?", new String[] { id });
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
