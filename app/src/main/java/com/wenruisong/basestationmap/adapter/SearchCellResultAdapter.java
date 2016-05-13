package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.basestation.LTECell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchCellResultAdapter extends BaseAdapter {

    private List<Cell> mList;
    private Context mContext;
    private BDLocation myLocation;
    private LatLng locLatLng;
    public SearchCellResultAdapter(Context context, List<Cell> list) {
        mContext = context;
        mList = list;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }

   public void setDatas(List<Cell> list)
   {
       mList = list;
       notifyDataSetChanged();
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
        final ViewHolder holder;

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_cell, null);
        holder = new ViewHolder();
        holder.cellName = (TextView) view.findViewById(R.id.cell_name);
        holder.cellAddress = (TextView) view.findViewById(R.id.cell_address);
        holder.cellGothere = (TextView) view.findViewById(R.id.cell_go_there);
        holder.cellDistance = (TextView) view.findViewById(R.id.cell_distance);
        holder.cellNetType = (TextView)view.findViewById(R.id.cell_nettype);
        final Cell cell = mList.get(position);
        holder.cellName.setText(cell.cellName);
        if(cell.baiduLatLng!=null && locLatLng!=null)
        holder.cellDistance.setText(DistanceUtils.getDistance(cell.baiduLatLng ,locLatLng));
        if (cell.address == null)
        holder.cellAddress.setVisibility(View.GONE);
        else {
            holder.cellAddress.setVisibility(View.VISIBLE);
            holder.cellAddress.setText(cell.address);
        }

        if(cell instanceof LTECell){
            holder.cellNetType.setText("LTE");
        } else if( cell instanceof GSMCell){
            holder.cellNetType.setText("GSM");
        }
        return view;
    }

    class ViewHolder {
        TextView cellName;
        TextView cellAddress;
        TextView cellDistance;
        TextView cellGothere;
        TextView cellType;
        TextView cellNetType;
    }
}

