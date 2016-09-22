package com.wenruisong.basestationmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchPoiResultAdapter;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.model.SearchHistoryItem;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.ToastUtil;

import java.util.List;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchPoiFragment extends BaseFragment implements PoiSearch.OnPoiSearchListener,Inputtips.InputtipsListener {
    private ListView poi_result_listView;
    private SearchPoiResultAdapter searchPoiResultAdapter;
    private int mType;
    private static final String TYPE = "TYPE";
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private List<Tip> mTipList;
    private String city;
    private PoiResult mPoiResult;
    private Context mContext;
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

        if(LocationHelper.getInstance().isLocated)
        {
             city = LocationHelper.getInstance().city;
            if(city==null)
                city = "";
        }

        if(mType == 0) {
            poi_result_listView.setOnItemClickListener(normalSearchClick);
        } else {
            poi_result_listView.setOnItemClickListener(placePickClick);
        }

        poi_result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doSearchQuery(mTipList.get(position).getName());
            }
        });
        return  v;
    }

    protected void doSearchQuery(String keyWord) {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    public void updateDatas(String queryString)
    {
        if(queryString!=null &&!queryString.isEmpty()){
            InputtipsQuery inputquery = new InputtipsQuery(queryString, city);
            Inputtips inputTips = new Inputtips(BasestationMapApplication.getContext(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    private AdapterView.OnItemClickListener normalSearchClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           PoiItem poiItem = mPoiResult.getPois().get(position);
            saveSearchAction(poiItem);
            SearchResultEvents.getBus().post(new SearchResultEvents.OnPoiClick(AMapUtil.convertToLatLng(poiItem.getLatLonPoint()),poiItem.getTitle(),poiItem.getAdName()));
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener placePickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PoiItem poiItem = mPoiResult.getPois().get(position);

            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", poiItem.getTitle());
            bundle.putDouble("LAT",poiItem.getLatLonPoint().getLatitude());
            bundle.putDouble("LNG",poiItem.getLatLonPoint().getLongitude());
            intent.putExtras(bundle);

            getActivity().setResult(3,intent);
            getActivity().finish();
        }
    };

    private void saveSearchAction( PoiItem poiItem){
        SearchHistoryItem searchHistoryItem = new SearchHistoryItem();
        searchHistoryItem.address = poiItem.getAdName();
        searchHistoryItem.keyword = poiItem.getTitle();
        searchHistoryItem.latLng = AMapUtil.convertToLatLng(poiItem.getLatLonPoint());
        searchHistoryItem.searchtype = Constants.SEARCH_TYPE_POI;
        searchHistoryItem.time =Long.toString(System.currentTimeMillis());
        SearchHistorySqliteHelper searchHistorySqliteHelper = new SearchHistorySqliteHelper(getActivity());
        searchHistorySqliteHelper.insertSearchResult(searchHistoryItem);
    }


    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        mPoiResult = poiResult;
        List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
        List<SuggestionCity> suggestionCities = poiResult
                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            mTipList = tipList;
            searchPoiResultAdapter.setDatas(mTipList);
            searchPoiResultAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(getContext(), rCode);
        }

    }
}
