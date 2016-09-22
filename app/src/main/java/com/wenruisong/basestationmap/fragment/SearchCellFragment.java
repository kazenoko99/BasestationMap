package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchCellResultAdapter;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.eventbus.CommonAddressPickEvents;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.model.SearchHistoryItem;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchCellFragment extends BaseFragment {
    private ListView cell_result_listView;
    private ArrayList<Cell> cellSearchResults = new ArrayList<>();
    private SearchCellResultAdapter searchCellResultAdapter;
    private int mType;
    private static final String TYPE = "TYPE";
    public static SearchCellFragment newInstance(int param1) {
        SearchCellFragment fragment = new SearchCellFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchCellFragment() {
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
        cell_result_listView = (ListView)v.findViewById(R.id.result_list);
        searchCellResultAdapter = new SearchCellResultAdapter(getActivity(),cellSearchResults,false);
        cell_result_listView.setAdapter(searchCellResultAdapter);

        switch (mType)
        {
            case Constants.NormalSearch:
                cell_result_listView.setOnItemClickListener(normalSearchClick);
                break;
            case Constants.RoutePicPlace:
                cell_result_listView.setOnItemClickListener(placePickClick);
                break;
            case Constants.CommonPlacePic:
                cell_result_listView.setOnItemClickListener(commonAddressPickClick);
                break;
        }
        return  v;
    }

    private AdapterView.OnItemClickListener normalSearchClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cell cell = cellSearchResults.get(position);
            saveSearchAction(cell);
            SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(cell.aMapLatLng,cell.index,cell.getInstanceType()));
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener placePickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cell cell = cellSearchResults.get(position);
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", cell.cellName);
            bundle.putDouble("LAT",cell.aMapLatLng.latitude);
            bundle.putDouble("LNG",cell.aMapLatLng.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(2,intent);
            getActivity().finish();
        }
    };


    private AdapterView.OnItemClickListener commonAddressPickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cell cell = cellSearchResults.get(position);
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", cell.cellName);
            bundle.putString("ADDRESS", cell.address);
            CommonAddress commonAddress = new CommonAddress();
            commonAddress.address = cell.address;
            commonAddress.name = cell.bsName;
            commonAddress.latLng = cell.aMapLatLng;
            commonAddress.gpsLatLng = cell.latLng;
            CommonAddressPickEvents.getBus().post(new CommonAddressPickEvents.OnAddressPick(commonAddress));

            getActivity().finish();
        }
    };

    private void saveSearchAction(Cell cell){
        SearchHistoryItem searchHistoryItem = new SearchHistoryItem();
        searchHistoryItem.address = cell.address;
        searchHistoryItem.keyword = cell.cellName+"(小区)";
        searchHistoryItem.latLng = cell.aMapLatLng;
        searchHistoryItem.searchtype = Constants.SEARCH_TYPE_CELL;
        searchHistoryItem.nettype = cell.getInstanceType();
        searchHistoryItem.cellindex = cell.index;
        searchHistoryItem.time =Long.toString(System.currentTimeMillis());
        SearchHistorySqliteHelper searchHistorySqliteHelper = new SearchHistorySqliteHelper(getActivity());
        searchHistorySqliteHelper.insertSearchResult(searchHistoryItem);

    }

    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty()) {
            cellSearchResults = BasestationManager.searchCellsByName(query);
            searchCellResultAdapter.setDatas(cellSearchResults);
        }
    }


}
