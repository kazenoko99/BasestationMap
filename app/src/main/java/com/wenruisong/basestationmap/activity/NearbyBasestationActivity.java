package com.wenruisong.basestationmap.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.model.Poi;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchBsResultAdapter;
import com.wenruisong.basestationmap.adapter.SearchCellResultAdapter;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;

import java.util.ArrayList;
import java.util.List;

public class NearbyBasestationActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView btn_back;
    private TabLayout tab;
    private ListPagerAdapter mListPagerAdapter;
    private ViewPager vp_search;
    private ArrayList<Basestation> bsSearchResults;
    private SearchBsResultAdapter searchBsResultAdapter;
    private ArrayList<Cell> cellSearchResults;
    private SearchCellResultAdapter searchCellResultAdapter;
    private List<ListView> mListViews= new ArrayList<>();
    ListView nearbyCells,nearbyBasestations;
    private Poi centerPoi;
    private String[] mTitle = new String[]{"基站", "小区"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_basestation);
        centerPoi = getIntent().getParcelableExtra("POI");
        LayoutInflater inflater = getLayoutInflater();
        nearbyCells = (ListView) inflater.inflate(R.layout.listview_nearby, null);
        nearbyBasestations = (ListView) inflater.inflate(R.layout.listview_nearby, null);
        TextView title = (TextView)findViewById(R.id.activity_title);
        title.setText("("+centerPoi.getName()+"周边)");


        bsSearchResults = BasestationManager.searchNearbyBs(centerPoi.getCoordinate());
        cellSearchResults = BasestationManager.searchNearbyCells(centerPoi.getCoordinate());
        searchBsResultAdapter = new SearchBsResultAdapter(this,bsSearchResults,true);
        searchBsResultAdapter.setTargetPoi(centerPoi);
        searchCellResultAdapter = new SearchCellResultAdapter(this,cellSearchResults,true);
        searchCellResultAdapter.setTargetPoi(centerPoi);
        nearbyCells.setAdapter(searchCellResultAdapter);
        nearbyBasestations.setAdapter(searchBsResultAdapter);
        mListViews.add(nearbyBasestations);
        mListViews.add(nearbyCells);


        mListPagerAdapter = new ListPagerAdapter();
        vp_search = (ViewPager) findViewById(R.id.vp_search);
        vp_search.setOffscreenPageLimit(2);
        vp_search.setAdapter(mListPagerAdapter);

        tab = (TabLayout) findViewById(R.id.tab_layout);
        tab.setOnTabSelectedListener(mTabListener);
        tab.setupWithViewPager(vp_search);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(tab);
        vp_search.addOnPageChangeListener(listener);
    }

    @Override
    public void onClick(View v) {

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


    class ListPagerAdapter extends PagerAdapter {
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
            container.addView(mListViews.get(position), 0);//添加页卡
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return  mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;//官方提示这样写
        }
    }


}
