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
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.utils.Logs;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by wen on 2016/1/25.
 */
public class CsvToSqliteHelper extends SQLiteOpenHelper {
    private static String Tag = "CsvToSqliteHelper";
    public static final String PACKAGE_NAME = "com.wenruisong.basestationmap";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;
  //"create table gsm_cells(CID integer 0,NAME VARCHAR2(30),BS VARCHAR2(30)2,LAC integer,BCCH integer4,LAT double,LON double6,
  // AZIMUTH integer,TOTAL_DOWNTILT integer8,DOWNTILT integer,HEIGHT integer10,TYPE integer,BAIDULAT double12,
  // BAIDULON double,ADDRESS VARCHAR2(100))14;BSID integer15 ,INDEX Integer16";
    public static final String CREATE_DB_GSM = "create table gsm_cells(CID integer,NAME VARCHAR2(30),BS VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),BSID integer,CELLINDEX integer);";
    public static final String CREATE_DB_LTE= "create table lte_cells(CID integer,NAME VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double,ADDRESS VARCHAR2(100),BSID integer ,CELLINDEX integer);";
    public static final String DROP_DB_GSM = "drop table gsm_cells;";
    public static final String DROP_DB_LTE= "drop table lte_cells;";

    private static Context mContext;
    private static String mCsvPath;
    private static SQLiteDatabase mDatebase;
    private static int csvRowsCount;
    Cell.CellType mCellType;
    private static ProgressDialog mProgress;
    public CsvToSqliteHelper(Context context, String name, Cell.CellType cellType,String csvPath,int version) {
        super(context, name, null, version);
        mCellType = cellType;
        mContext = context;
        mCsvPath = csvPath;
    }

    public CsvToSqliteHelper(Context context,String name, Cell.CellType cellType,int version) {
        super(context, name, null, version);
        mCellType = cellType;
        mContext = context;
    }

    public void setCellType(Cell.CellType cellType)
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

    public static void createGsmTable( SQLiteDatabase db )
    {
        Logs.d(Tag, CREATE_DB_GSM);
        mDatebase = db;
        if(Settings.isTableExsit("GSM")) {
            Logs.d(Tag, "GSM is Exsit");
            db.execSQL(DROP_DB_GSM);
        }
        db.execSQL(CREATE_DB_GSM);
        Settings.setTableExsit("GSM",true);
        //create table gsm_cells(CID integer,NAME VARCHAR2(30),LAC integer,BCCH integer,LAT double,LON double,AZIMUTH integer,TOTAL_DOWNTILT integer,DOWNTILT integer,HEIGHT integer,TYPE integer,BAIDULAT double,BAIDULON double);
        CreateGSMDbTask task = new CreateGSMDbTask();
        task.execute();
    }



    static class CreateGSMDbTask extends AsyncTask<Void, Integer, Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                CsvReader r = new CsvReader(mCsvPath, ',', Charset.forName("GBK"));
                    r.readHeaders();
                csvRowsCount = CsvParser.csvGetRows(mCsvPath);
                mProgress.setMax(csvRowsCount);
                Logs.d(Tag, "get csvRowsCount"+csvRowsCount);
                for (int i=0; i<csvRowsCount;i++)
                {
                    CsvParser.csvToDatebaseGSM(mDatebase, r,i);
                    publishProgress(i);
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
            Logs.d(Tag, "updated" + values[0] );
            mProgress.setProgress(values[0]);
            //setProgress(values[0] * 2500);
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Settings.setDatabaseReady("GSM", true);
        }
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

}
