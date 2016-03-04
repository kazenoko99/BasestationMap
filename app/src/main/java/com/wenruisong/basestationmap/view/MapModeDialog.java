package com.wenruisong.basestationmap.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/11.
 */
public class MapModeDialog extends Dialog {
    public MapModeDialog(Context context) {
        super(context);
    }


    public static class Builder {
        private Context context;
        private RadioGroup radioGroup;
        private RadioButton mapNormalRadio;
        private RadioButton mapSateliteRadio;
        private RadioButton map3dRadio;

        private View contentView;
        private OnMapModeChangeListner onMapModeChangeListner;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnMapModeChangeListner(OnMapModeChangeListner onMapModeChangeListner) {
            this.onMapModeChangeListner = onMapModeChangeListner;
            return this;
        }


        public MapModeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MapModeDialog dialog = new MapModeDialog(context);
            View layout = inflater.inflate( R.layout.map_mode_dialog, null );
            dialog.setContentView(layout);
            // set the dialog title
            radioGroup =(RadioGroup) dialog.findViewById(R.id.map_style_radios);
            mapNormalRadio = (RadioButton) dialog.findViewById(R.id.map_style_normal);
            map3dRadio = (RadioButton) dialog.findViewById(R.id.map_style_3d);
            mapSateliteRadio = (RadioButton) dialog.findViewById(R.id.map_style_satelite);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == mapNormalRadio.getId())
                    {
                        onMapModeChangeListner.setMapMode(0);
                    }
                    else if(checkedId == mapSateliteRadio.getId())
                    {
                        onMapModeChangeListner.setMapMode(1);
                    }
                    else if(checkedId == mapSateliteRadio.getId())
                    {
                        onMapModeChangeListner.setMapMode(2);
                    }
                    else {
                        onMapModeChangeListner.setMapMode(0);
                    }
                }
            });

            dialog.setContentView(layout);
            return dialog;
        }
    }
   public enum MapMode{
       mapNormal,
       mapSatelite,
       map3d
   }
   public interface OnMapModeChangeListner
    {
        void setMapMode(int positon);
    }
}
