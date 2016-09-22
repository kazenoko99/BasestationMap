package com.wenruisong.basestationmap;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wenruisong.basestationmap.activity.MapPickPlaceActivity;
import com.wenruisong.basestationmap.fragment.SearchBasestationFragment;
import com.wenruisong.basestationmap.fragment.SearchCellFragment;
import com.wenruisong.basestationmap.fragment.SearchCommonAddressFragment;
import com.wenruisong.basestationmap.fragment.SearchHistoryFragment;
import com.wenruisong.basestationmap.fragment.SearchPoiFragment;
import com.wenruisong.basestationmap.fragment.SearchSettingFragment;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener
{
    private String TAG = "SearchActivity";

    private int mType;
    private ImageView btn_back;
    private TabLayout tab;
    private TabLayout tab_guide;
    private AutoCompleteTextView searchView;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<Fragment> mGuideFragments = new ArrayList<>();

    private SearchPoiFragment searchPoiFragment;
    private SearchCellFragment searchCellFragment;
    private SearchBasestationFragment searchBsFragment;

    private SearchHistoryFragment searchHistoryFragment;
    private SearchCommonAddressFragment searchCommonAddressFragment;

    private SearchSettingFragment searchSettingFragment = new SearchSettingFragment();
    private SearchPagerAdapter searchPagerAdapter;
    private SearchPagerAdapter guidePagerAdapter;
    private ViewPager vp_search;
    private ViewPager vp_guide;
    private LinearLayout selectLocation;
    private TextView mSelectMyLocation, mSelectSavedPlace, mSelectMapPoint ,mSelectGspPoint;
    private FrameLayout mSelectMyLocationLayout, mSelectSavedPlaceLayout, mSelectMapPointLayout ,mSelectGspPointLayout;

    private String[] mTitle = new String[]{"搜基站", "查小区", "搜地点", "搜索设置"};

    private String[] mGuideTitles = new String[]{"历史记录", "收藏夹"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();

    }

    private void initViews(){
        mType = getIntent().getIntExtra(Constants.TYPE, 0);
        mSelectMyLocation = (TextView)findViewById(R.id.my_location);
        mSelectMyLocationLayout = (FrameLayout) findViewById(R.id.my_location_layout);
        mSelectSavedPlaceLayout = (FrameLayout) findViewById(R.id.common_address_layout);
        mSelectMapPointLayout = (FrameLayout) findViewById(R.id.map_select_layout);
        mSelectGspPointLayout = (FrameLayout) findViewById(R.id.gps_location_layout);
        mSelectMapPointLayout.setOnClickListener(this);
        mSelectMyLocation.setOnClickListener(this);
        selectLocation = (LinearLayout) findViewById(R.id.select_location);


        searchView = (AutoCompleteTextView) findViewById(R.id.searchview);
        searchView.setHint("请输入搜索内容");
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        searchView.addTextChangedListener(watcher);

        vp_search = (ViewPager) findViewById(R.id.vp_search);
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        vp_search.setOffscreenPageLimit(3);

        searchPoiFragment = SearchPoiFragment.newInstance(mType);
        searchCellFragment = SearchCellFragment.newInstance(mType);
        searchBsFragment = SearchBasestationFragment.newInstance(mType);
        mFragments.add(searchBsFragment);
        mFragments.add(searchCellFragment);
        mFragments.add(searchPoiFragment);
        mFragments.add(searchSettingFragment);

        searchCommonAddressFragment =SearchCommonAddressFragment.newInstance(mType);
        searchHistoryFragment = SearchHistoryFragment.newInstance(mType);

        mGuideFragments.add(searchHistoryFragment);
        mGuideFragments.add(searchCommonAddressFragment);

        searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        guidePagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        guidePagerAdapter.setData(mGuideTitles,mGuideFragments);
        searchPagerAdapter.setData(mTitle,mFragments);
        vp_search.setAdapter(searchPagerAdapter);
        tab = (TabLayout) findViewById(R.id.tab_layout);
        tab.setOnTabSelectedListener(mTabListener);
        tab.setupWithViewPager(vp_search);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(tab);
        vp_search.addOnPageChangeListener(listener);

        vp_guide.setAdapter(guidePagerAdapter);
        tab_guide = (TabLayout) findViewById(R.id.tab_guide);
        tab_guide.setOnTabSelectedListener(mGuideTabListener);
        tab_guide.setupWithViewPager(vp_guide);
        final TabLayout.TabLayoutOnPageChangeListener listener2 =
                new TabLayout.TabLayoutOnPageChangeListener(tab_guide);
        vp_guide.addOnPageChangeListener(listener2);

        switch (mType)
        {
            case Constants.NormalSearch:
                selectLocation.setVisibility(View.GONE);
                break;
            case Constants.RoutePicPlace:
                selectLocation.setVisibility(View.VISIBLE);
                break;
            case Constants.CommonPlacePic:
                selectLocation.setVisibility(View.VISIBLE);
                vp_guide.setVisibility(View.GONE);
                tab_guide.setVisibility(View.GONE);
                tab.setVisibility(View.VISIBLE);
                mSelectSavedPlaceLayout.setVisibility(View.GONE);
                mSelectMyLocationLayout.setVisibility(View.GONE);
                break;
        }
    }

    private TabLayout.OnTabSelectedListener mTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            vp_search.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

    private TabLayout.OnTabSelectedListener mGuideTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            vp_guide.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };


    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                vp_guide.setVisibility(View.GONE);
                tab_guide.setVisibility(View.GONE);
                tab.setVisibility(View.VISIBLE);
                searchPoiFragment.updateDatas(s.toString());
                searchCellFragment.updateDatas(s.toString());
                searchBsFragment.updateDatas(s.toString());
            } else {
                if(mType == Constants.CommonPlacePic) {
                    tab.setVisibility(View.GONE);
                    vp_guide.setVisibility(View.VISIBLE);
                    tab_guide.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.map_select_layout:
                Intent intent = new Intent(SearchActivity.this,MapPickPlaceActivity.class);
                intent.putExtra(Constants.TYPE, mType);
                startActivityForResult(intent,mType);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if(requestCode ==Constants.CommonPlacePic){
                finish();
            }

            if(requestCode ==Constants.RoutePicPlace){
                Intent intent = new Intent();
                intent.putExtras(data);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


    class SearchPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;
         String[] titiles;
        List<Fragment> fragments;
        public SearchPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public void setData( String[] titles,List<Fragment> fragments){
            this.fragments = fragments;
            this.titiles = titles;
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titiles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
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
