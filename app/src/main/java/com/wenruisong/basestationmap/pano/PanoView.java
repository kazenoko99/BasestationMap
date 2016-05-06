package com.wenruisong.basestationmap.pano;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.lbsapi.panoramaview.PanoramaView;

/**
 * Created by wen on 2016/3/30.
 */
public class PanoView extends PanoramaView {


    private OnPanoViewMoveListener onPanoViewMoveListener;

    public void setOnPanoViewMoveListener(OnPanoViewMoveListener onPanoViewMoveListener) {
        this.onPanoViewMoveListener = onPanoViewMoveListener;
    }


    public PanoView(Context context) {
        super(context);
    }

    public PanoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            onPanoViewMoveListener.viewMoved();
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnPanoViewMoveListener{
        void viewMoved();
    }


}
