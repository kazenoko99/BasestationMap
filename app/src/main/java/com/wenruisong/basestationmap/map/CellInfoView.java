package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wenruisong.basestationmap.basestation.Cell;


/**
 * Created by wuxuexiang on 15-6-24.
 */
abstract class CellInfoView {
    Context mContext;
    TextView cellid;
    TextView cellname;
    TextView celllat;
    TextView celllng;
    TextView cellhigth;
    TextView cellazumith;
    TextView cellisshifen;

     abstract void initWidget(Cell cell);

}
