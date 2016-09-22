package com.wenruisong.basestationmap.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/11.
 */
public class MapModePopupWindow extends RelativePopupWindow implements CheckBox.OnCheckedChangeListener {
    private static int mapMode = 0;
    private static boolean isShowMaptext = true;
    private static boolean isShowTraffic = false;

    private Context context;
    private RadioGroup radioGroup;
    private RadioButton mapNormalRadio;
    private RadioButton mapSateliteRadio;
    private RadioButton map3dRadio;

    private CheckBox mapShowText;
    private CheckBox mapShowTraffic;

    private TextView screenShoot;
    private View contentView;
    private View triggerView;
    private static OnMapModeChangeListner onMapModeChangeListner;

    private int x,y;
    public MapModePopupWindow(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_mode_dialog, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }

        radioGroup =(RadioGroup) view.findViewById(R.id.map_style_radios);
        radioGroup.check(getCheckedId(mapMode));
        mapNormalRadio = (RadioButton) view.findViewById(R.id.map_style_normal);
        map3dRadio = (RadioButton) view.findViewById(R.id.map_style_3d);
        mapSateliteRadio = (RadioButton) view.findViewById(R.id.map_style_satelite);
        mapShowText = (CheckBox) view.findViewById(R.id.show_maptext);
        mapShowTraffic = (CheckBox) view.findViewById(R.id.show_traffic);
        mapShowTraffic.setChecked(isShowTraffic);
        mapShowText.setChecked(isShowMaptext);
        mapShowTraffic.setOnCheckedChangeListener(this);
        mapShowText.setOnCheckedChangeListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == mapNormalRadio.getId()) {
                    onMapModeChangeListner.setMapMode(0);
                    mapMode= 0;
                } else if(checkedId == mapSateliteRadio.getId()) {
                    onMapModeChangeListner.setMapMode(1);
                    mapMode= 1;
                } else if(checkedId == map3dRadio.getId()) {
                    onMapModeChangeListner.setMapMode(2);
                    mapMode= 2;
                } else {
                    onMapModeChangeListner.setMapMode(0);
                    mapMode= 0;
                }
            }
        });

        screenShoot = (TextView)view.findViewById(R.id.screen_shoot);
        screenShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapModeChangeListner.screenShoot();
            }
        });



    }

    private int getCheckedId(int mode){
        switch (mode) {
            case 0:
                return R.id.map_style_normal;
            case 1:
                return R.id.map_style_satelite;
            case 2:
                return R.id.map_style_3d;
            default:
                return R.id.map_style_normal;
        }
    }


    public static void setOnMapModeChangeListner(OnMapModeChangeListner changeListner) {
         onMapModeChangeListner = changeListner;
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          showReveal(anchor,null,true);
            triggerView = anchor;
//            CircularAnim.show(getContentView()).triggerView(anchor).go();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showReveal(@NonNull final View anchor, final Animator.AnimatorListener listener, final boolean isShow) {
        final View contentView = getContentView();
        contentView.post(new Runnable() {
            @Override
            public void run() {
                final int[] myLocation = new int[2];
                final int[] anchorLocation = new int[2];
                contentView.getLocationOnScreen(myLocation);
                anchor.getLocationOnScreen(anchorLocation);
                final int cx = anchorLocation[0] - myLocation[0] + anchor.getWidth()/2;
                final int cy = anchorLocation[1] - myLocation[1] + anchor.getHeight()/2;

                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                final float finalRadius = (float) Math.hypot(dx, dy);
                Animator animator;
                if(isShow)
                  animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                else{
                     animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, finalRadius,0f );
                }
                animator.setDuration(400);
                if(listener!=null)
                animator.addListener(listener);
                animator.start();
            }
        });
    }

    @Override
    public void dismiss() {
//
        showReveal(triggerView, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                MapModePopupWindow.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        },false);
    }

    public interface OnMapModeChangeListner
    {
        void setMapMode(int positon);
        void showMapText(boolean flag);
        void showTraffic(boolean flag);
        void screenShoot();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.show_maptext:
                onMapModeChangeListner.showMapText(isChecked);
                isShowMaptext = isChecked;
                break;
            case R.id.show_traffic:
                onMapModeChangeListner.showTraffic(isChecked);
                isShowTraffic = isChecked;
                break;
        }
    }

}
