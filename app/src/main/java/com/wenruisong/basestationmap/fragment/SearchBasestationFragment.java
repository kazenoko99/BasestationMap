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
import com.wenruisong.basestationmap.adapter.SearchBsResultAdapter;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/21.
 */
public class SearchBasestationFragment extends BaseFragment{

    private int mType;
    private static final String TYPE = "TYPE";
    public static SearchBasestationFragment newInstance(int param1) {
        SearchBasestationFragment fragment = new SearchBasestationFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchBasestationFragment() {
        // Required empty public constructor
    }

    private ListView cell_result_listView;
    private ArrayList<Basestation> bsSearchResults = new ArrayList<>();
    private SearchBsResultAdapter searchBsResultAdapter;

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
        searchBsResultAdapter = new SearchBsResultAdapter(getActivity(),bsSearchResults);
        cell_result_listView.setAdapter(searchBsResultAdapter);
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
            Basestation bs = bsSearchResults.get(position);
            SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(bs.baiduLatLng,bs.basestationIndex));
            getActivity().finish();
        }
    };

    private AdapterView.OnItemClickListener placePickClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Basestation bs = bsSearchResults.get(position);
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("NAME", bs.bsName);
            bundle.putDouble("LAT",bs.baiduLatLng.latitude);
            bundle.putDouble("LNG",bs.baiduLatLng.longitude);
            intent.putExtras(bundle);

            getActivity().setResult(2,intent);
            getActivity().finish();
        }
    };


    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty()) {
            bsSearchResults = BasestationManager.searchBsByName(query);
            searchBsResultAdapter.setDatas(bsSearchResults);
        }
    }

}
