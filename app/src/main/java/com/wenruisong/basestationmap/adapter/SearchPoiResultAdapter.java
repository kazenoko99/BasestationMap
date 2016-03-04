package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/2/20.
 */
public class SearchPoiResultAdapter extends BaseAdapter {
    private Context mContext;
    private BDLocation myLocation;
     private List<SuggestionResult.SuggestionInfo> suggestionInfos = new ArrayList<>();
    private LatLng locLatLng;
    public SearchPoiResultAdapter(Context context) {
        mContext = context;
        if (LocationHelper.getInstance().isLocated) {
            myLocation = LocationHelper.getInstance().location;
            locLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }
    public void setDatas(SuggestionResult suggestionResult)
    {
        suggestionInfos =  suggestionResult.getAllSuggestions();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return suggestionInfos == null ? 0 : suggestionInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestionInfos == null ? 0 : suggestionInfos.get(position);
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

        final SuggestionResult.SuggestionInfo poiInfo = suggestionInfos.get(position);
        holder.poiInfoName.setText(poiInfo.key);
        if(poiInfo.pt!=null && locLatLng!=null)
            holder.poiInfoDistance.setText(DistanceUtils.getDistance(poiInfo.pt, locLatLng));
        if (poiInfo.district == null)
            holder.poiInfoAddress.setVisibility(View.GONE);
        else {
            holder.poiInfoAddress.setVisibility(View.VISIBLE);
            holder.poiInfoAddress.setText(poiInfo.district.toString());
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

