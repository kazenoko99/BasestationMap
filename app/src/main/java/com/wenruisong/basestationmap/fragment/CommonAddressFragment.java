package com.wenruisong.basestationmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.MapCoreActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.SearchActivity;
import com.wenruisong.basestationmap.adapter.CommonAddressAdapter;
import com.wenruisong.basestationmap.database.CommonAddressSqliteHelper;
import com.wenruisong.basestationmap.eventbus.CommonAddressPickEvents;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.offlinemap.AynscWorkListener;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.view.CommonAddressView;

import java.util.ArrayList;
import java.util.List;


public class CommonAddressFragment extends BackPressHandledFragment implements CommonAddressView.CommonAddressOnClickListener {
    private static final String TAG = CommonAddressFragment.class.getSimpleName();
    private FloatingActionButton mAddCommonAddress;
    private CommonAddressView mCommonAddressView;
    private RecyclerView mCommonAddressRecyclerView;
    private CommonAddressSqliteHelper mCommonAddressSqliteHelper;
    private CommonAddressAdapter mCommonAddressAdapter;
    private List<CommonAddress> mCommonAddressList = new ArrayList<>();

    public static final int CODE_ADD_COMMON_ADDRESS = 0x0055;

    public static BackPressHandledFragment getInstance(Bundle bundle) {
        BackPressHandledFragment backPressHandledFragment = new CommonAddressFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonAddressPickEvents.getBus().register(this);
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_common_address, null);
        final MainActivity mMainActivity = (MainActivity)getActivity();
        mCommonAddressRecyclerView = (RecyclerView)root.findViewById(R.id.common_address_list);
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        mToolbar.setTitle("收藏夹");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mMainActivity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.mz_titlebar_ic_list);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMainActivity.openOrCloseDrawers();
            }
        });
        mCommonAddressView = (CommonAddressView) root.findViewById(R.id.common_address);
        mCommonAddressView.setCommonAddressOnClickListener(this);
        mCommonAddressSqliteHelper = new CommonAddressSqliteHelper(getContext());
        mCommonAddressView.setDatebase(mCommonAddressSqliteHelper);


        doWorkAynsc(new AynscWorkListener() {
            @Override
            public void onPreExcuted() {

            }

            @Override
            public Object doInBackground() {
                return mCommonAddressView.getAddressData();
            }

            @Override
            public void onPostResult(Object obj) {
                mCommonAddressView.updateAddress((List<CommonAddress>) obj);
            }

            @Override
            public void onPostExcuted() {
            }
        });
        mCommonAddressList = mCommonAddressSqliteHelper.queryCommonAddress();
        mCommonAddressAdapter = new CommonAddressAdapter(getContext(),mCommonAddressList);
        mCommonAddressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommonAddressRecyclerView.setAdapter(mCommonAddressAdapter);
        mCommonAddressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAddCommonAddress = (FloatingActionButton)root.findViewById(R.id.btnFloatingAction);
        mAddCommonAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SearchActivity.class);
                //intent.setAction(action);
                intent.putExtra(Constants.TYPE, Constants.CommonPlacePic);
                getActivity().startActivityForResult(intent, CODE_ADD_COMMON_ADDRESS);
            }
        });
        return root;
    }

    @Subscribe
    public void OnAddressPick(CommonAddressPickEvents.OnAddressPick event) {
        mCommonAddressSqliteHelper.insertCommonAddress(  event.mCommonAddress);
        mCommonAddressList.add(  event.mCommonAddress);
        mCommonAddressAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackResult(Bundle bundle) {
        super.onBackResult(bundle);
        Logs.d(TAG, TAG + " : onBackResult()");

//        List<PoiItem> list = bundle.getParcelableArrayList(Constants.FRAG_SEARCH_RESULT_ITEMS);
//        if ((list != null) && (list.size() > 0)) {
//            mCommonAddressView.updateAddress(list.get(0));
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonAddressPickEvents.getBus().unregister(this);
    }

    @Override
    public void onAddressClick(CommonAddress address) {
        Logs.d(TAG, TAG + " : onAddressClick()");
//        LatLonPoint point = new LatLonPoint(address.getLatitude(), address.getLongitude());
//        PoiItem poi = new PoiItem("id", point, address.getIdentification(), address.getAddress());
//
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.RESULT_MAP_SHOW_TYPE, Constants.RESULT_TYPE_COMMON_ADDRESS);
//        bundle.putParcelable(Constants.RESULT_COMMON_ADDRESS_POI, poi);
        getFragShower().showFragments(MapCoreActivity.FRAG_MAP_VIEW, true, false, false, null); // #222515
    }
}




