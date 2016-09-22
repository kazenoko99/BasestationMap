package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.HashMap;
import java.util.List;

/**
 * 用于离线地图下载的城市列表：可展开的ListView的adapter
 * <p/>
 * Created by yinjianhua on 15-6-3.
 */
public class OfflineCityExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    private Context mContext;
    private List<OfflineMapProvince> mProvinceList;
    private HashMap<Object, List<OfflineMapCity>> mCityHashMap;
    private boolean[] mGroupOpenArray;// 记录一级目录是否打开
    private String mDownloadingCityName = "";
    private int mDownloadingComplete = 0;
    private String mUnzipingCityName = "";
    private int mUnzipingComplete = 0;
    private View.OnClickListener mDownloadBtnListener;

    public OfflineCityExpandableListAdapter(Context context, List<OfflineMapProvince> list
            , HashMap<Object, List<OfflineMapCity>> cityHashMap, View.OnClickListener listener) {
        mContext = context;
        mProvinceList = list;
        mCityHashMap = cityHashMap;
        mDownloadBtnListener = listener;
        mGroupOpenArray = new boolean[mProvinceList.size()];
    }

    public void setProvinceList(List<OfflineMapProvince> mProvinceList) {
        this.mProvinceList = mProvinceList;
    }

    public void setCityHashMap(HashMap<Object, List<OfflineMapCity>> mCityHashMap) {
        this.mCityHashMap = mCityHashMap;
    }

    public void setGroupOpenArray(boolean[] groupIsOpen) {
        if (groupIsOpen != null) {
            mGroupOpenArray = groupIsOpen;
        }
    }

    @Override
    public int getGroupCount() {
        return mProvinceList.size();
    }

    /**
     * 获取一级标签内容
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mProvinceList.get(groupPosition).getProvinceName();
    }

    /**
     * 获取一级标签的ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取一级标签下二级标签的总数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mCityHashMap.get(groupPosition).size();
    }

    /**
     * 获取一级标签下二级标签的内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCityHashMap.get(groupPosition).get(childPosition).getCity();
    }

    /**
     * 获取二级标签的ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 指定位置相应的组视图
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 对一级标签进行设置
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView group_text;
        ImageView group_image;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_group, null);
        }
        group_text = (TextView) convertView.findViewById(R.id.group_text);
        group_image = (ImageView) convertView
                .findViewById(R.id.group_image);
        group_text.setText(mProvinceList.get(groupPosition)
                .getProvinceName());
        if (mGroupOpenArray[groupPosition]) {
            group_image.setImageDrawable(ResourcesUtil.getDrawable(
                    R.drawable.map_expand_up));
        } else {
            group_image.setImageDrawable(ResourcesUtil.getDrawable(
                    R.drawable.map_expand_down));
        }
        return convertView;
    }

    /**
     * 对一级标签下的二级标签进行设置
     */
    @Override
    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ViewHolder holder;

        // 展开的每个子List最多10几个,暂不复用,复用发现有错位问题
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_offlinemap_child, null);
        holder = new ViewHolder();
        holder.cityName = (TextView) view.findViewById(R.id.tv_city_name);
        holder.citySize = (TextView) view.findViewById(R.id.tv_city_size);
        holder.cityStatus = (TextView) view.findViewById(R.id.tv_city_status);
        holder.downloadBtn = (Button) view.findViewById(R.id.btn_city_down);
        holder.downloadLayout = (RelativeLayout) view.findViewById(R.id.rl_city_down);

        String cityName = mCityHashMap.get(groupPosition).get(childPosition).getCity();
        holder.cityName.setText(cityName);

        String size = getCitySize(groupPosition, childPosition);
        holder.citySize.setText(size + ResourcesUtil.getString(R.string.offline_map_unit_mb));

        // 根据情况显示下载按钮还是下载状态
        int downloadState = mCityHashMap.get(groupPosition).get(childPosition).getState();

        // 如果未下载过或者网络异常
        if ((OfflineMapStatus.CHECKUPDATES == downloadState)
                ||(OfflineMapStatus.EXCEPTION_NETWORK_LOADING == downloadState)
                ||(OfflineMapStatus.ERROR == downloadState)) {
            String downState = getDownloadState(groupPosition, childPosition);
            holder.downloadBtn.setText(downState);
            holder.downloadBtn.setVisibility(View.VISIBLE);
//            holder.downloadBtn.setTag(mCityHashMap.get(groupPosition).get(childPosition));
//            holder.downloadBtn.setOnClickListener(mDownloadBtnListener);

            holder.downloadLayout.setVisibility(View.VISIBLE);
            holder.downloadLayout.setTag(mCityHashMap.get(groupPosition).get(childPosition));
            holder.downloadLayout.setOnClickListener(mDownloadBtnListener);
            holder.cityStatus.setVisibility(View.INVISIBLE);
        } else {
            String downState = getDownloadState(groupPosition, childPosition);
            holder.cityStatus.setText(downState);
            holder.cityStatus.setVisibility(View.VISIBLE);
            holder.downloadBtn.setVisibility(View.INVISIBLE);
//            holder.downloadBtn.setOnClickListener(null);
            holder.downloadLayout.setVisibility(View.INVISIBLE);
            holder.downloadLayout.setOnClickListener(null);
        }

        return view;
    }


    private String getCitySize(final int groupPosition, final int childPosition) {
        String size;

        if ((groupPosition >= 3) && (childPosition == 0)) { // 如果是省的总大小
            List<OfflineMapCity> cityList = mProvinceList.get(groupPosition).getCityList();
            long sizeCount = 0;
            for (OfflineMapCity city : cityList) {
                sizeCount += city.getSize();
            }

            size = String.format("%.2f", (sizeCount / (1024 * 1024f)));

        } else {
            size = String.format("%.2f", (mCityHashMap.get(groupPosition).get(
                    childPosition).getSize()) / (1024 * 1024f));
        }

        return size;
    }


    private String getDownloadState(int groupPosition, int childPosition) {
        int downloadState;
        String downloadStateText = null;
        String cityName = null;

        downloadState = mCityHashMap.get(groupPosition).get(childPosition).getState();
        cityName = mCityHashMap.get(groupPosition).get(childPosition).getCity();

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
//                downloadStateText = ResourcesUtil.getString(R.string.offline_map_downloading);
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;
            case OfflineMapStatus.ERROR:
//                downloadStateText = ResourcesUtil.getString(R.string.offline_map_downloading);
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;

            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                // 网络异常暂时也显示成下载按钮
                downloadStateText = ResourcesUtil.getString(R.string.offline_map_download);
                break;
        }

        return downloadStateText;
    }

    public void setDownloadingCityName(String mDownloadingCityName) {
        this.mDownloadingCityName = mDownloadingCityName;
    }

    public void setDownloadingComplete(int mDownloadingComplete) {
        this.mDownloadingComplete = mDownloadingComplete;
    }

    public void setUnzipingCityName(String mUnzipingCityName) {
        this.mUnzipingCityName = mUnzipingCityName;
    }

    public void setUnzipingComplete(int mUnzipingComplete) {
        this.mUnzipingComplete = mUnzipingComplete;
    }


    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
        RelativeLayout downloadLayout;
    }

    /**
     * 当选择子节点的时候，调用该方法
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
