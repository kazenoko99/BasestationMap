package com.wenruisong.basestationmap.basestation.Marker;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;

/**
 * Created by wen on 2016/3/1.
 */
public class ServiceCellMarker {
    private static ServiceCellMarker instance;
    private ValueAnimator rippleValueAnimator;
    public static ServiceCellMarker getInstance() {
        if (instance == null)
            instance = new ServiceCellMarker();
        return instance;
    }
    private static  CircleOptions circleOptions =  new CircleOptions()
            .strokeWidth(0);
    private Circle mCircle1;
    private Circle mCircle2;
    private Circle mCircle3;
    private AMap aMap;
    private float radius1;
    private float radius2;
    private float radius3;
   int rippleFromColor = Color.parseColor("#FFF44336");
    int rippleToColor = Color.parseColor("#00FFFFFF");
    private LatLng lastPoint = new LatLng(0,0);
    int radius = 0;
    public void setShow(boolean show) {
        mShow = show;
    }

    private boolean mShow = true;
    public void setMap(AMap aMap){
        this.aMap = aMap;
    }
    public void addInMap(LatLng point) {
        if(!lastPoint.equals(point)) {
            lastPoint = point;
            clear();
            mCircle1 = aMap.addCircle(circleOptions.center(point));
            mCircle2 = aMap.addCircle(circleOptions.center(point));
            mCircle3 = aMap.addCircle(circleOptions.center(point));
            show();
        }
    }

    public  void clear() {
        if(mCircle1!=null) {
            mCircle1.remove();
            mCircle2.remove();
            mCircle3.remove();
        }
    }

    public  void show(){
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mShow) {
                    try {
                        synchronized (this) {
                            radius = radius + 1;
                            if (radius > 60) {
                                radius = 0;
                            }
                        }
                        Message.obtain(mHandler, 0,radius,0).sendToTarget();
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//        rippleValueAnimator = ValueAnimator.ofFloat(0f, 1f);
//        rippleValueAnimator.setDuration(2000);
//        rippleValueAnimator.setRepeatMode(ValueAnimator.INFINITE);
//        rippleValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        rippleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                render((float)animation.getAnimatedValue());
//            }
//        });
//        Looper.prepare();
//        rippleValueAnimator.start();
//        Looper.loop();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            radius1 = msg.arg1;

            mCircle1.setRadius(radius1);
            mCircle1.setFillColor(Color.argb(handleFillColor(radius1), 255, 0, 0));
            radius2 = handleRadius(radius1);
            mCircle2.setRadius(radius2);
            mCircle2.setFillColor(Color.argb(handleFillColor(radius2), 255, 0, 0));
            radius3 =  handleRadius(radius2);
            mCircle3.setRadius(radius3);
            mCircle3.setFillColor(Color.argb(handleFillColor(radius3), 255, 0, 0));
        }
    };

    private void render(float value){
        radius1 = value;

        mCircle1.setRadius(radius1*60);
        mCircle1.setFillColor(evaluateTransitionColor(radius1,rippleFromColor,rippleToColor));
//        radius2 = handleRadius(radius1);
//        mCircle2.setRadius(radius2);
//        mCircle2.setFillColor(Color.argb(handleFillColor(radius2), 255, 0, 0));
//        radius3 =  handleRadius(radius2);
//        mCircle3.setRadius(radius3);
//        mCircle3.setFillColor(Color.argb(handleFillColor(radius3), 255, 0, 0));
    }

    private float handleRadius(float radius){
        float r = radius+20;
        if(r > 60)
            r = r - 60;
        return r;
    }

    private int handleFillColor(float radius){
        return (int)(2*(60-radius));
    }

    /**
     * Calculate the current color by the current fraction value.
     *
     * @param fraction The current fraction value
     * @param startValue The start color
     * @param endValue The end color
     * @return The calculate fraction color
     */
    public static int evaluateTransitionColor(float fraction, int startValue, int endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }
}
