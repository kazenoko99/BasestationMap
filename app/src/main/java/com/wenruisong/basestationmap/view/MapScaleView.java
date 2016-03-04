package com.wenruisong.basestationmap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;


/**
 * 地图比例尺
 * padding不影响线的位置
 */
public class MapScaleView extends TextView {

    private Drawable mScaleIcon;
    private int mScaleIconWidth;

    public MapScaleView(Context context) {
        this(context, null);
    }

    public MapScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("NewApi")
    public MapScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    @SuppressLint("NewApi")
    private void initView() {
        setSingleLine();
//        setGravity(Gravity.CENTER_HORIZONTAL);
        setScaleDrawable(getResources().getDrawable(R.drawable.map_scale_line));
    }

    public void setScaleDrawable(Drawable scaleIcon) {
        if (scaleIcon != mScaleIcon) {
            mScaleIcon = scaleIcon;
            if (mScaleIcon != null) {
                mScaleIconWidth = mScaleIcon.getIntrinsicWidth();
            } else {
                mScaleIconWidth = 0;
            }
            invalidate();
        }
    }

    public Drawable getScaleDrawable() {
        return mScaleIcon;
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == mScaleIcon) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    public void setScaleDrawableWidth(int pixels) {
        mScaleIconWidth = pixels;
        requestLayout();
        invalidate();
    }

    public int getScaleDrawableWidth() {
        return mScaleIconWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mScaleIcon != null) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            if (widthMode != MeasureSpec.EXACTLY) {
                int widthSize = getMeasuredWidth();
                int heightSize = getMeasuredHeight();

                int width = Math.max(widthSize, mScaleIconWidth);
                setMeasuredDimension(width, heightSize);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mScaleIcon != null) {
            canvas.save();
            int scaleIconheight = mScaleIcon.getIntrinsicHeight();
            int dx = /*getPaddingLeft() + */getScrollX();
            int dy = getBottom() - getTop() - scaleIconheight + getScrollY();
            canvas.translate(dx, dy);
            mScaleIcon.setBounds(0, 0, mScaleIconWidth, scaleIconheight);
            mScaleIcon.draw(canvas);
            canvas.restore();
        }
    }

}
