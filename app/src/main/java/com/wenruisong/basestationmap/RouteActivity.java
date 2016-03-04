package com.wenruisong.basestationmap;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.wenruisong.basestationmap.fragment.RouteBusFragment;
import com.wenruisong.basestationmap.fragment.RouteDriveFragment;
import com.wenruisong.basestationmap.fragment.RouteWalkFragment;
import com.wenruisong.basestationmap.fragment.SearchBsFragment;
import com.wenruisong.basestationmap.fragment.SearchCellFragment;
import com.wenruisong.basestationmap.fragment.SearchPoiFragment;
import com.wenruisong.basestationmap.fragment.SearchSettingFragment;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    private ImageView btn_back;
    private TabLayout tab;
    private List<Fragment> mFragments = new ArrayList<>();
    private RouteBusFragment routeBusFragment = new RouteBusFragment();
    private RouteWalkFragment routeWalkFragment = new RouteWalkFragment();
    private RouteDriveFragment routeDriveFragment = new RouteDriveFragment();
    private SearchSettingFragment searchSettingFragment = new SearchSettingFragment();
    private RoutePagerAdapter routePagerAdapter;
    private ViewPager vp_route;
    private String[] mTitle  = new String[]{ "驾车","公交","步行"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        vp_route =(ViewPager)findViewById(R.id.vp_route);
        routePagerAdapter= new RoutePagerAdapter(getSupportFragmentManager());
        mFragments.add(routeDriveFragment);
        mFragments.add(routeBusFragment);
        mFragments.add(routeWalkFragment);
        vp_route.setAdapter(routePagerAdapter);
        tab = (TabLayout)findViewById(R.id.tab_layout);
        tab.setOnTabSelectedListener(mTabListener);
        tab.setupWithViewPager(vp_route);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(tab);
        vp_route.addOnPageChangeListener(listener);

        btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private TabLayout.OnTabSelectedListener mTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            vp_route.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };


    class RoutePagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;

        public RoutePagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container,
                    position);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
