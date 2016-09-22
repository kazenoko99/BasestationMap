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

import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.List;

/**
 * 用于离线地图下载的城市列表：可展开的ListView的adapter
 * <p/>
 */
public class CityExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    private Context mContext;
    private List<OfflineMapProvince> mProvinceList;
    private boolean[] mGroupOpenArray;// 记录一级目录是否打开


    public CityExpandableListAdapter(Context context, List<OfflineMapProvince> list) {
        mContext = context;
        mProvinceList = list;
        mGroupOpenArray = new boolean[mProvinceList.size()];
    }

    public void setProvinceList(List<OfflineMapProvince> mProvinceList) {
        this.mProvinceList = mProvinceList;
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
        return mProvinceList.get(groupPosition).getCityList().size();
    }

    /**
     * 获取一级标签下二级标签的内容
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mProvinceList.get(groupPosition).getCityList().get(childPosition);
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
        String cityName =  mProvinceList.get(groupPosition).getCityList().get(childPosition).getCity();
        holder.cityName.setText(cityName);

        holder.citySize.setVisibility(View.INVISIBLE);
        holder.downloadBtn.setVisibility(View.INVISIBLE);
        holder.downloadLayout.setVisibility(View.INVISIBLE);
        holder.cityStatus.setVisibility(View.INVISIBLE);
        // 如果未下载过或者网络异常


        return view;
    }



    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityStatus;
        Button downloadBtn;
        RelativeLayout downloadLayout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
