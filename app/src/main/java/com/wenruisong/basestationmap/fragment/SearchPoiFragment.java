package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchPoiResultAdapter;
import com.wenruisong.basestationmap.helper.LocationHelper;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchPoiFragment extends BaseFragment implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
    private ListView poi_result_listView;
    private SearchPoiResultAdapter searchPoiResultAdapter;
    private GeoCoder geoCoderSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private SuggestionSearchOption suggestionSearchOption;

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_result, container, false);
        poi_result_listView = (ListView)v.findViewById(R.id.result_list);
        searchPoiResultAdapter  = new SearchPoiResultAdapter(getActivity());
        poi_result_listView.setAdapter(searchPoiResultAdapter);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        suggestionSearchOption = new SuggestionSearchOption();
        if(LocationHelper.getInstance().isLocated)
        {
            String city = LocationHelper.getInstance().city;
            if(city!=null)
                suggestionSearchOption.city(city);
            else
                suggestionSearchOption.city("");
        }
        return  v;
    }

    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty())
         mSuggestionSearch
                .requestSuggestion(suggestionSearchOption.keyword(query));
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        searchPoiResultAdapter.setDatas(suggestionResult);
    }
}
