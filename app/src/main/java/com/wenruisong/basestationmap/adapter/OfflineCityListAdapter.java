package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.map.OfflineMapManager;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.List;

public class OfflineCityListAdapter extends BaseAdapter {

    private List<MKOLUpdateElement> mList;
    private Context mContext;
    private MKOfflineMap mOffline;

    public OfflineCityListAdapter(Context context, List<MKOLUpdateElement> list) {
        mContext = context;
        mList = list;
        mOffline = OfflineMapManager.getInstance().mOffline;
    }

    public void updateList(List<MKOLUpdateElement> list){
        if(list != null){
            mList = list;
            notifyDataSetChanged();
        }
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

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_child, null);
        holder = new ViewHolder();
        holder.cityName = (TextView) view.findViewById(R.id.tv_city_name);
        holder.citySize = (TextView) view.findViewById(R.id.tv_city_size);
        holder.cityStatus = (TextView) view.findViewById(R.id.tv_city_status);
        holder.downloadBtn = (Button) view.findViewById(R.id.btn_city_down);

        final String cityName = mList.get(position).cityName;
        final int cityID= mList.get(position).cityID;
        holder.cityName.setText(cityName);

        String size = getCitySize(position);
        holder.citySize.setText(size + ResourcesUtil.getString(R.string.offline_map_unit_mb));



        if ( mList.get(position).ratio!=100) {
            holder.downloadBtn.setText("可下载");
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.downloadBtn.setTag(mList.get(position));
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOffline.start(cityID);
                    holder.downloadBtn.setText("正在下载");
                }
            });
            holder.cityStatus.setVisibility(View.INVISIBLE);
        } else {

            holder.cityStatus.setVisibility(View.VISIBLE);
            holder.downloadBtn.setVisibility(View.INVISIBLE);
            holder.downloadBtn.setOnClickListener(null);
        }
        return view;
    }


    private String getCitySize(final int position) {
        String size;

        size = String.format("%.2f", mList.get(position).size / (1024 * 1024f));

        return size;
    }



    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
    }
}
