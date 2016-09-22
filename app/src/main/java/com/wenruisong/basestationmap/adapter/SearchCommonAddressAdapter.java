package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.model.CommonAddress;

import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchCommonAddressAdapter extends BaseAdapter {

    private List<CommonAddress> mList;
    private Context mContext;
    private AMapLocation myLocation;
    private LatLng locLatLng;
    public SearchCommonAddressAdapter(Context context, List<CommonAddress> list) {
        mContext = context;
        mList = list;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }

   public void setDatas(List<CommonAddress> list)
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_common_address, null);
        holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.name);
        holder.address = (TextView) view.findViewById(R.id.address);
        holder.icon = (ImageView)view.findViewById(R.id.titlePic) ;
        holder.type = (TextView) view.findViewById(R.id.type);
        CommonAddress commonAddress = mList.get(position);
        holder.name.setText(commonAddress.name);
        holder.address.setText(commonAddress.address);
        return view;
    }

    class ViewHolder {
        public TextView address;
        public TextView name;
        public ImageView icon;
        public TextView type;
    }
}

