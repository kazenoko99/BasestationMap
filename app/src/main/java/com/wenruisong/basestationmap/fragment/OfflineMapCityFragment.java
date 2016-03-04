package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.OfflineCityListAdapter;
import com.wenruisong.basestationmap.map.OfflineMapManager;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;

public class OfflineMapCityFragment extends BackPressHandledFragment {
    private MKOfflineMap mOffline = null;
    private ListView hotCityListView;
    private ListView mCityListView;
    private ListView mSearchResultListView;
    private AutoCompleteTextView mSearchView;
    private OfflineCityListAdapter mCityAdapter;
    private OfflineCityListAdapter mHotCityAdapter;
    private OfflineCityListAdapter mSearchAdapter;
    ArrayList<MKOLUpdateElement> hotCities = new ArrayList<>();
    ArrayList<MKOLUpdateElement> cities = new ArrayList<>();
    ArrayList<MKOLUpdateElement> searchResultCities = new ArrayList<>();
    public static BaseFragment getInstance(Bundle bundle) {
        Logs.d("OfflineMapModule", "OfflineMapCityFragment getInstance");

        BaseFragment backPressHandledFragment = new OfflineMapCityFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = initView(inflater);
        mOffline = OfflineMapManager.getInstance().mOffline;
        addHotCityList();
        addAllCityList();
        setListener();
        updateCityList();

        return root;
    }



    private View initView(LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.fragment_offline_map_city, null);
        hotCityListView = (ListView) root.findViewById(R.id.hotcitylist);
        mCityListView = (ListView) root.findViewById(R.id.allcitylist);
        mSearchView = (AutoCompleteTextView) root.findViewById(R.id.searchview);
        mSearchResultListView = (ListView) root.findViewById(R.id.lv_search_result);

        mSearchView.setHint(ResourcesUtil.getString(R.string.offline_map_city_search_hint));
        mSearchView.addTextChangedListener(watcher);
        mSearchAdapter = new OfflineCityListAdapter(getActivity(), searchResultCities );
        mSearchResultListView.setAdapter(mSearchAdapter);
        return root;
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addSearchCityList(s.toString());
            Toast.makeText(getActivity(), "yes changed"+s, Toast.LENGTH_SHORT).show();
            if(searchResultCities!=null && searchResultCities.size()>0)
            {
                mSearchResultListView.setVisibility(View.VISIBLE);
            }
            else
                mSearchResultListView.setVisibility(View.GONE);
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

    private void setListener() {

    }


    private void addHotCityList() {
        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records1) {
                MKOLUpdateElement element = new MKOLUpdateElement();
                element.cityName = r.cityName;
                element.cityID = r.cityID;
                element.size = r.size;
                hotCities.add(element);
            }
        }
        mHotCityAdapter = new OfflineCityListAdapter(getActivity(), hotCities );
        hotCityListView.setAdapter(mHotCityAdapter);
    }

    private void addAllCityList() {
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records2 != null) {
            for (MKOLSearchRecord r : records2) {
                MKOLUpdateElement element = new MKOLUpdateElement();
                element.cityName = r.cityName;
                element.cityID = r.cityID;
                element.size = r.size;
                cities.add(element);
            }
        }
        mCityAdapter = new OfflineCityListAdapter(getActivity(), cities );
        mCityListView.setAdapter(mCityAdapter);
    }

    private void addSearchCityList(String City) {
        searchResultCities.clear();
        ArrayList<MKOLSearchRecord> records3 = mOffline.searchCity(City.toString());
        if (records3 != null) {
            for (MKOLSearchRecord r : records3) {
                MKOLUpdateElement element = new MKOLUpdateElement();
                element.cityName = r.cityName;
                element.cityID = r.cityID;
                element.size = r.size;
                searchResultCities.add(element);
            }

            mSearchAdapter.notifyDataSetChanged();
        }
    }


    public void updateCityList() {
        mCityAdapter.notifyDataSetChanged();
        mHotCityAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        Logs.d("OfflineMapModule", "OfflineMapCityFragment onDestroy");
        super.onDestroy();
    }



    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
}
