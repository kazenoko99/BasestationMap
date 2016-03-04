package com.wenruisong.basestationmap.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.wenruisong.basestationmap.R;

import java.util.Arrays;
import java.util.List;


public class DrawerListAdapter extends BaseExpandableListAdapter {

    private List<String> mNameList;
    private List<Integer> mIconList;
    private Context mContext;
    private RotateAnimation expandAnimation;
    private RotateAnimation collappseAnimation;
    private ImageView indicatorView;
    private static List<String> mToolList = Arrays.asList("标尺", "指南针", "GPS打点");
    public DrawerListAdapter(Context context, List<String> list, List<Integer> iconList) {
        mContext = context;
        mNameList = list;
        mIconList = iconList;
        expandAnimation =new RotateAnimation(0f,180f, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        expandAnimation.setDuration(300);//设置动画持续时间
        expandAnimation.setFillAfter(true);//停在最后

        collappseAnimation =new RotateAnimation(180f,0f, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        collappseAnimation.setDuration(300);//设置动画持续时间
    }


    @Override
    public int getGroupCount() {
        return mNameList == null ? 0 : mNameList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition == mNameList.size()-2)
            return 3;
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_common, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_drawer_icon);
        TextView textView = (TextView) view.findViewById(R.id.tv_drawer_name);
        ImageView indicator = (ImageView) view.findViewById(R.id.iv_drawer_indicator);
        view.setTag(indicator);
        if((mIconList!=null) && (mIconList.size()>groupPosition)){
            imageView.setImageDrawable(mContext.getDrawable((mIconList.get(groupPosition).intValue())));
        }
        if(groupPosition==4)
        {
            indicator.setVisibility(View.VISIBLE);
            if(isExpanded)
                indicator.startAnimation(expandAnimation);
            else
                indicator.startAnimation(collappseAnimation);
        }
        textView.setText(mNameList.get(groupPosition));

        return view;
    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(groupPosition == mNameList.size()-2)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_tools, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_drawer_icon);
            TextView textView = (TextView) view.findViewById(R.id.tv_drawer_name);


            imageView.setImageDrawable(mContext.getDrawable((mIconList.get(childPosition).intValue())));
            textView.setText(mToolList.get(childPosition));
            return view;
        }
        else
            return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
