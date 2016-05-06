package com.wenruisong.basestationmap;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wenruisong.basestationmap.fragment.SearchBasestationFragment;
import com.wenruisong.basestationmap.fragment.SearchCellFragment;
import com.wenruisong.basestationmap.fragment.SearchPoiFragment;
import com.wenruisong.basestationmap.fragment.SearchSettingFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener
{
    private String TAG = "SearchActivity";

    public final static String TYPE = "TYPE";
    public final static int NormalSearch = 0;
    public final static int PicPlace = 1;
    private int mType;
    private ImageView btn_back;
    private TabLayout tab;
    private AutoCompleteTextView searchView;
    private List<Fragment> mFragments = new ArrayList<>();
    private SearchPoiFragment searchPoiFragment;
    private SearchCellFragment searchCellFragment;
    private SearchBasestationFragment searchBsFragment;
    private SearchSettingFragment searchSettingFragment = new SearchSettingFragment();
    private SearchPagerAdapter searchPagerAdapter;
    private ViewPager vp_search;
    private LinearLayout selectLocation;
    private TextView mSelectMyLocation, mSelectSavedPlace, mSelectMapPoint;
    private String[] mTitle = new String[]{"搜基站", "查小区", "搜地点", "搜索设置"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mType = getIntent().getIntExtra(TYPE, 0);
        mSelectMyLocation = (TextView)findViewById(R.id.my_location);
        mSelectMyLocation.setOnClickListener(this);
        selectLocation = (LinearLayout) findViewById(R.id.select_location);
        if (mType == 1)
            selectLocation.setVisibility(View.VISIBLE);
        else
            selectLocation.setVisibility(View.GONE);
        searchView = (AutoCompleteTextView) findViewById(R.id.searchview);

        searchView.setHint("请输入搜索内容");
        searchView.addTextChangedListener(watcher);
        vp_search = (ViewPager) findViewById(R.id.vp_search);
        searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        searchPoiFragment = SearchPoiFragment.newInstance(mType);
        searchCellFragment = SearchCellFragment.newInstance(mType);
        searchBsFragment = SearchBasestationFragment.newInstance(mType);
        mFragments.add(searchBsFragment);
        mFragments.add(searchCellFragment);
        mFragments.add(searchPoiFragment);
        mFragments.add(searchSettingFragment);
        vp_search.setAdapter(searchPagerAdapter);
        tab = (TabLayout) findViewById(R.id.tab_layout);
        tab.setOnTabSelectedListener(mTabListener);
        tab.setupWithViewPager(vp_search);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(tab);
        vp_search.addOnPageChangeListener(listener);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

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

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                searchPoiFragment.updateDatas(s.toString());
                searchCellFragment.updateDatas(s.toString());
                searchBsFragment.updateDatas(s.toString());
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
        }
    }


    class SearchPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;

        public SearchPagerAdapter(FragmentManager fm) {
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
