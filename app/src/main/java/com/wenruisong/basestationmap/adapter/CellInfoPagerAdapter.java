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

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.map.CellInfoGsmView;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/21.
 */
public class CellInfoPagerAdapter extends PagerAdapter {
    ArrayList<GSMCell> mCells = new ArrayList<>();
    ArrayList<CellInfoGsmView> views = new ArrayList<>();
    ArrayList<Boolean> flags = new ArrayList<>();
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


    public void setDates(ArrayList<GSMCell> cells) {
        mCells = cells;
        for (GSMCell cell : cells) {
            flags.add(false);
        }
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
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_gsm_cellinfo, null);

        CellInfoGsmView gsmInfoView = new CellInfoGsmView(root);
        gsmInfoView.initWidget(mCells.get(itemPos));
        views.add(gsmInfoView);
        container.addView(root);
        return root;
    }
}


