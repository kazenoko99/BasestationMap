package com.wenruisong.basestationmap.fragment;

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
public class SearchBsFragment extends BaseFragment{
    private ListView cell_result_listView;
    private ArrayList<Basestation> bsSearchResults = new ArrayList<>();
    private SearchBsResultAdapter searchBsResultAdapter;

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_result, container, false);
        cell_result_listView = (ListView)v.findViewById(R.id.result_list);
        searchBsResultAdapter = new SearchBsResultAdapter(getActivity(),bsSearchResults);
        cell_result_listView.setAdapter(searchBsResultAdapter);
        cell_result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Basestation bs = bsSearchResults.get(position);
                SearchResultEvents.getBus().post(new SearchResultEvents.OnCellClick(bs.baiduLatLng,bs.cellIndex));
                getActivity().finish();
            }
        });
        return  v;
    }

    public void updateDatas(String query)
    {
        if(query!=null &&!query.isEmpty()) {
            bsSearchResults = BasestationManager.searchBsByName(query);
            searchBsResultAdapter.setDatas(bsSearchResults);
        }
    }

}
