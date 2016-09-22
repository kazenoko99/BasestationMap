package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.DisplayUtil;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.HashMap;
import java.util.List;


public class OfflineDownloadAdapter extends BaseAdapter {
    private Context mContext;
    private List<OfflineMapCity> mCityList;
    private List<String> mCanUpdateCityList;
    private View.OnClickListener mCtrlClickListener;
//    private String mDownloadingCityName = "";
//    private int mDownloadingComplete = 0;
//    private String mUnzipingCityName = "";
//    private int mUnzipingComplete = 0;

    private HashMap<String, Integer> mCityDownloadCompleteMap;
    private HashMap<String, Integer> mCityUnzipCompleteMap;

    private boolean mIsShowRecommendList;

    public OfflineDownloadAdapter(Context context, List<OfflineMapCity> cityList
            , List<String> canUpdateCityList, View.OnClickListener ctrlClickListener, boolean isShowRecommendList) {
        mContext = context;
        mCityList = cityList;
        mCanUpdateCityList = canUpdateCityList;
        mCtrlClickListener = ctrlClickListener;
        mIsShowRecommendList = isShowRecommendList;
    }

    public void setCityList(List<OfflineMapCity> mCityList) {
        this.mCityList = mCityList;
    }

//    public void setDownloadingCityName(String mDownloadingCityName) {
//        this.mDownloadingCityName = mDownloadingCityName;
//    }
//
//    public void setDownloadingComplete(int mDownloadingComplete) {
//        this.mDownloadingComplete = mDownloadingComplete;
//    }
//
//    public void setUnzipingCityName(String mUnzipingCityName) {
//        this.mUnzipingCityName = mUnzipingCityName;
//    }
//
//    public void setUnzipingComplete(int mUnzipingComplete) {
//        this.mUnzipingComplete = mUnzipingComplete;
//    }

    public void setCityDownloadCompleteMap(HashMap<String, Integer> hashMap) {
        mCityDownloadCompleteMap = hashMap;
    }

    public void setCityUnzipCompleteMap(HashMap<String, Integer> hashMap) {
        mCityUnzipCompleteMap = hashMap;
    }

    @Override
    public int getCount() {
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
            holder.tv_download_status = (TextView) view.findViewById(R.id.tv_download_status);
            holder.tv_city_size = (TextView) view.findViewById(R.id.tv_city_size);
            holder.btn_download_ctrl = (Button) view.findViewById(R.id.btn_control_download);
            holder.pb_download = (ProgressBar) view.findViewById(R.id.pb_download);
            holder.rl_download_ctrl = (RelativeLayout) view.findViewById(R.id.rl_control_download);

            // 设置按键监听
//            holder.btn_download_ctrl.setOnClickListener(mCtrlClickListener);
            holder.rl_download_ctrl.setOnClickListener(mCtrlClickListener);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        String cityName;
        String cityStatus = "";
        String citySizeStr = "";
        String btnTextStr = "";
        int btnVisibility = View.INVISIBLE;
        boolean btnIsEnable = false;
        int progressbarVisibility = View.INVISIBLE;
        int progressValue = 0;

        int downloadStatus;

        if ((mCityList != null) && (mCityList.get(position) != null)) {
            cityName = mCityList.get(position).getCity();
            downloadStatus = mCityList.get(position).getState();
            float citySize = (mCityList.get(position).getSize() / 1024f / 1024f);

            switch (downloadStatus) {
                case OfflineMapStatus.SUCCESS:
                    if (!mIsShowRecommendList) {
                        cityStatus = "";
                        if ((mCanUpdateCityList != null) && (mCanUpdateCityList.contains(cityName))) {
                            btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_update);
                            btnVisibility = View.VISIBLE;
                        } else {
                            btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_see);
                            if (cityName.equals(ResourcesUtil.getString(R.string.offline_map_city_gaiyaotu))) {
                                btnVisibility = View.INVISIBLE;
                            } else {
                                btnVisibility = View.VISIBLE;
                            }
                        }

                        citySizeStr = String.format("%.2f", citySize) + ResourcesUtil.getString(R.string.offline_map_unit_mb);
                        progressbarVisibility = View.INVISIBLE;
                        btnIsEnable = true;
                    }

                    break;
                case OfflineMapStatus.LOADING:
                    if (!mIsShowRecommendList) {
                        cityStatus = ResourcesUtil.getString(R.string.offline_map_downloading);
                        progressbarVisibility = View.VISIBLE;
//                        if (mDownloadingCityName.equals(cityName)) {
//                            progressValue = mDownloadingComplete;
//                            float downloadedSize = citySize * mDownloadingComplete / 100f;
//                            citySizeStr = String.format("%.2f", downloadedSize) + "/" + String.format("%.2f", citySize) + "MB";
//
                        if ((mCityDownloadCompleteMap!=null)&&(mCityDownloadCompleteMap.containsKey(cityName))) {
                            progressValue = mCityDownloadCompleteMap.get(cityName);
                            float downloadedSize = citySize * progressValue / 100f;
                            citySizeStr = String.format("%.2f", downloadedSize) + "/" + String.format("%.2f", citySize) + "MB";

                        }else{  // 如果还没回调进度,则当作0
                            progressValue = 0;
                            float downloadedSize = citySize * progressValue / 100f;
                            citySizeStr = String.format("%.2f", downloadedSize) + "/" + String.format("%.2f", citySize) + "MB";
                        }

                        btnVisibility = View.VISIBLE;
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_pause);
                        btnIsEnable = true;
                    }

