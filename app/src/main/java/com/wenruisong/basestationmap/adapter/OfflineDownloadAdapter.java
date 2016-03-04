package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class OfflineDownloadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MKOLUpdateElement> mCityList = null;
    DecimalFormat decimalFormat;
    MKOfflineMap mOffline;
    public OfflineDownloadAdapter(Context context,MKOfflineMap mOffline) {
        mContext = context;
        this.mOffline = mOffline;
        mCityList = mOffline.getAllUpdateInfo();
        decimalFormat=new DecimalFormat(".00");//
    }

    public void setCityList(MKOfflineMap mOffline) {
        this.mCityList = mOffline.getAllUpdateInfo();
    }



    @Override
    public int getCount() {
        if(mCityList==null)
            return 0;
        return mCityList.size();
    }

    @Override
    public Object getItem(int paramInt) {
        return mCityList.get(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {
        return paramInt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup paramViewGroup) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_download, null);
            holder = new ViewHolder();
            holder.tv_city_name = (TextView) view.findViewById(R.id.tv_city_name);
            holder.tv_download_status = (TextView) view.findViewById(R.id.tv_city_status);
            holder.tv_city_size = (TextView) view.findViewById(R.id.tv_city_size);
            holder.tv_city_server_size = (TextView) view.findViewById(R.id.tv_city_server_size);
            holder.btn_download_ctrl = (Button) view.findViewById(R.id.btn_control_download);
            holder.pb_download = (ProgressBar) view.findViewById(R.id.pb_download);
            holder.btn_control_delete = (Button) view.findViewById(R.id.btn_control_delete);
            holder.btn_control_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOffline.remove(((MKOLUpdateElement)view.getTag()).cityID);
                    notifyDataSetChanged();
                }
            });
//            holder.btn_download_ctrl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra("x", e.geoPt.longitude);
//                    intent.putExtra("y", e.geoPt.latitude);
//                    intent.setClass(OfflineDemo.this, BaseMapDemo.class);
//                    startActivity(intent);
//                }
//            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        String cityName = "";
    String cityServerSize = "";
    String citySizeStr = "";
        boolean btnIsEnable = false;
        int progressValue = 0;
        int downloadStatus;

        if ((mCityList != null) && (mCityList.get(position) != null)) {
            cityName = mCityList.get(position).cityName;
            downloadStatus = mCityList.get(position).status;
            float citySize = (mCityList.get(position).size / 1024f / 1024f);

            cityServerSize = "/"+decimalFormat.format(mCityList.get(position).serversize / 1024f / 1024f)+"M";
            progressValue =  mCityList.get(position).ratio;
            if (cityName != null) {
                holder.tv_city_name.setText(cityName);
            }
            switch (downloadStatus)
            {
                case MKOLUpdateElement.DOWNLOADING:
                holder.tv_download_status.setText("下载中");
                    holder.pb_download.setVisibility(View.VISIBLE);
                break;
                case MKOLUpdateElement.SUSPENDED:
                    holder.tv_download_status.setText("暂停");
                    holder.pb_download.setVisibility(View.VISIBLE);
                    break;
                case MKOLUpdateElement.FINISHED:
                default:
                    holder.tv_download_status.setVisibility(View.GONE);
                    holder.pb_download.setVisibility(View.GONE);
                    holder.btn_download_ctrl.setEnabled(btnIsEnable);
                    holder.btn_download_ctrl.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            }

            holder.tv_city_size.setText(decimalFormat.format(citySize));
            holder.btn_download_ctrl.setTag(mCityList.get(position));
            holder.btn_control_delete.setTag(mCityList.get(position));
            holder.pb_download.setProgress(progressValue);
            holder.tv_city_server_size.setText(cityServerSize);

            // 若不显示离线包大小,则更改离线包大小的View的padding值
            if (citySizeStr.length() == 0) {
                holder.tv_city_size.setPadding(0, 0, 0, 0);
            } else {
                float paddingRight = ResourcesUtil.getDimension(R.dimen.offline_map_download_list_item_status_margin_size);
                holder.tv_city_size.setPadding(0, 0, (int) paddingRight, 0);
            }
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_city_name;
        TextView tv_download_status;
        TextView tv_city_size;
        TextView tv_city_server_size;
        Button btn_download_ctrl;
        Button btn_control_delete;
        ProgressBar pb_download;
    }
}


