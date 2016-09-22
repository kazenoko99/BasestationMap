package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.help.Tip;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchPoiResultAdapter extends BaseAdapter {
    private Context mContext;
    private AMapLocation myLocation;
     private List<Tip> tips = new ArrayList<>();
    private LatLng locLatLng;
    public SearchPoiResultAdapter(Context context) {
        mContext = context;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }
    public void setDatas( List<Tip> infos)
    {
        tips =  infos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tips == null ? 0 : tips.size();
    }

    @Override
    public Object getItem(int position) {
        return tips == null ? 0 : tips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_poi, null);
        holder = new ViewHolder();
        holder.poiInfoName = (TextView) view.findViewById(R.id.poi_name);
        holder.poiInfoAddress = (TextView) view.findViewById(R.id.poi_address);
        holder.poiInfoGothere = (TextView) view.findViewById(R.id.poi_go_there);
        holder.poiInfoDistance = (TextView) view.findViewById(R.id.poi_distance);

        final Tip tip = tips.get(position);
        holder.poiInfoName.setText(tip.getName());
        if(tip.getPoint()!=null && locLatLng!=null) {
            LatLng pt = AMapUtil.convertToLatLng(tip.getPoint());
            holder.poiInfoDistance.setText(DistanceUtils.getDistance(pt, locLatLng));
        }
        if (tip.getDistrict() == null)
            holder.poiInfoAddress.setVisibility(View.GONE);
        else {
            holder.poiInfoAddress.setVisibility(View.VISIBLE);
            holder.poiInfoAddress.setText(tip.getDistrict());
        }
        return view;
    }

    class ViewHolder {
        TextView poiInfoName;
        TextView poiInfoAddress;
        TextView poiInfoDistance;
        TextView poiInfoGothere;
    }
}

