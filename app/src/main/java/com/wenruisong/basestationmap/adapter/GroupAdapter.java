package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.GroupDetailActivity;
import com.wenruisong.basestationmap.group.Group;

import java.util.ArrayList;

/**
 * Created by wen on 2016/5/24.
 */
public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    protected Context mContext;
    protected ArrayList<Group> mDataList;
    protected LayoutInflater mInflater;    // 视图容器

    public GroupAdapter(Context context, ArrayList<Group> groups) {
        mContext = context;
        mDataList = groups;
        mInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder = null;
        View  root = mInflater.inflate(R.layout.item_group, parent, false);
            holder = new ViewHolder(root);
            holder.groupImage = (ImageView) root.findViewById(R.id.group_image);
            holder.groupCreator = (TextView) root.findViewById(R.id.group_maker);
            holder.groupBelonging = (TextView) root.findViewById(R.id.group_belong);
            holder.groupMemberCount = (TextView) root.findViewById(R.id.group_menber_cout);
            holder.groupName = (TextView) root.findViewById(R.id.group_name);
            holder.groupDiscription = (TextView) root.findViewById(R.id.group_discription);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Group mGroup = mDataList.get(position);
        viewHolder.groupName.setText(mGroup.groupName);
        viewHolder.groupBelonging.setText(mGroup.groupBelonging);
        if(mGroup.groupCreator!=null)
        viewHolder.groupCreator.setText(mGroup.groupCreator.getUsername());
        if(mGroup.groupDiscption!=null)
        viewHolder.groupDiscription.setText(mGroup.groupDiscption);
    }

    //定义ViewHolder，包括两个控件
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView groupImage;
        public TextView groupName;
        public TextView groupCreator;
        public TextView groupBelonging;
        public TextView groupCity;
        public TextView groupMemberCount;
        public TextView groupDiscription;
        public View root;
        public ViewHolder(View root) {
            super(root);
            this.root = root;
            root.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, GroupDetailActivity.class);
            Group mGroup = mDataList.get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putSerializable("GROUP",mGroup);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        if ((mDataList == null) || (mDataList.size() == 0)) {
            return 0;
        }

        return (mDataList.size());
    }



}
