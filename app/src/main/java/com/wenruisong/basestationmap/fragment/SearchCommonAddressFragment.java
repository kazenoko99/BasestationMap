package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.maps.model.Poi;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchCommonAddressAdapter;
import com.wenruisong.basestationmap.database.CommonAddressSqliteHelper;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.eventbus.MapPlacePickEvents;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.model.SearchHistoryItem;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchCommonAddressFragment extends BaseFragment {
    private ListView commonPlaceListView;
    private ArrayList<CommonAddress> mCommonAddresses = new ArrayList<>();
    private SearchCommonAddressAdapter searchCellResultAdapter;
    private int mType;
    private static final String TYPE = "TYPE";
    public static SearchCommonAddressFragment newInstance(int param1) {
        SearchCommonAddressFragment fragment = new SearchCommonAddressFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchCommonAddressFragment() {
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
        commonPlaceListView = (ListView)v.findViewById(R.id.result_list);
        CommonAddressSqliteHelper  mCommonAddressSqliteHelper = new CommonAddressSqliteHelper(getActivity());
        mCommonAddresses = mCommonAddressSqliteHelper.queryCommonAddress();
        searchCellResultAdapter = new SearchCommonAddressAdapter(getActivity(),mCommonAddresses);
        commonPlaceListView.setAdapter(searchCellResultAdapter);

        switch (mType)
        {
            case Constants.NormalSearch:
                commonPlaceListView.setOnItemClickListener(normalSearchClick);
                break;
            case Constants.RoutePicPlace:
                commonPlaceListView.setOnItemClickListener(placePickClick);
                break;

        }
        return  v;
    }

    private AdapterView.OnItemClickListener normalSearchClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CommonAddress commonAddress = mCommonAddresses.get(position);
            saveSearchAction(commonAddress);
            Poi poi = new Poi(commonAddress.name,commonAddress.latLng,null);
            MapPlacePickEvents.getBus().post(new MapPlacePickEvents.OnPlacePick(poi));
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener placePickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CommonAddress commonAddress = mCommonAddresses.get(position);
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", commonAddress.name);
            bundle.putDouble("LAT",commonAddress.latLng.latitude);
            bundle.putDouble("LNG",commonAddress.latLng.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(2,intent);
            getActivity().finish();
        }
    };




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




}
