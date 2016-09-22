package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.wenruisong.basestationmap.R;

import java.util.List;


public class CitySearchResultAdapter extends BaseAdapter {

    private List<OfflineMapCity> mResultList;
    private Context mContext;

    public CitySearchResultAdapter(Context context, List<OfflineMapCity> resultList) {
        mContext = context;
        mResultList = resultList;
    }

    public void updateResultList(List<OfflineMapCity> resultList){
        mResultList = resultList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mResultList == null ? 0 : mResultList.size();
    }

    @Override
    public OfflineMapCity getItem(int position) {
        return mResultList == null ? null : mResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_child, null);
            holder = new ViewHolder();
            holder.cityName = (TextView) view.findViewById(R.id.tv_city_name);
            holder.citySize = (TextView) view.findViewById(R.id.tv_city_size);
            holder.cityStatus = (TextView) view.findViewById(R.id.tv_city_status);
            holder.downloadBtn = (Button) view.findViewById(R.id.btn_city_down);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((mResultList != null) && (mResultList.get(position) != null)) {
            String cityName;
            cityName = mResultList.get(position).getCity();
            holder.cityStatus.setVisibility(View.INVISIBLE);
            holder.citySize.setVisibility(View.INVISIBLE);
            holder.downloadBtn.setVisibility(View.INVISIBLE);
            holder.cityName.setText(cityName);
        }
        return view;
    }

    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
    }
}
