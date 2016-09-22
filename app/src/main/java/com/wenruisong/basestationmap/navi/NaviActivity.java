package com.wenruisong.basestationmap.navi;

import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.wenruisong.basestationmap.R;

public class NaviActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }


}