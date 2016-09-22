package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created by wen on 2016/7/14.
 */

public class ShortCutGridView extends GridView {

    private int mClickPosition = -1;

    public ShortCutGridView(Context context) {
        super(context);
    }

    public ShortCutGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShortCutGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写dispatchTouchEvent方法禁止GridView滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            mClickPosition = pointToPosition(x, y);
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int position = pointToPosition(x, y);
            if(mClickPosition != position) {
                onWindowFocusChanged(false);
                return super.dispatchTouchEvent(ev);
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            onWindowFocusChanged(false);//移动时，失去焦点
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View childView = getChildAt(0);
        int column = getWidth() / childView.getWidth();
        int childCount = getChildCount();
        Paint paint;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x19000000);
        for (int i = 0; i < childCount; i++) {
            View cellView = getChildAt(i);
            //靠右边的不画右边线
            if ((i + 1) % column == 0) {
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), paint);
                //靠底部的不画下边线
            } else if ((i + 1) > (childCount - (childCount % column))) {
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), paint);
            } else {
                //画右边线
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), paint);
                //画下边线
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), paint);
            }
        }
        if (childCount % column != 0) {
            for (int j = 0; j < (column - childCount % column); j++) {
                View lastView = getChildAt(childCount - 1);
                canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth() * j, lastView.getBottom(), paint);
            }

        }
    }
}
