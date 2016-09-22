package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;

import java.util.List;


public class DrawerListAdapter extends BaseAdapter {

    private List<String> mNameList;
    private List<Integer> mIconList;
    private Context mContext;
    private ImageView indicatorView;

    public DrawerListAdapter(Context context, List<String> list, List<Integer> iconList) {
        mContext = context;
        mNameList = list;
        mIconList = iconList;

    }


    @Override
    public int getCount() {
        return mNameList == null ? 0 : mNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_drawer_common, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_drawer_icon);
        TextView textView = (TextView) view.findViewById(R.id.tv_drawer_name);
        ImageView indicator = (ImageView) view.findViewById(R.id.iv_drawer_indicator);
        view.setTag(indicator);
        if((mIconList!=null) && (mIconList.size()>position)){
            imageView.setImageDrawable(mContext.getResources().getDrawable((mIconList.get(position).intValue())));
        }

        textView.setText(mNameList.get(position));

        return view;
    }

}
