package com.wenruisong.basestationmap.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.CsvFile;
import com.wenruisong.basestationmap.common.SearchCsv;
import com.wenruisong.basestationmap.eventbus.FileExplorerEvents;
import com.wenruisong.basestationmap.fragment.BasestationImportFragment;
import com.wenruisong.basestationmap.helper.LocationHelper;

import java.util.ArrayList;
import java.util.List;

public class BasestationImportActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private NetTypePagerAdapter netTypePagerAdapter;
    private TabLayout mTabLayout;
    BasestationImportFragment mFragmentGsm;
    BasestationImportFragment mFrangmentLte;
    private String updatePath;
    private int updateFragmentIndex;
    private TextView mCity;
    private String selectedCity = "请选择城市";
    private LinearLayout mLoadingLayout;
    private Boolean updateFlag = false;
    private String[] mTitle = new String[2];
    private ProgressBar mLoadingProgress;
    ArrayList<CsvFile> allCsvList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basestation_import);
        mViewPager = (ViewPager) findViewById(R.id.vp_container);
  
        mTitle = new String[]{
                "GSM",
                "TD-LTE"
        };
        mCity = (TextView)findViewById(R.id.city);
        if(LocationHelper.getInstance().location!=null) {
            selectedCity = LocationHelper.getInstance().location.getCity();
            mCity.setText(selectedCity);
        } else {
            mCity.setText("请选择城市");
        }
        mFragmentGsm = BasestationImportFragment.newInstance(selectedCity, "GSM");
        mFrangmentLte = BasestationImportFragment.newInstance(selectedCity, "LTE");
        mFragments.add(mFragmentGsm);
        mFragments.add(mFrangmentLte);
        netTypePagerAdapter = new NetTypePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(netTypePagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mLoadingProgress = (ProgressBar)findViewById(R.id.progress);
        mLoadingLayout = (LinearLayout)findViewById(R.id.loading_layout) ;
        mTabLayout.setOnTabSelectedListener(mTabListener);
        mTabLayout.setupWithViewPager(mViewPager);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
        mViewPager.addOnPageChangeListener(listener);


        mCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasestationImportActivity.this, ChooseCityActivity.class);
                startActivityForResult(intent,1);
            }
        });
        LoadCsvTask loadCsvTask = new LoadCsvTask();
        loadCsvTask.execute();
        mLoadingProgress.animate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == RESULT_OK) {
           selectedCity = data.getStringExtra("CITY");
           mCity.setText(selectedCity);
           mFragmentGsm.setCity(selectedCity);
           mFrangmentLte.setCity(selectedCity);
       }
    }
    @Override
    public void onResume() {
        super.onResume();
        FileExplorerEvents.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FileExplorerEvents.getBus().unregister(this);
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


    class NetTypePagerAdapter extends FragmentStatePagerAdapter {
        FragmentManager fm;

        public NetTypePagerAdapter(FragmentManager fm) {
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    private class LoadCsvTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            SearchCsv.getCsvFile(allCsvList, Environment.getExternalStorageDirectory());// 获得视频文件
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFragmentGsm.setDates(allCsvList);
            mFrangmentLte.setDates(allCsvList);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }
    
}
