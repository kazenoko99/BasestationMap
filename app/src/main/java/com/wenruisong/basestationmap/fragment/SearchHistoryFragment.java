package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchHistoryAdapter;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.model.SearchHistoryItem;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;


public class SearchHistoryFragment extends BaseFragment implements SearchHistoryAdapter.OnItemClickListener{

    private SearchHistorySqliteHelper searchHistorySqliteHelper;
   // private ArrayList<CommonAddress> mCommonAddresses = new ArrayList<>();
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RecyclerView mSearchHistyRecylerView;


    private ArrayList searchHistory;
    private int mType;
    private static final String TYPE = "TYPE";
    public static SearchHistoryFragment newInstance(int param1) {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchHistoryFragment() {
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
        View v = inflater.inflate(R.layout.fragment_search_history, container, false);
        mSearchHistyRecylerView = (RecyclerView)v.findViewById(R.id.search_history);
        mSearchHistyRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchHistorySqliteHelper = new SearchHistorySqliteHelper(getContext());
        searchHistory = searchHistorySqliteHelper.querySearchResult();
        if(searchHistory == null){
            searchHistory = new ArrayList();
        }
        mSearchHistoryAdapter = new SearchHistoryAdapter(getContext(),this);
        mSearchHistoryAdapter.setDates(searchHistory);
        mSearchHistyRecylerView.setAdapter(mSearchHistoryAdapter);
        return  v;
    }




    private void saveSearchAction(CommonAddress commonAddress){
        SearchHistoryItem searchHistoryItem = new SearchHistoryItem();
        searchHistoryItem.address = commonAddress.address;
        searchHistoryItem.keyword = commonAddress.name;
        searchHistoryItem.latLng = commonAddress.latLng;
        searchHistoryItem.searchtype = commonAddress.addressType;
        searchHistoryItem.nettype = commonAddress.nettype;
        searchHistoryItem.cellindex = commonAddress.cellindex;
        searchHistoryItem.time =Long.toString(System.currentTimeMillis());
        SearchHistorySqliteHelper searchHistorySqliteHelper = new SearchHistorySqliteHelper(getActivity());
        searchHistorySqliteHelper.insertSearchResult(searchHistoryItem);

    }


    @Override
    public void removeItem(int position) {
        if(searchHistory!=null && searchHistory.size()!=0){
            searchHistorySqliteHelper.delSearchResult(((SearchHistoryItem)searchHistory.get(position)).id);
            searchHistory.remove(position);
            mSearchHistoryAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void clickItem(int position) {
        if(mType ==  Constants.NormalSearch) {
            if (searchHistory != null && searchHistory.size() != 0) {
                SearchHistoryItem searchHistoryItem = (SearchHistoryItem) searchHistory.get(position);
                switch (searchHistoryItem.searchtype) {
                    case Constants.SEARCH_TYPE_BASESTATION:
                        SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(searchHistoryItem.latLng, searchHistoryItem.cellindex,searchHistoryItem.nettype));
                        break;
                    case Constants.SEARCH_TYPE_CELL:
                        SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(searchHistoryItem.latLng, searchHistoryItem.cellindex,searchHistoryItem.nettype));
                        break;
                    case Constants.SEARCH_TYPE_POI:
                        SearchResultEvents.getBus().post(new SearchResultEvents.OnPoiClick(searchHistoryItem.latLng, searchHistoryItem.keyword, searchHistoryItem.address));
                        break;
                }
                getActivity().finish();
            }
        } else if (mType ==  Constants.RoutePicPlace){
            SearchHistoryItem searchHistoryItem =(SearchHistoryItem)searchHistory.get(position);
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", searchHistoryItem.keyword);
            bundle.putDouble("LAT",searchHistoryItem.latLng.latitude);
            bundle.putDouble("LNG",searchHistoryItem.latLng.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(2,intent);
            getActivity().finish();
        }
    }
}
