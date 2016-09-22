package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.group.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/5/28.
 */
public class GroupMemberAdapter extends BaseAdapter {
    final class ViewHolder {
        public TextView name;
        public TextView joinedTime;
        public TextView memberType;
    }
    private List<User> mMenbers;
    private Context mContext;
    public GroupMemberAdapter(Context context, ArrayList<User> members) {
        mContext = context;
        mMenbers = members;
    }

    public void setDates(List<User> members) {
        mMenbers = members;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.item_group_member, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.memberType = (TextView) view.findViewById(R.id.type);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
        }

        User member = mMenbers.get(position);
        viewHolder.name.setText(member.getUsername());

        return view;
    }

    @Override
    public int getCount() {
        return mMenbers ==null? 0 : mMenbers.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}