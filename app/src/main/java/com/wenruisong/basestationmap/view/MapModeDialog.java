package com.wenruisong.basestationmap.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/11.
 */
public class MapModeDialog extends Dialog {
    public MapModeDialog(Context context) {
        super(context);
    }


    public static class Builder implements CheckBox.OnCheckedChangeListener{

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
        private OnMapModeChangeListner onMapModeChangeListner;

        private int x,y;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnMapModeChangeListner(OnMapModeChangeListner onMapModeChangeListner) {
            this.onMapModeChangeListner = onMapModeChangeListner;
            return this;
        }

        public Builder setLocation(int x, int y){
            this.x =x;
            this.y =y;
            return this;
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

        public MapModeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MapModeDialog dialog = new MapModeDialog(context);
            View layout = inflater.inflate( R.layout.map_mode_dialog, null );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(true);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(layout, params);
            // set the dialog title
            radioGroup =(RadioGroup) dialog.findViewById(R.id.map_style_radios);
            radioGroup.check(getCheckedId(mapMode));
            mapNormalRadio = (RadioButton) dialog.findViewById(R.id.map_style_normal);
            map3dRadio = (RadioButton) dialog.findViewById(R.id.map_style_3d);
            mapSateliteRadio = (RadioButton) dialog.findViewById(R.id.map_style_satelite);
            mapShowText = (CheckBox) dialog.findViewById(R.id.show_maptext);
            mapShowTraffic = (CheckBox) dialog.findViewById(R.id.show_traffic);
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

            screenShoot = (TextView)dialog.findViewById(R.id.screen_shoot);
            screenShoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMapModeChangeListner.screenShoot();
                }
            });
            dialog.setContentView(layout);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity( Gravity.TOP);
            lp.horizontalMargin = 0;
                    lp.verticalMargin =0;
            lp.x = 0; // 新位置X坐标
            lp.y = 200; // 新位置Y坐标
            dialogWindow.setAttributes(lp);

            return dialog;
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

   public interface OnMapModeChangeListner
    {
        void setMapMode(int positon);
        void showMapText(boolean flag);
        void showTraffic(boolean flag);
        void screenShoot();
    }


}
