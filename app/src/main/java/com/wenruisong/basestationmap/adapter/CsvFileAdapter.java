package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.CsvFile;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/24.
 */

public class CsvFileAdapter extends BaseAdapter {
    final class ViewHolder {
        public ImageView iconImageView;
        public TextView nameTextView;
        public TextView pathTextView;
    }
  private ArrayList<CsvFile> mCsvFiles;
    private Context mContext;
    public CsvFileAdapter(Context context,ArrayList<CsvFile> csvFiles) {
        mContext = context;
        mCsvFiles = csvFiles;
    }

    public void setDates(ArrayList<CsvFile> list) {
        mCsvFiles = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.fragment_file_list_item, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.pathTextView = (TextView) view.findViewById(R.id.path);
            viewHolder.nameTextView = (TextView) view.findViewById(R.id.name);
        }

         CsvFile csvFile = mCsvFiles.get(position);

        viewHolder.nameTextView.setText(csvFile.csvName);
        viewHolder.pathTextView.setText(csvFile.csvPath);
        return view;
    }

    @Override
    public int getCount() {
        return mCsvFiles ==null? 0 : mCsvFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mCsvFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}