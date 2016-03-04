package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;
import java.util.List;


public class OfflineMapFragment extends BackPressHandledFragment{
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private OfflineMapPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private String[] mTitle= new String[2];

    public static OfflineMapFragment getInstance(Bundle bundle) {
        OfflineMapFragment backPressHandledFragment = new OfflineMapFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logs.d("OfflineMapModule", "OfflineMapFragment onCreateView");
        View root = initView(inflater);
        return root;
    }

    private View initView(LayoutInflater inflater) {
        Logs.d("OfflineMapModule", "OfflineMapFragment initView");
        View root = inflater.inflate(R.layout.fragment_offline_view_pager, null);
        mViewPager = (ViewPager) root.findViewById(R.id.vp_container);
        View drawerIcon = root.findViewById(R.id.drawer_icon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openOrCloseDrawers();
            }
        });
        mTitle= new String[]{
                ResourcesUtil.getString(R.string.offline_map_download_title),
                ResourcesUtil.getString(R.string.offline_map_title)
        } ;
        OfflineMapDownloadFragment downloadFragment = (OfflineMapDownloadFragment) OfflineMapDownloadFragment.getInstance(null);
        OfflineMapCityFragment cityFragment = (OfflineMapCityFragment) OfflineMapCityFragment.getInstance(null);
        mFragments.add(downloadFragment);
        mFragments.add(cityFragment);
        mAdapter = new OfflineMapPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout=(TabLayout)root.findViewById(R.id.tab_layout);
        mTabLayout.setOnTabSelectedListener(mTabListener);
        mTabLayout.setupWithViewPager(mViewPager);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
        mViewPager.addOnPageChangeListener(listener);
        return root;
    }

    private TabLayout.OnTabSelectedListener mTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class OfflineMapPagerAdapter extends FragmentPagerAdapter {
        public OfflineMapPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle[position];
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
