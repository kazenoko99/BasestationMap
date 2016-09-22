package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.utils.DisplayUtil;
import com.wenruisong.basestationmap.view.SlidingButtonView;

import java.util.List;

/**
 * Created by wen on 2016/5/24.
 */
public class CommonAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements SlidingButtonView.IonSlidingButtonListener  {

    protected Context mContext;
    protected List<CommonAddress> mAddressList;
    protected LayoutInflater mInflater;    // 视图容器
    private OnSlidingViewClickListener mIDeleteBtnClickListener;
    private SlidingButtonView mMenu = null;

    public CommonAddressAdapter(Context context, List<CommonAddress> commonAddresses) {
        mContext = context;
        mAddressList = commonAddresses;
        mInflater = LayoutInflater.from(context); // 创建视图容器并设置上下文
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder;
        View  root = mInflater.inflate(R.layout.item_common_address, parent, false);
            holder = new ViewHolder(root);
            holder.pic = (ImageView) root.findViewById(R.id.titlePic);
            holder.name = (TextView) root.findViewById(R.id.name);
            holder.address = (TextView) root.findViewById(R.id.address);
            holder.devideLine =  root.findViewById(R.id.devide_line);
            holder.layout_content =  (ViewGroup) root.findViewById(R.id.layout_content);
            holder.delete = (TextView) root.findViewById(R.id.tv_delete);
            holder.edit = (TextView) root.findViewById(R.id.tv_edit);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.layout_content.getLayoutParams().width = DisplayUtil.getScreenWidth(mContext);
        CommonAddress commonAddress = mAddressList.get(position);
        viewHolder.name.setText(commonAddress.name);
        viewHolder.address.setText(commonAddress.address);
        viewHolder.layout_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                closeMenu();
                viewHolder.slidingButtonView.openMenu();
                return false;
            }
        });

   if(position ==getItemCount()-1){
       viewHolder.devideLine.setVisibility(View.GONE);
      }
    }

    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }


    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(mMenu != slidingButtonView){
                closeMenu();
            }
        }
    }


    public void closeMenu() {
        if(mMenu != null)
        mMenu.closeMenu();
        mMenu = null;

    }

    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        Log.i("asd","mMenuΪnull");
        return false;
    }


    //定义ViewHolder，包括两个控件
    private class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView pic;
        public TextView address;
        public TextView name;
        public View devideLine;
        public TextView edit;
        public TextView delete;
        public ViewGroup layout_content;
        public SlidingButtonView slidingButtonView;
        public ViewHolder(final View root) {
            super(root);
            ((SlidingButtonView) root).setSlidingButtonListener(CommonAddressAdapter.this);
            slidingButtonView =  ((SlidingButtonView) root);

        }
    }

    @Override
    public int getItemCount() {
        if ((mAddressList == null) || (mAddressList.size() == 0)) {
            return 0;
        }

        return (mAddressList.size());
    }

    public void removeData(int position){
        mAddressList.remove(position);
        notifyItemRemoved(position);

    }



    public interface OnSlidingViewClickListener {
        void onItemClick(View view,int position);
        void onDeleteBtnCilck(View view,int position);
    }

}
