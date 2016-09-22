package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.List;


public class OfflineCitySearchResultAdapter extends BaseAdapter {

    private List<OfflineMapCity> mResultList;
    private Context mContext;
    private View.OnClickListener mListener;

    public OfflineCitySearchResultAdapter(Context context, List<OfflineMapCity> resultList,
                                          View.OnClickListener listener) {
        mContext = context;
        mResultList = resultList;
        mListener = listener;
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
            float citySize;
            String citySizeStr;
            String downloadStateStr;
            String cityName;

            cityName = mResultList.get(position).getCity();

            citySize = (mResultList.get(position).getSize() / 1024f / 1024f);
            citySizeStr = String.format("%.2f", citySize) + ResourcesUtil.getString(R.string.offline_map_unit_mb);

            int downloadState = mResultList.get(position).getState();



            if ((OfflineMapStatus.CHECKUPDATES == downloadState)
                    ||(OfflineMapStatus.EXCEPTION_NETWORK_LOADING == downloadState)
                    ||(OfflineMapStatus.ERROR == downloadState)) {
                String downState = getDownloadState(downloadState);
                holder.downloadBtn.setText(downState);
                holder.downloadBtn.setVisibility(View.VISIBLE);
                holder.downloadBtn.setTag(mResultList.get(position));
                holder.downloadBtn.setOnClickListener(mListener);
                holder.cityStatus.setVisibility(View.INVISIBLE);
            } else {
                downloadStateStr = getDownloadState(downloadState);
                holder.cityStatus.setText(downloadStateStr);
                holder.cityStatus.setVisibility(View.VISIBLE);
                holder.downloadBtn.setVisibility(View.INVISIBLE);
                holder.downloadBtn.setOnClickListener(null);
            }

            holder.cityName.setText(cityName);
            holder.citySize.setText(citySizeStr);
        }

        return view;
    }


    private String getDownloadState(int downloadState){
        String downloadStateStr="";

        switch (downloadState) {
            case OfflineMapStatus.SUCCESS:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_downloaded);
                break;
            case OfflineMapStatus.LOADING:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_downloading);
                break;
            case OfflineMapStatus.WAITING:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download_waiting);
                break;
            case OfflineMapStatus.PAUSE:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download_pauseing);
                break;
            case OfflineMapStatus.UNZIP:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download_unziping);
                break;
            case OfflineMapStatus.STOP:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download_stop);
                break;
            case OfflineMapStatus.CHECKUPDATES:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download);
                break;
            case OfflineMapStatus.ERROR:
                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download);
                break;



//            case OfflineMapLoader.EMPTY_VALUE:
//                downloadStateStr = ResourcesUtil.getString(R.string.offline_map_download);
//                break;
        }

        return downloadStateStr;
    }


    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
    }
}
