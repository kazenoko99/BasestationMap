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
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchBsResultAdapter extends BaseAdapter {

    private List<Basestation> mList;
    private Context mContext;
    private BDLocation myLocation;
    private LatLng locLatLng;
    public SearchBsResultAdapter(Context context, List<Basestation> list) {
        mContext = context;
        mList = list;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }

   public void setDatas(List<Basestation> list)
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

        final Basestation Bs = mList.get(position);
        holder.cellName.setText(Bs.bsName);
        if(Bs.baiduLatLng!=null && locLatLng!=null)
        holder.cellDistance.setText(DistanceUtils.getDistance(Bs.baiduLatLng ,locLatLng));
        if (Bs.address == null)
        holder.cellAddress.setVisibility(View.GONE);
        else {
            holder.cellAddress.setVisibility(View.VISIBLE);
            holder.cellAddress.setText(Bs.address);
        }
        return view;
    }

    class ViewHolder {
        TextView cellName;
        TextView cellAddress;
        TextView cellDistance;
        TextView cellGothere;
    }
}

