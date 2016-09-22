package com.wenruisong.basestationmap.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.wenruisong.basestationmap.R;

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
