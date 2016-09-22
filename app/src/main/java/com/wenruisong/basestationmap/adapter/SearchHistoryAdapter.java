package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.model.SearchHistoryItem;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/16.
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void removeItem(int position);
        void clickItem(int position);
    }

    protected Context mContext;
    protected ArrayList mDataList;
    protected LayoutInflater mInflater;    // 视图容器

    public SearchHistoryAdapter(Context context, OnItemClickListener listener) {
          mListener = listener;
        mContext = context;
        mInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
    }

    public void setDates(ArrayList dates){
        mDataList = dates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        ViewHolder holder = null;
        if (viewType == 0) {
            root = mInflater.inflate(R.layout.clear_history_item, parent, false);
            holder = new ViewHolder(root);
            holder.typeImage = null;
            holder.deleteButton = null;
            holder.title = (TextView) root.findViewById(R.id.clearhistory);
        } else if (viewType == 1) {
            root = mInflater.inflate(R.layout.search_history_list_two_line_item, parent, false);
            holder = new ViewHolder(root);
            holder.typeImage = (ImageView) root.findViewById(R.id.type);
            holder.title = (TextView) root.findViewById(R.id.keyword);
            holder.address = (TextView) root.findViewById(R.id.address);
            holder.deleteButton = (ViewGroup) root.findViewById(R.id.delete);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (position == mDataList.size()) {
            //清除所有历史
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;
        Object item = mDataList.get(position);
            if(((SearchHistoryItem)mDataList.get(position)).address!=null) {
                viewHolder.address.setVisibility(View.VISIBLE);
                viewHolder.address.setText(((SearchHistoryItem) mDataList.get(position)).address);
            }
            viewHolder.title.setText(((SearchHistoryItem)mDataList.get(position)).keyword);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.removeItem(position);
                }
            });
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clickItem(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDataList.size()) {// 只有清楚历史，才能达到这个值
            return 0;
        } else {
            return 1;
        }
    }

    //定义ViewHolder，包括两个控件
    private class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView typeImage;
        public TextView title;
        public TextView address;
        public ViewGroup deleteButton;
        public View root;
        public ViewHolder(View root) {
            super(root);
            this.root = root;
        }
    }

    @Override
    public int getItemCount() {
        if ((mDataList == null) || (mDataList.size() == 0)) {
            return 0;
        }

        return (mDataList.size() + 1);
    }
}
