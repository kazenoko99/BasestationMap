package com.wenruisong.basestationmap.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.csvreader.CsvReader;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.model.ImportedCity;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by wen on 2016/1/25.
 */
public class CsvToSqliteHelper extends SQLiteOpenHelper {
    public static final String PACKAGE_NAME = "com.wenruisong.basestationmap";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;

    private static String Tag = "CsvToSqliteHelper";
    private static Context mContext;
    private static String mCsvPath;
    private static SQLiteDatabase mDatebase;
    private static int csvRowsCount;
    private static ProgressDialog mProgress;
    private static String mCellType;
    private ImportedCellsSqliteHelper mImportedCellsSqliteHelper;
    public CsvToSqliteHelper(Context context, String name, String cellType,String csvPath,int version) {
        super(context, name, null, version);
        mCellType = cellType;
        mContext = context;
        mCsvPath = csvPath;
    }

    public CsvToSqliteHelper(Context context,String name, String cellType,int version) {
        super(context, name, null, version);
        mCellType = cellType;
        mContext = context;
    }




    public  void createCellTable( SQLiteDatabase db,String path,String city, String type)
    {
        Logs.d(Tag,path);
        mCsvPath = path;
        mDatebase = db;
        mCellType = type;
        switch (type) {
            case "GSM": {
                if (Settings.isTableExsit(city,"GSM")) {
                    Logs.d(Tag, "GSM is Exsit");
                    db.execSQL(String.format(Constants.DROP_DB_GSM,city));
                }
                db.execSQL(String.format(Constants.CREATE_DB_GSM,city));
                Settings.setTableExsit(city,"GSM", true);
                //create table gsm_cells(CID integer,NAME VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double);
            }
            case "LTE": {
                Logs.d(Tag, Constants.CREATE_DB_LTE);
                mDatebase = db;
                if(Settings.isTableExsit(city,"LTE")) {
                    Logs.d(Tag, "LTE is Exsit");
                    db.execSQL(String.format(Constants.DROP_DB_LTE,city));
                }
                db.execSQL(String.format(Constants.CREATE_DB_LTE,city));
                Settings.setTableExsit(city,"LTE", true);
                //create table gsm_cells(CID integer,NAME VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double);
            }

        }
        CreateTableTask task = new CreateTableTask();
        setCellType(type);
        task.execute(type,city);
    }


    private static  void showProgress(){
        mProgress = new ProgressDialog(mContext);
        mProgress.setIcon(R.drawable.ic_launcher);
        mProgress.setTitle("正在导入基站数据");
        //mProgress.setProgressDrawable(new ColorDrawable(mContext.getColor(R.color.blue)));
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgress.setCancelable(false);
        mProgress.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "确定", Toast.LENGTH_SHORT).show();
            }
        });

        mProgress.setButton2("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT).show();

            }
        });
        mProgress.show();
    }

    public static void setCellType(String cellType)
    {
        mCellType = cellType;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatebase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

     class CreateTableTask extends AsyncTask<String, Integer, Integer> {
        private String mCity;
        private String mNetType;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            mImportedCellsSqliteHelper = new ImportedCellsSqliteHelper(mContext);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                mCity = params[1];
                mNetType = params[0];
                CsvReader r = new CsvReader(mCsvPath, ',', Charset.forName("GBK"));
                r.readHeaders();
                csvRowsCount = CsvParser.csvGetRows(mCsvPath);
                mProgress.setMax(csvRowsCount);
                Logs.d(Tag, "get csvRowsCount" + csvRowsCount);
                switch (params[0]) {
                    case "GSM":
                        for (int i = 0; i < csvRowsCount; i++) {
                            CsvParser.csvToDatebaseGSM(mDatebase,params[1], r, i);
                            publishProgress(i);
                        }
                        break;
                    case "LTE":
                        for (int i = 0; i < csvRowsCount; i++) {
                            CsvParser.csvToDatebaseLTE(mDatebase,params[1], r, i);
                            publishProgress(i);
                        }
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 更新进度条
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //参数值为0-10000，所以要乘以2500，values[0]取值分别是1，2，3，4
            Logs.d(Tag, "updated" + values[0]);
            mProgress.setProgress(values[0]);
            //setProgress(values[0] * 2500);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
                    Settings.setDatabaseReady(mCity,mNetType, true);
                    ImportedCity importedCell = new ImportedCity();
                    importedCell.cellCount = csvRowsCount;
                    importedCell.netType = mNetType;
                    importedCell.city =mCity;
                    mImportedCellsSqliteHelper.insertImportedCells(importedCell);

            }
    }
}
