package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.CsvFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/5/28.
 */
public class GroupFileAdapter  extends BaseAdapter {
    final class ViewHolder {
        public TextView name;
        public TextView creator;
        public TextView createTime;
        public TextView discription;
    }
    private List<CsvFile> mCsvFiles;
    private Context mContext;
    public GroupFileAdapter(Context context,ArrayList<CsvFile> csvFiles) {
        mContext = context;
        mCsvFiles = csvFiles;
    }

    public void setDates(List<CsvFile> list) {
        mCsvFiles = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_group_file, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.createTime = (TextView) view.findViewById(R.id.file_create_time);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.creator = (TextView) view.findViewById(R.id.file_creator);
            viewHolder.discription = (TextView) view.findViewById(R.id.file_discription);
        }

        CsvFile csvFile = mCsvFiles.get(position);
        viewHolder.createTime.setText(csvFile.getCreatedAt());
        viewHolder.name.setText(csvFile.csvName);
        viewHolder.creator.setText(csvFile.creator.getUsername());
        viewHolder.discription.setText(csvFile.discription);

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