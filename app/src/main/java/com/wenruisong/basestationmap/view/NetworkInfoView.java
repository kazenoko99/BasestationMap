package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/9/7.
 */
public class NetworkInfoView extends LinearLayout
{
    public NetworkInfoView(Context context) {
        super(context);
    }

    public NetworkInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.network_test_chance, this, true);
    }

    public NetworkInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
