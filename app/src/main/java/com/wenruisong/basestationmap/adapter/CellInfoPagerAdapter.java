package com.wenruisong.basestationmap.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.telephony.CellInfoGsm;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.lbsapi.BMapManager;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.map.CellInfoGsmView;
import com.wenruisong.basestationmap.map.CellInfoLteView;
import com.wenruisong.basestationmap.map.CellInfoView;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/21.
 */
public class CellInfoPagerAdapter extends PagerAdapter {
    ArrayList<Cell> mCells = new ArrayList<>();
    ArrayList<CellInfoView> views = new ArrayList<>();
    Context context;

    public CellInfoPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        int size = mCells.size();
        if (size > 1) {
            return size * 2;
        } else {
            return size;
        }
    }


    public void setDates(ArrayList<Cell> cells) {
        mCells = cells;
        notifyDataSetChanged();
    }

    public int getDataCount() {
        return mCells.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int itemPos = position;
        if (mCells.size() > 1) {
            itemPos = position % mCells.size();
        }

        if(mCells.get(itemPos) instanceof GSMCell) {
            View root = LayoutInflater.from(context).inflate(R.layout.fragment_gsm_cellinfo, null);
            CellInfoGsmView gsmInfoView = new CellInfoGsmView(context, root);
            gsmInfoView.initWidget(mCells.get(itemPos));
            views.add(gsmInfoView);
            container.addView(root);
            return root;
        } else {
            View root = LayoutInflater.from(context).inflate(R.layout.fragment_lte_cellinfo, null);
            CellInfoLteView lteInfoView = new CellInfoLteView(context,root);
            lteInfoView.initWidget(mCells.get(itemPos));
            views.add(lteInfoView);
            container.addView(root);
            return root;
        }

    }
}


