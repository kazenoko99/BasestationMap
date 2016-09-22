package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.RouteActivity;
import com.wenruisong.basestationmap.activity.CellDetailActivity;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.basestation.LTECell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchCellResultAdapter extends BaseAdapter {

    private List<Cell> mList;
    private Context mContext;
    private AMapLocation myLocation;
    private LatLng locLatLng;
    private boolean isNearby = false;
    private Poi targetPoi;

    public SearchCellResultAdapter(Context context, List<Cell> list,boolean isNearby) {
        mContext = context;
        mList = list;
        this.isNearby = isNearby;

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

    public void setTargetPoi(Poi poi){
        targetPoi = poi;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_cell, null);
        holder = new ViewHolder();
        holder.cellName = (TextView) view.findViewById(R.id.cell_name);
        holder.cellAddress = (TextView) view.findViewById(R.id.cell_address);
        holder.cellGothere = (TextView) view.findViewById(R.id.cell_go_there);
        holder.cellDistance = (TextView) view.findViewById(R.id.cell_distance);
        holder.cellNetType = (TextView)view.findViewById(R.id.cell_nettype);
        holder.cellDetail = (TextView)view.findViewById(R.id.cell_detail);
        final Cell cell = mList.get(position);
        holder.cellName.setText(cell.cellName);
        if(isNearby){
            holder.cellDistance.setText(DistanceUtils.getDistance(cell.aMapLatLng,targetPoi.getCoordinate()));
        }
        else if(cell.aMapLatLng !=null && locLatLng!=null)
        holder.cellDistance.setText(DistanceUtils.getDistance(cell.aMapLatLng,locLatLng));
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
       holder.cellDetail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext,CellDetailActivity.class);
               intent.putExtra("CELL",mList.get(position));
               mContext.startActivity(intent);
           }
       });
        holder.cellGothere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(mContext, RouteActivity.class);
                Bundle bundle = new Bundle();
                Cell cell =  (Cell)getItem(position);
                bundle.putString(Constants.ROUTE_TARGET_NAME,cell.bsName+"(小区)");
                bundle.putDouble(Constants.ROUTE_TARGET_LAT,cell.aMapLatLng.latitude);
                bundle.putDouble(Constants.ROUTE_TARGET_LNG,cell.aMapLatLng.longitude);
                intent.putExtra(Constants.ROUTE_BUNDLE,bundle);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    class ViewHolder {
        TextView cellName;
        TextView cellAddress;
        TextView cellDistance;
        TextView cellGothere;
        TextView cellType;
        TextView cellDetail;
        TextView cellNetType;
    }
}

