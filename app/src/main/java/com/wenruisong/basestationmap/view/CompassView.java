package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import com.wenruisong.basestationmap.R;

import java.text.DecimalFormat;
import java.util.Locale;


public class CompassView extends View {
    private static final DecimalFormat mDegreeFormat = new DecimalFormat("##0\u00B0");
    private int mCenterX;

    private float mDegree;
    private String mDegreeTxt = "";


    private static final String[] mStringDirection = new String[4];
    // the compass degree text markers
    private static final String[] mStringDegrees = new String[12];

    static {
        for (int i = 0; i < 4; i++) {
            mStringDegrees[i] = " " + ((i * 90) + "\u00B0");
        }
    }

    private String mCenterText = "";
    private boolean mChinese;

    private String mLatitudeText = "";
    private String mLongitudeText = "";

    private String mLatitudeIndicator = "";
    private String mLongitudeIndicator = "";

    private Paint mPaintCenterIndicator;
    private Paint mPaintCenterIndicator2;
    private Paint mPaintCenterDirection;
    private Paint mPaintCenterDirection2;
    private Paint mPaintBounderDirection;
    private Paint mPaintLocation;
    private Drawable mRotateBounder;
    private String W;
    private String E;
    private String N;
    private String S;
    private String NE;
    private String NW;
    private String SE;
    private String SW;
    private Drawable mReferenceLine;
    private int mReferenceY;

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeStrings();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    private void initializeStrings() {
        mStringDirection[0] = getResources().getString(R.string.direction_north);
        mStringDirection[1] = getResources().getString(R.string.direction_east);
        mStringDirection[2] = getResources().getString(R.string.direction_south);
        mStringDirection[3] = getResources().getString(R.string.direction_west);
        mChinese = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");
        W = getResources().getString(R.string.direction_due_west);
        E = getResources().getString(R.string.direction_due_east);
        N = getResources().getString(R.string.direction_due_north);
        S = getResources().getString(R.string.direction_due_south);
        NE = getResources().getString(R.string.direction_north_east);
        NW = getResources().getString(R.string.direction_north_west);
        SE = getResources().getString(R.string.direction_south_east);
        SW = getResources().getString(R.string.direction_south_west);
    }

    private void init() {
        mCenterX = getWidth() / 2;
        //初始化画笔
        mPaintCenterIndicator = new Paint();
        mPaintCenterIndicator.setTextSize(sp2px(28));
        mPaintCenterIndicator.setAntiAlias(true);
        mPaintCenterIndicator.setColor(0xFFF15238);

        mPaintCenterDirection = new Paint();
        mPaintCenterDirection.setTextSize(sp2px(14));
        mPaintCenterDirection.setAntiAlias(true);
        mPaintCenterDirection.setColor(0xFFF15238);

        mPaintBounderDirection = new Paint();
        mPaintBounderDirection.setTextSize(sp2px(18));
        mPaintBounderDirection.setAntiAlias(true);
        mPaintBounderDirection.setColor(0xFFF15238);

        mPaintLocation = new Paint();
        mPaintLocation.setTextSize(sp2px(18));
        mPaintLocation.setTextAlign(Paint.Align.CENTER);
        mPaintLocation.setAntiAlias(true);
        mPaintLocation.setColor(0xFFFFFF);

        mRotateBounder = getResources().getDrawable(R.drawable.compass_boundary);
        mRotateBounder.setBounds(0, 0, mRotateBounder.getIntrinsicWidth(), mRotateBounder.getIntrinsicHeight());
        mReferenceLine = getResources().getDrawable(R.drawable.compass_reference_line);
        mReferenceLine.setBounds(0, 0, mReferenceLine.getIntrinsicWidth(), mReferenceLine.getIntrinsicHeight());
        mReferenceY = getResources().getDimensionPixelOffset(R.dimen.compass_content_margin_top) + mRotateBounder.getIntrinsicHeight() / 2;
    }

    public void updateDegree(float degree) {
        mDegree = degree;
        mDegreeTxt = mDegreeFormat.format(mDegree);
        calculateOrientation(degree);
        postInvalidate();
    }

