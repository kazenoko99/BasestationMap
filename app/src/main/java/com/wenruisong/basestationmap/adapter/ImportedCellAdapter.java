package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/24.
 */
public class ImportedCellAdapter extends BaseAdapter {
    ArrayList<Cell> mList;
    private Context mContext;
    public ImportedCellAdapter(Context context,  ArrayList<Cell> cells) {
        mContext = context;
        mList = cells;
    }
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? 0 : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_importedcell, null);
        holder = new ViewHolder();
        holder.cellName = (TextView) view.findViewById(R.id.tv_imported_cellname);
        holder.cellid = (TextView) view.findViewById(R.id.tv_imported_cellid);
        holder.lat = (TextView) view.findViewById(R.id.tv_imported_celllat);
        holder.lon = (TextView) view.findViewById(R.id.tv_imported_celllon);

        Cell basestationCell = mList.get(position);
        holder.cellName.setText(basestationCell.cellName);
        holder.cellid.setText(basestationCell.cellid);
        return view;
    }

    class ViewHolder {
        TextView cellName;
        TextView cellid;
        TextView lat;
        TextView lon;
    }
}
