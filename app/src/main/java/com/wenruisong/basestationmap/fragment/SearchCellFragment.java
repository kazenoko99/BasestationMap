package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.SearchCellResultAdapter;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchCellFragment extends BaseFragment implements
        OnGetGeoCoderResultListener {
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
        searchCellResultAdapter = new SearchCellResultAdapter(getActivity(),cellSearchResults);
        cell_result_listView.setAdapter(searchCellResultAdapter);

        if(mType == 0) {
            cell_result_listView.setOnItemClickListener(normalSearchClick);
        } else {
            cell_result_listView.setOnItemClickListener(placePickClick);
        }
        return  v;
    }

    private AdapterView.OnItemClickListener normalSearchClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cell cell = cellSearchResults.get(position);
            SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(cell.baiduLatLng,cell.index));
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
            bundle.putDouble("LAT",cell.baiduLatLng.latitude);
            bundle.putDouble("LNG",cell.baiduLatLng.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(2,intent);
            getActivity().finish();
        }
    };

    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty()) {
            cellSearchResults = BasestationManager.searchCellsByName(query);
            searchCellResultAdapter.setDatas(cellSearchResults);
        }
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }
}
