package com.wenruisong.basestationmap.fragment;


import android.os.Bundle;
import android.renderscript.Double2;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.OfflineDownloadAdapter;
import com.wenruisong.basestationmap.map.OfflineMapManager;

/**
 * Created by wen on 2016/3/27.
 */
public class PanoFragment extends BaseFragment {
    private PanoramaView mPanoView;
    private static final String LAT = "CELL";
    private static final String LNG = "param2";
    private Double mLat;
    private Double mLng;
    // TODO: Rename and change types and number of parameters
    public static PanoFragment newInstance(Double lat, Double lng) {
        PanoFragment fragment = new PanoFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT, lat);
        args.putDouble(LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public PanoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLat = getArguments().getDouble(LAT);
            mLng = getArguments().getDouble(LNG);
        }
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pano, null);
        mPanoView = (PanoramaView) root.findViewById(R.id.panorama);
        mPanoView.setPanorama(mLng, mLat);
        return root;
    }
}