                    break;
                case OfflineMapStatus.UNZIP:
                    if (!mIsShowRecommendList) {
                        cityStatus = ResourcesUtil.getString(R.string.offline_map_download_unziping);
//                        if (mUnzipingCityName.equals(cityName)) {
//                            progressbarVisibility = View.VISIBLE;
//                            progressValue = mUnzipingComplete;
//                            float unzipedSize = citySize * mDownloadingComplete / 100f;
//                            citySizeStr = String.format("%.2f", unzipedSize) + "/" + String.format("%.2f", citySize)
//                                    + ResourcesUtil.getString(R.string.offline_map_unit_mb);
//                        }

                        if ((mCityUnzipCompleteMap!=null)&&(mCityUnzipCompleteMap.containsKey(cityName))) {
                            progressbarVisibility = View.VISIBLE;
                            progressValue = mCityUnzipCompleteMap.get(cityName);
                            float unzipedSize = citySize * progressValue / 100f;
                            citySizeStr = String.format("%.2f", unzipedSize) + "/" + String.format("%.2f", citySize)
                                    + ResourcesUtil.getString(R.string.offline_map_unit_mb);
                        }
                        btnVisibility = View.VISIBLE;
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_pause);
                        btnIsEnable = false;
                    }

                    break;

                case OfflineMapStatus.WAITING:
                    if (!mIsShowRecommendList) {
                        // 正在等待的时候不显示下载的大小
                        citySizeStr = "";
                        cityStatus =  ResourcesUtil.getString(R.string.offline_map_download_waiting);
                        progressbarVisibility = View.INVISIBLE;
                        btnVisibility = View.VISIBLE;
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_cancel);
                        btnIsEnable = true;
                    }

                    break;

                case OfflineMapStatus.PAUSE:
                    if (!mIsShowRecommendList) {
                        // 暂停的时候不显示下载的大小
                        citySizeStr = "";
                        cityStatus = ResourcesUtil.getString(R.string.offline_map_download_pauseing);
                        progressbarVisibility = View.INVISIBLE;
                        btnVisibility = View.VISIBLE;
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_continue);
                        btnIsEnable = true;
                    }

                    break;
