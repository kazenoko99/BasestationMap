package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.model.SearchHistoryItem;
import com.wenruisong.basestationmap.utils.Constants;

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
    private SuggestionResult mSuggestionResult;
    private int mType;
    private static final String TYPE = "TYPE";
    public static SearchPoiFragment newInstance(int param1) {
        SearchPoiFragment fragment = new SearchPoiFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchPoiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE);
        }
    }

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

        if(mType == 0) {
            poi_result_listView.setOnItemClickListener(normalSearchClick);
        } else {
            poi_result_listView.setOnItemClickListener(placePickClick);
        }
        return  v;
    }

    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty())
         mSuggestionSearch
                .requestSuggestion(suggestionSearchOption.keyword(query));
    }

    private AdapterView.OnItemClickListener normalSearchClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SuggestionResult.SuggestionInfo suggestionInfo = mSuggestionResult.getAllSuggestions().get(position);
            saveSearchAction(suggestionInfo);
            SearchResultEvents.getBus().post(new SearchResultEvents.OnPoiClick(suggestionInfo.pt,suggestionInfo.key,suggestionInfo.district));
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener placePickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SuggestionResult.SuggestionInfo suggestionInfo = mSuggestionResult.getAllSuggestions().get(position);

            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", suggestionInfo.key);
            bundle.putDouble("LAT",suggestionInfo.pt.latitude);
            bundle.putDouble("LNG",suggestionInfo.pt.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(3,intent);
            getActivity().finish();
        }
    };

    private void saveSearchAction( SuggestionResult.SuggestionInfo suggestionInfo){
        SearchHistoryItem searchHistoryItem = new SearchHistoryItem();
        searchHistoryItem.address = suggestionInfo.district;
        searchHistoryItem.keyword = suggestionInfo.key;
        searchHistoryItem.latLng = suggestionInfo.pt;
        searchHistoryItem.searchtype = Constants.SEARCH_TYPE_POI;
        searchHistoryItem.time =Long.toString(System.currentTimeMillis());
        SearchHistorySqliteHelper searchHistorySqliteHelper = new SearchHistorySqliteHelper(getActivity());
        searchHistorySqliteHelper.insertSearchResult(searchHistoryItem);
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
        mSuggestionResult = suggestionResult;
        searchPoiResultAdapter.setDatas(mSuggestionResult);
    }
}
