package com.wenruisong.basestationmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wenruisong.basestationmap.model.ImportedCity;

import java.util.ArrayList;

/**
 * Created by wen on 2016/5/6.
 */
public class ImportedCellsSqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ImportedCells.db";
    private static final String TABLE_NAME = "ImportedCellsTable";
    private static final String CREATE_TABLE= " create table "
            + " ImportedCellsTable(_id integer primary key autoincrement, city text,nettype text,"
            + " cellcount integer)";

    private SQLiteDatabase db;

    public ImportedCellsSqliteHelper(Context c)
    {
        super(c, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;
        db.execSQL(CREATE_TABLE);
    }


    public void insertImportedCells (ImportedCity importedCell)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+ " where city = '"
                + importedCell.city + "';", null);
        if(cursor.getCount()>0){
            return;
        }

        try
        {
            ContentValues values = new ContentValues();
            values.put("city",importedCell.city);
            values.put("cellcount",  importedCell.cellCount);
            values.put("nettype", importedCell.netType );
            db.insert(TABLE_NAME, null, values);
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public ArrayList<ImportedCity> queryImportedCells ()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+ ";", null);
        ArrayList<ImportedCity> importedCells = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            for(int i=0; i<cursor.getCount();i++) {

                ImportedCity importedCell = new ImportedCity();
                importedCell.city = cursor.getString(cursor.getColumnIndex("city"));
                importedCell.netType = cursor.getString(cursor.getColumnIndex("nettype"));
                importedCell.id = cursor.getString(cursor.getColumnIndex("_id"));
                importedCell.cellCount  = cursor.getInt(cursor.getColumnIndex("cellcount"));
                importedCells.add(importedCell);
                cursor.moveToNext();
            }
            return importedCells;
        }
        else return null;
    }


    public ArrayList<ImportedCity> queryImportedCitys ()
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+ " GROUP BY city;", null);
        ArrayList<ImportedCity> importedCells = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {
            for(int i=0; i<cursor.getCount();i++) {

                ImportedCity importedCell = new ImportedCity();
                importedCell.city = cursor.getString(cursor.getColumnIndex("city"));
                importedCell.netType = cursor.getString(cursor.getColumnIndex("nettype"));
                importedCell.id = cursor.getString(cursor.getColumnIndex("_id"));
                importedCell.cellCount  = cursor.getInt(cursor.getColumnIndex("cellcount"));
                importedCells.add(importedCell);
                cursor.moveToNext();
            }
            return importedCells;
        }
        else return null;
    }


    public void deleteImportedCells (String id)
    {
        if (db == null) db = getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[] { id });
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
