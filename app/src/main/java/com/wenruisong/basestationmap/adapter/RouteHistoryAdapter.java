package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.model.RouteHistoryItem;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/16.
 */
public class RouteHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RemoveItemClickListener mListener;

    public interface RemoveItemClickListener {
        public void removeItem(int position);
    }

    protected Context mContext;
    protected ArrayList mDataList;
    protected LayoutInflater mInflater;    // 视图容器

    public RouteHistoryAdapter(Context context, RemoveItemClickListener listener) {
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
        if (viewType == 0) {
            RouteHistoryViewHolder holder = null;
            root = mInflater.inflate(R.layout.clear_history_item, parent, false);
            holder = new RouteHistoryViewHolder(root);
            holder.typeImage = null;
            holder.deleteButton = null;
            holder.startName = (TextView) root.findViewById(R.id.clearhistory);
            return holder;
        } else if (viewType == 1) {
            root = mInflater.inflate(R.layout.item_route_history, parent, false);
            RouteHistoryViewHolder holder = new RouteHistoryViewHolder(root);
            holder.typeImage = (ImageView) root.findViewById(R.id.type);
            holder.startName = (TextView) root.findViewById(R.id.startName);
            holder.endName = (TextView) root.findViewById(R.id.endName);
            holder.deleteButton = (ViewGroup) root.findViewById(R.id.delete);
            return holder;
        }

      return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (position == mDataList.size()) {
            //清除所有历史
            return;
        }

        RouteHistoryViewHolder viewHolder = (RouteHistoryViewHolder) holder;
        Object item = mDataList.get(position);

            viewHolder.startName.setText(((RouteHistoryItem) item).mStartName);

        viewHolder.endName.setText(((RouteHistoryItem)item).mEndName);


            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.removeItem(position);
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


    public class RouteHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView typeImage;
        public TextView startName;
        public TextView endName;
        public ViewGroup deleteButton;

        public RouteHistoryViewHolder(View root) {
            super(root);
        }

        @Override
        public void onClick(View v) {

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
