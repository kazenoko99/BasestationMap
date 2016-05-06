package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wenruisong.basestationmap.R;

/**
 * @author zchong@meizu.com
 */
public class LocationSeletorLayout extends LinearLayout {
//    private String mStartPointName;
//    private String mEndPointName;
    private float mTextSize;
    private int mTextColor;
    private int mStartPointId;
    private int mEndPointId;
    private int pointNum = 5;

//    private float mStartTextWidth;
//    private float mStartTextHeigh;
//    private float mEndTextWidth;
//    private float mEndTextHeigh;

    private Drawable mWalkLineDrawable;
    private float mWalkLineSpace;

    private Drawable mStartPointDrawable;
    private float mStartPointWidth;
    private float mStartPointHeight;

    private Drawable mEndPointDrawable;
    private float mEndPointWidth;
    private float mEndPointHeight;


    private boolean mStartIsCurrent;
    private boolean mEndIsCurrent;
    private TextPaint mTextPaint;

    public LocationSeletorLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public LocationSeletorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LocationSeletorLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RouteMainLayout, defStyle, 0);

//        mStartPointDrawable = a.getString(R.styleable.RouteMainLayout_startName);
//        mEndPointDrawable = a.getString(R.styleable.RouteMainLayout_endName);

        mStartPointId = a.getResourceId(R.styleable.RouteMainLayout_startId, 0);
        mEndPointId = a.getResourceId(R.styleable.RouteMainLayout_endId, 0);

        mTextColor = a.getColor(R.styleable.RouteMainLayout_pointTextColor, Color.WHITE);
        mTextSize = a.getDimension(R.styleable.RouteMainLayout_pointTextSize, 0);
        mWalkLineSpace = a.getDimension(R.styleable.RouteMainLayout_connectionLineImgSpace, 0);

        mTextSize = a.getDimension(R.styleable.RouteMainLayout_pointTextSize, 0);
        mStartPointWidth = a.getDimension(R.styleable.RouteMainLayout_startImgWidth, 0);
        mStartPointHeight = a.getDimension(R.styleable.RouteMainLayout_startImgHeight, 0);
        mEndPointWidth = a.getDimension(R.styleable.RouteMainLayout_endImgWidth, 0);
        mEndPointHeight = a.getDimension(R.styleable.RouteMainLayout_endImgHeight, 0);
        if (a.hasValue(R.styleable.RouteMainLayout_connectionLineImg)) {
            mWalkLineDrawable = a.getDrawable(
                    R.styleable.RouteMainLayout_connectionLineImg);
            mWalkLineDrawable.setCallback(this);
        }

        if (a.hasValue(R.styleable.RouteMainLayout_startImg)) {
            mStartPointDrawable = a.getDrawable(
                    R.styleable.RouteMainLayout_startImg);
            mStartPointDrawable.setCallback(this);
        }

        if (a.hasValue(R.styleable.RouteMainLayout_endImg)) {
            mEndPointDrawable = a.getDrawable(
                    R.styleable.RouteMainLayout_endImg);
            mEndPointDrawable.setCallback(this);
        }
        a.recycle();

        mStartIsCurrent = true;
        mEndIsCurrent = false;
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        setWillNotDraw(false);
//        invalidateTextPaintAndMeasurements();
    }

    public void updateStartIcon(boolean startIsCurrent){
        if(startIsCurrent != mStartIsCurrent){
            mStartIsCurrent = startIsCurrent;
            postInvalidate();
        }
    }

    public boolean isStartIsCurrent(){
        return mStartIsCurrent;
    }

    public boolean isEndIsCurrent(){
        return mEndIsCurrent;
    }

    public void updateEndIcon(boolean endIsCurrent){
        if(endIsCurrent != mEndIsCurrent){
            mEndIsCurrent = endIsCurrent;
            postInvalidate();
        }
    }

    public void updateIconImg(boolean startIsCurrent, boolean endIsCurrent){
        boolean needUpdate = false;
        if(startIsCurrent != mStartIsCurrent){
            needUpdate = true;
            mStartIsCurrent = startIsCurrent;
        }

        if(endIsCurrent != mEndIsCurrent){
            needUpdate = true;
            mEndIsCurrent = endIsCurrent;
        }

        if(needUpdate)
            postInvalidate();
    }


    private void drawImg(Canvas canvas, Drawable drawable, int left, int top, int right, int bottom){
        if(drawable == null)
            return;
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);
    }

    private int drawStartView(Canvas canvas){
        View startView = findViewById(mStartPointId);
        float middleStartY = startView.getTop() + startView.getHeight()/2;
        int paddingLeft = getPaddingLeft();

        float centerY = middleStartY;
        int centerX = paddingLeft/2;

        if(mStartIsCurrent){
            return  drawCurrentImg(canvas, centerX, (int)centerY);
        }else{
            return drawNormalImg(canvas, centerX, (int) centerY);
        }
    }

    private int drawCurrentImg(Canvas canvas, int centerX, int centerY){
        int left = (int)(centerX - mStartPointWidth/2);
        int top = (int)(centerY - mStartPointHeight/2);
        int right = (int)(centerX + mStartPointWidth/2);
        int bottom = (int)(centerY + mStartPointHeight/2);
        drawImg(canvas, mStartPointDrawable, left, top, right, bottom);
        return bottom;
    }

    private int drawNormalImg(Canvas canvas, int centerX, int centerY){
        int left = (int)(centerX - mEndPointWidth/2);
        int top = (int)(centerY - mEndPointHeight/2);
        int right = (int)(centerX + mEndPointWidth/2);
        int bottom = (int)(centerY + mEndPointHeight/2);
        drawImg(canvas, mEndPointDrawable, left, top, right, bottom);
        return bottom;
    }

    private int drawEndView(Canvas canvas){
        View endView = findViewById(mEndPointId);
        float middleStartY = endView.getTop() + endView.getHeight()/2;
        int paddingLeft = getPaddingLeft();

        float centerY = middleStartY;
        int centerX = paddingLeft/2;

        if(mEndIsCurrent){
            return  drawCurrentImg(canvas, centerX, (int)centerY);
        }else{
            return drawNormalImg(canvas, centerX, (int)centerY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();

//        float yStart = onDrawStartText(canvas);
//        float yEnd = onDrawEndText(canvas);
        View startView = findViewById(mStartPointId);
        int yStart  = startView.getTop() + startView.getHeight()/2 + (int)mStartPointHeight/2;
        View endView = findViewById(mEndPointId);
        int yEnd = endView.getTop() + endView.getHeight()/2 - (int)mEndPointHeight/2;;

        drawStartView(canvas);
        drawEndView(canvas);


        int width = mWalkLineDrawable.getMinimumWidth();
        int height = mWalkLineDrawable.getMinimumHeight();


        int between = yEnd - yStart;
        for (int i = 0; i < pointNum; i++) {
            int left = paddingLeft / 2 - width / 2;
            int top =  yStart + between * (i + 1) /(pointNum + 1) - height / 2;
            int right = paddingLeft / 2 + width / 2;
            int bottom =  yStart + between * (i + 1) /(pointNum + 1) + height / 2;
            drawImg(canvas, mWalkLineDrawable, left, top, right, bottom);
        }

//        int begintY = (int) (yStart + mWalkLineSpace);
//        while (begintY < yEnd) {
//            int left = paddingLeft / 2 - width / 2;
//            int top =  begintY - height / 2;
//            int right = paddingLeft / 2 + width / 2;
//            int bottom =  begintY + height / 2;
//            drawImg(canvas, mWalkLineDrawable, left, top, right, bottom);
//            begintY += height;
//            begintY += mWalkLineSpace;
//        }
    }

}
