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


/**
 * Created by yinjianhua on 15-8-15.
 */
public class OfflineCityHeaderListAdapter extends BaseAdapter {

    private List<OfflineMapCity> mList;
    private Context mContext;
    private View.OnClickListener mDownloadBtnListener;

    public OfflineCityHeaderListAdapter(Context context, List<OfflineMapCity> list
            , View.OnClickListener listener) {
        mContext = context;
        mList = list;
        mDownloadBtnListener = listener;
    }

    public void updateList(List<OfflineMapCity> list){
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
        ViewHolder holder;

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_child, null);
        holder = new ViewHolder();
        holder.cityName = (TextView) view.findViewById(R.id.tv_city_name);
        holder.citySize = (TextView) view.findViewById(R.id.tv_city_size);
        holder.cityStatus = (TextView) view.findViewById(R.id.tv_city_status);
        holder.downloadBtn = (Button) view.findViewById(R.id.btn_city_down);

        String cityName = mList.get(position).getCity();
        holder.cityName.setText(cityName);

        String size = getCitySize(position);
        holder.citySize.setText(size + ResourcesUtil.getString(R.string.offline_map_unit_mb));

        // 根据情况显示下载按钮还是下载状态
        int downloadState = mList.get(position).getState();

        if ((OfflineMapStatus.CHECKUPDATES == downloadState)
                ||(OfflineMapStatus.EXCEPTION_NETWORK_LOADING == downloadState)
                ||(OfflineMapStatus.ERROR == downloadState)) {
            String downState = getDownloadState(position);
            holder.downloadBtn.setText(downState);
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.downloadBtn.setTag(mList.get(position));
            holder.downloadBtn.setOnClickListener(mDownloadBtnListener);
            holder.cityStatus.setVisibility(View.INVISIBLE);
        } else {
            String downState = getDownloadState(position);
            holder.cityStatus.setText(downState);
            holder.cityStatus.setVisibility(View.VISIBLE);
            holder.downloadBtn.setVisibility(View.INVISIBLE);
            holder.downloadBtn.setOnClickListener(null);
        }
        return view;
    }


    private String getCitySize(final int position) {
        String size;

        size = String.format("%.2f", mList.get(position).getSize() / (1024 * 1024f));

        return size;
    }


    private String getDownloadState(final int position) {
        int downloadState;
        String downloadStateText = null;
        String cityName = null;

        downloadState = mList.get(position).getState();

        switch (downloadState) {
            case OfflineMapStatus.SUCCESS:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_downloaded);
                break;
            case OfflineMapStatus.LOADING:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_downloading);
                break;
            case OfflineMapStatus.WAITING:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download_waiting);
                break;
            case OfflineMapStatus.PAUSE:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download_pauseing);
                break;
            case OfflineMapStatus.UNZIP:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download_unziping);
                break;
            case OfflineMapStatus.STOP:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download_stop);
                break;
            case OfflineMapStatus.CHECKUPDATES:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;
            case OfflineMapStatus.ERROR:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;

//            case OfflineMapLoader.EMPTY_VALUE:
//                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
//                break;
        }

        return downloadStateText;
    }


    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
    }
}
