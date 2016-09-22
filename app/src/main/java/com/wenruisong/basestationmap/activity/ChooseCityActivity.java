package com.wenruisong.basestationmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.CityExpandableListAdapter;
import com.wenruisong.basestationmap.adapter.CitySearchResultAdapter;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;
import java.util.List;

public class ChooseCityActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ListView mSearchResultListView;
    private EditText mSearchView;
    private CitySearchResultAdapter mSearchAdapter;
    private CityExpandableListAdapter mCityAdapter;
    private OfflineMapManager mOfflineMapManager;
    private List<OfflineMapProvince> mProvinceList;
    private List<OfflineMapCity> mSearchResultList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        expandableListView = (ExpandableListView) findViewById(R.id.list);
        mSearchView = (EditText) findViewById(R.id.et_search);
        mSearchResultListView = (ListView) findViewById(R.id.lv_search_result);

        mSearchView.setHint(ResourcesUtil.getString(R.string.offline_map_city_search_hint));
        mSearchView.addTextChangedListener(watcher);
        mOfflineMapManager = new OfflineMapManager(this,null);
        mProvinceList = mOfflineMapManager.getOfflineMapProvinceList();

        mCityAdapter = new CityExpandableListAdapter(this , mProvinceList);
        expandableListView.setAdapter(mCityAdapter);
        mSearchAdapter = new CitySearchResultAdapter(this, mSearchResultList);
        mSearchResultListView.setAdapter(mSearchAdapter);

        // 点击列表则隐藏光标并关闭软键盘
        mSearchResultListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchView.setCursorVisible(false);

                // 关闭系统软键盘
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return false;
            }
        });

        mSearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OfflineMapCity mapCity =  mSearchResultList.get(position);
                Intent intent = new Intent();
                intent.putExtra("CITY",mapCity.getCity());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                OfflineMapCity offlineMapCity = mProvinceList.get(groupPosition).getCityList().get(childPosition);
                Intent intent = new Intent();
                intent.putExtra("CITY",offlineMapCity.getCity());
                setResult(RESULT_OK, intent);
                finish();

                return false;
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String key = s.toString();
            mSearchResultList.clear();
            if (!key.isEmpty()) {
                for (int i = 0; i < mProvinceList.size(); i++) {
                    List<OfflineMapCity> citys = mProvinceList.get(i).getCityList();

                    for (OfflineMapCity city : citys) {
                        if (city.getCity().contains(key)) {
                            mSearchResultList.add(city);
                        }
                    }
                }
                if(mSearchResultList.size()>0){
                    expandableListView.setVisibility(View.GONE);
                    mSearchResultListView.setVisibility(View.VISIBLE);
                } else {
                    expandableListView.setVisibility(View.VISIBLE);
                    mSearchResultListView.setVisibility(View.GONE);
                    mSearchAdapter.notifyDataSetChanged();
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

}