//                case OfflineMapStatus.CHECKUPDATES:
//                    if (!mIsShowRecommendList) {
//                        // FIXME: 日后应该把这个时候应该显示"等待下载",同时需要变动Loader的代码
//                        //                cityStatus = "检查更新：CHECKUPDATES";
//                        cityStatus = ResourcesUtil.getString(R.string.offline_map_downloading);
//                        if (mDownloadingCityName.equals(cityName)) {
//                            //                        cityStatus += "  " + 0 + "%";
//                            progressbarVisibility = View.VISIBLE;
//                            progressValue = 0;
//                            citySizeStr = String.format("%.2f", 0f) + "/" + String.format("%.2f", citySize)
//                                    + ResourcesUtil.getString(R.string.offline_map_unit_mb);
//                        }
//                        btnVisibility = View.VISIBLE;
//                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download_pause);
//                        btnIsEnable = true;
//                    }
//
//                    break;

                case OfflineMapStatus.ERROR:
                    if (!mIsShowRecommendList) {
//                        cityStatus = "错误";
                        cityStatus = "";
                        citySizeStr = ResourcesUtil.getString(R.string.offline_map_download_error);
                        progressbarVisibility = View.INVISIBLE;
                        btnVisibility = View.VISIBLE;
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_redownload);
                        btnIsEnable = true;
                    }

                    break;

                case OfflineMapStatus.CHECKUPDATES:
                    if (mIsShowRecommendList) {
                        cityStatus = "";
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_download);
                        citySizeStr = String.format("%.2f", citySize)
                                + ResourcesUtil.getString(R.string.offline_map_unit_mb);
                        progressbarVisibility = View.INVISIBLE;
                        btnIsEnable = true;
                        btnVisibility = View.VISIBLE;
                    }

                    break;

                case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                    if (mIsShowRecommendList) {
                        cityStatus = "";
                        btnTextStr = ResourcesUtil.getString(R.string.offline_map_network_error);
                        citySizeStr = String.format("%.2f", citySize)
                                + ResourcesUtil.getString(R.string.offline_map_unit_mb);
                        progressbarVisibility = View.INVISIBLE;
                        btnIsEnable = true;
                        btnVisibility = View.VISIBLE;
                    }

                    break;


                // 根据高德开发的说法：不会出现STOP状态
                /*case OfflineMapStatus.STOP:
                    cityStatus = "停止";
                    progressbarVisibility = View.INVISIBLE;
                    btnVisibility = View.VISIBLE;
                    btnTextStr = "继续";
                    btnIsEnable = true;
                    break;*/
            }

            if (cityName != null) {
                holder.tv_city_name.setText(cityName);
            }
            holder.tv_download_status.setText(cityStatus);
            holder.tv_city_size.setText(citySizeStr);
//            holder.btn_download_ctrl.setTag(mCityList.get(position));
            holder.btn_download_ctrl.setVisibility(btnVisibility);
            holder.btn_download_ctrl.setText(btnTextStr);
            holder.btn_download_ctrl.setEnabled(btnIsEnable);

            holder.rl_download_ctrl.setVisibility(btnVisibility);
            holder.rl_download_ctrl.setTag(mCityList.get(position));
            holder.rl_download_ctrl.setEnabled(btnIsEnable);

            holder.pb_download.setVisibility(progressbarVisibility);
            if (View.VISIBLE == progressbarVisibility) {
                holder.pb_download.setProgress(progressValue);
            }

            // 若不显示离线包大小,则更改离线包大小的View的padding值
            if (citySizeStr.length() == 0) {
                holder.tv_city_size.setPadding(0, 0, 0, 0);
            } else {
                float paddingRight = ResourcesUtil.getDimension(R.dimen.offline_map_download_list_item_status_margin_size);
                paddingRight = DisplayUtil.px2dip(paddingRight);
                holder.tv_city_size.setPadding(0, 0, (int) paddingRight, 0);
            }
        }
        return view;
    }

    public void setIsShowRecommendList(boolean isShow) {
        mIsShowRecommendList = isShow;
    }


    static class ViewHolder {
        TextView tv_city_name;
        TextView tv_download_status;
        TextView tv_city_size;
        Button btn_download_ctrl;
        ProgressBar pb_download;
        RelativeLayout rl_download_ctrl;
    }
}


