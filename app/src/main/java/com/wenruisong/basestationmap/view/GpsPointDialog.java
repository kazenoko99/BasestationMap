package com.wenruisong.basestationmap.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/11.
 */
public class GpsPointDialog extends Dialog {
    public GpsPointDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private EditText gpsPointName;
        private EditText gpsPointLat;
        private EditText gpsPointLng;
        private Button clearAllPoint;
        private Button confirmButton;
        private Button cancelButton;
        private View contentView;
        private OnAddGpsPointListner onAddGpsPointListner;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnMapModeChangeListner(OnAddGpsPointListner onAddGpsPointListner) {
            this.onAddGpsPointListner = onAddGpsPointListner;
            return this;
        }

        public GpsPointDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final GpsPointDialog dialog = new GpsPointDialog(context);
            View layout = inflater.inflate(R.layout.map_gps_point_dialog, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);

            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(layout, params);
            // set the dialog title
            gpsPointName = (EditText) dialog.findViewById(R.id.gps_point_name);
            gpsPointLat = (EditText) dialog.findViewById(R.id.gps_point_lat);
            gpsPointLng = (EditText) dialog.findViewById(R.id.gps_point_lng);
            confirmButton = (Button) dialog.findViewById(R.id.gps_point_close);
            clearAllPoint = (Button) dialog.findViewById(R.id.gps_point_clear);
            cancelButton = (Button) dialog.findViewById(R.id.gps_point_cancel);
            clearAllPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddGpsPointListner.clearAllGpsPoint();
                    dialog.dismiss();
                }
            });
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gpsPointLat.getText().toString().isEmpty() || gpsPointLng.getText().toString().isEmpty()) {
                        Toast.makeText(context, "经纬度不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LatLng pointLatLng = new LatLng(Float.parseFloat(gpsPointLat.getText().toString()), Float.parseFloat(gpsPointLng.getText().toString()));
                    if (pointLatLng.latitude < 55 && pointLatLng.latitude > 5 && pointLatLng.longitude < 135 && pointLatLng.longitude > 73) {
                        onAddGpsPointListner.addGpsPoint(gpsPointName.getText().toString(), pointLatLng);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(BasestationMapApplication.getContext(), "请输入国内正确经纬度", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return dialog;
        }
    }


    public interface OnAddGpsPointListner {
        void addGpsPoint(String pointName, LatLng pointLatLng);

        void clearAllGpsPoint();
    }
}
