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
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.GSMBasestation;
import com.wenruisong.basestationmap.basestation.LTEBasestation;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchBsResultAdapter extends BaseAdapter {

    private List<Basestation> mList;
    private Context mContext;
    private AMapLocation myLocation;
    private LatLng locLatLng;

    private boolean isNearby = false;
    private Poi targetPoi;
    public SearchBsResultAdapter(Context context, List<Basestation> list,boolean isNearby) {
        mContext = context;
        mList = list;
        this.isNearby = isNearby;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }

   public void setTargetPoi(Poi poi){
       targetPoi = poi;
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
        final Basestation Bs = mList.get(position);
        holder.cellName.setText(Bs.bsName);
        if(isNearby){
            holder.cellDistance.setText(DistanceUtils.getDistance(Bs.amapLatLng,targetPoi.getCoordinate()));
        }
        else if(Bs.amapLatLng !=null && locLatLng!=null)
        holder.cellDistance.setText(DistanceUtils.getDistance(Bs.amapLatLng,locLatLng));
        if (Bs.address == null)
        holder.cellAddress.setVisibility(View.GONE);
        else {
            holder.cellAddress.setVisibility(View.VISIBLE);
            holder.cellAddress.setText(Bs.address);
        }

        if(Bs instanceof LTEBasestation){
            holder.cellNetType.setText("LTE");
        } else if( Bs instanceof GSMBasestation){
            holder.cellNetType.setText("GSM");
        }

        holder.cellGothere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(mContext, RouteActivity.class);
                Bundle bundle = new Bundle();
                Basestation bs =  (Basestation)getItem(position);
                bundle.putString(Constants.ROUTE_TARGET_NAME,bs.bsName+"(基站)");
                bundle.putDouble(Constants.ROUTE_TARGET_LAT,bs.amapLatLng.latitude);
                bundle.putDouble(Constants.ROUTE_TARGET_LNG,bs.amapLatLng.longitude);
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
        TextView cellNetType;
        TextView cellDetail;
    }
}

