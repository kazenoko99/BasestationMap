package com.wenruisong.basestationmap.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.CsvFileAdapter;
import com.wenruisong.basestationmap.adapter.ImportedCellAdapter;
import com.wenruisong.basestationmap.common.CsvFile;
import com.wenruisong.basestationmap.database.CsvToSqliteHelper;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/24.
 */
public class BasestationImportFragment extends BackPressHandledFragment{
    private ListView importedcellListView;
    private ImportedCellAdapter importedCellAdapter;
    private CsvFileAdapter csvFileAdapter;
    private ArrayList<CsvFile> mCsvFiles;
    private  String mCity;
    private String mType;
    private final static String CITY = "CITY";
    private final static String TYPE = "TYPE";
    public static BasestationImportFragment newInstance(String city, String type) {
        BasestationImportFragment f = new BasestationImportFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);
        args.putString(CITY,city);
        args.putString(TYPE,type);
        return f;
    }
    public void setCity(String city){
        mCity = city;
    }
    public void setDates(ArrayList<CsvFile> list){
        mCsvFiles = list;
        if(csvFileAdapter!=null)
        csvFileAdapter.setDates(list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_basestation_import, container, false);
        importedcellListView = (ListView) viewGroup.findViewById(R.id.cell_list);

        Bundle bundle = getArguments();
        mCity = bundle.getString(CITY);
        mType = bundle.getString(TYPE);
        csvFileAdapter = new CsvFileAdapter(getContext(),mCsvFiles);
        importedcellListView.setAdapter(csvFileAdapter);
        importedcellListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                showDialog(mCsvFiles.get(position).csvName,mCsvFiles.get(position).csvPath);
            }
        });
        return viewGroup;
    }

    void showDialog(String CsvFileName,final String path){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否以"+CsvFileName+"导入为"+mCity+"的"+mType+"基站数据");
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CsvToSqliteHelper dbhelp=new CsvToSqliteHelper(getActivity(), Constants.DBNAME, mType ,1);
                SQLiteDatabase mydb= dbhelp.getReadableDatabase();
                // CsvToSqliteHelper.createGsmTable(mydb);
                dbhelp.createCellTable(mydb, path,mCity, mType);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }


}