    private void calculateOrientation(float degree) {

        if (degree >= 355 || degree < 5) {
            mCenterText = N;
        } else if (degree >= 5 && degree < 85) {
            mCenterText = NE;
        } else if (degree >= 85 && degree <= 95) {
            mCenterText = E;
        } else if (degree >= 95 && degree < 175) {
            mCenterText = SE;
        } else if ((degree >= 175 && degree <= 185)) {
            mCenterText = S;
        } else if (degree >= 185 && degree < 265) {
            mCenterText = SW;
        } else if (degree >= 265 && degree < 275) {
            mCenterText = W;
        } else if (degree >= 275 && degree < 355) {
            mCenterText = NW;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float startY = mReferenceY - mRotateBounder.getIntrinsicHeight() / 2;
        //参考线
        canvas.save();
        canvas.translate(mCenterX - mReferenceLine.getIntrinsicWidth() / 2, startY - mReferenceLine.getIntrinsicHeight() - dp2px(9));
        mReferenceLine.draw(canvas);
        canvas.restore();
        //画外环
        canvas.save();
        canvas.rotate(-mDegree, mCenterX, mReferenceY);
        canvas.translate(mCenterX - mRotateBounder.getIntrinsicWidth() / 2, mReferenceY - mRotateBounder.getIntrinsicHeight() / 2);
        mRotateBounder.draw(canvas);
        canvas.restore();
        //画外环文字
        canvas.save();
        float textHeight = ((mPaintBounderDirection.descent() - mPaintBounderDirection.ascent()) / 2) * 2 - mPaintBounderDirection.descent();
        for (int i = 0; i < 4; i++) {
            float width = mPaintBounderDirection.measureText(mStringDirection[i]);
            canvas.rotate(-mDegree + i * 90, mCenterX, mReferenceY);
            canvas.drawText(mStringDirection[i], mCenterX - width / 2, startY + dp2px(39) + textHeight, mPaintBounderDirection);
            canvas.rotate(-1 * (-mDegree + i * 90), mCenterX, mReferenceY);
        }
        canvas.restore();
        //画中心度数和文字
        textHeight = ((mPaintCenterIndicator.descent() - mPaintCenterIndicator.ascent()) / 2) * 2 - mPaintCenterIndicator.descent();
        float width = mPaintCenterIndicator.measureText(mDegreeTxt);
        canvas.drawText(mDegreeTxt, mCenterX - width / 2, startY + (dp2px(130) + textHeight), mPaintCenterIndicator);

        textHeight = ((mPaintCenterDirection.descent() - mPaintCenterDirection.ascent()) / 2) * 2 - mPaintCenterDirection.descent();
        width = mPaintCenterDirection.measureText(mCenterText);
        canvas.drawText(mCenterText, mCenterX - width / 2, startY + (dp2px(162)) + textHeight, mPaintCenterDirection);

        //画经纬度
        textHeight = ((mPaintLocation.descent() - mPaintLocation.ascent()) / 2) * 2 - mPaintLocation.descent();
        final float textStarY = startY + mRotateBounder.getIntrinsicHeight() + dp2px(80) + textHeight;
        final float indicatStartY = textStarY + textHeight + dp2px(10);
        final float latTextWidth = mPaintLocation.measureText(mLatitudeText);
        final float latIndicatorWidth = mPaintLocation.measureText(mLatitudeIndicator);
        final float lonTextWidth = mPaintLocation.measureText(mLongitudeText);
        final float lonIndicatorWidth = mPaintLocation.measureText(mLongitudeIndicator);
        float latStartX = mCenterX - dp2px(35) - latIndicatorWidth / 2;
        float lonStartX = mCenterX + dp2px(35) + lonIndicatorWidth / 2;
        mPaintLocation.setAlpha(255);
        canvas.drawText(mLatitudeIndicator, latStartX, indicatStartY, mPaintLocation);
        canvas.drawText(mLongitudeIndicator, lonStartX, indicatStartY, mPaintLocation);
        mPaintLocation.setAlpha(128);
        canvas.drawText(mLatitudeText, latStartX, textStarY, mPaintLocation);
        canvas.drawText(mLongitudeText, lonStartX, textStarY, mPaintLocation);

    }

    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    public float sp2px(float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    public void updateLocation(double latitude, double longitude) {
        if (latitude >= 0.0f) {
            mLatitudeText = getResources().getString(R.string.location_north);
            mLatitudeIndicator = getLocationString(latitude);
        } else {
            mLatitudeText = getResources().getString(R.string.location_south);
            mLatitudeIndicator = getLocationString(-1.0 * latitude);
        }

        if (longitude >= 0.0f) {
            mLongitudeText = getResources().getString(R.string.location_east);
            mLongitudeIndicator = getLocationString(longitude);
        } else {
            mLongitudeText = getResources().getString(R.string.location_west);
            mLongitudeIndicator = getLocationString(-1.0 * longitude);
        }

    }


    private String getLocationString(double input) {
        int du = (int) input;
        int fen = (((int) ((input - du) * 3600))) / 60;
        int miao = (((int) ((input - du) * 3600))) % 60;
        return String.valueOf(du) + "°" + String.valueOf(fen) + "'" + String.valueOf(miao) + "\"";
    }

}
