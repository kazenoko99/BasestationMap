package com.wenruisong.basestationmap.pano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.baidu.lbsapi.panoramaview.ImageMarker;
import com.baidu.lbsapi.panoramaview.OnTabMarkListener;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.lbsapi.panoramaview.TextMarker;
import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.google.gson.Gson;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.Marker.BasestationNameMaker;
import com.wenruisong.basestationmap.basestation.Marker.SelectedBasestationMarker;
import com.wenruisong.basestationmap.map.CustomizedMapView;
import com.wenruisong.basestationmap.utils.DistanceUtils;

/**
 * 全景Demo主Activity
 */
public class PanoActivity extends AppCompatActivity {


    private static final String LTAG = "PanoActivity";
    private final String info = "全景图片拍摄地点距目标基站";
    private boolean isFirstLoad = true;
    private CameraMarker mCameraMarker;
    private SelectedBasestationMarker mSelectedBasestationMarker;
    private BasestationNameMaker mBasestationNameMaker;
    private PanoView mPanoView;
    private AMap aMap;
    private CustomizedMapView mCustomizedMapView;
    private double lat, lng;
    private LatLng cellLatLng;
    private String cellName;
    private Cell mCell;
    private int cellHigth;
    private PanoModel mPanoModel;
    private LatLng cameraLatLng;
    private float cameraRotation;
    private TextView panoInfoTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pano);
        mCameraMarker = new CameraMarker();
        mBasestationNameMaker = new BasestationNameMaker();
        mPanoModel = new PanoModel();
        mCustomizedMapView = (CustomizedMapView)findViewById(R.id.map);
        mCustomizedMapView.onCreate(savedInstanceState);
        initView();

        Intent intent = getIntent();
        if (intent != null) {
            mCell = intent.getParcelableExtra("CELL");
            cellLatLng = mCell.aMapLatLng;
            cellName = mCell.cellName;
            cellHigth = mCell.highth;
            lng = mCell.aMapLatLng.longitude;
            lat = mCell.aMapLatLng.latitude;
            mSelectedBasestationMarker.setSelected(aMap,mCell);
            Basestation basestation = new Basestation();
            basestation.amapLatLng = mCell.aMapLatLng;
            basestation.bsName = mCell.bsName;

            mBasestationNameMaker.setBasestation(basestation);
            mBasestationNameMaker.showInMap(aMap);

            Point sourcePoint = new Point(lng, lat);
            Point resultPointLL = CoordinateConverter.converter(CoordinateConverter.COOR_TYPE.COOR_TYPE_GCJ02, sourcePoint);
            testPanoByType(resultPointLL);
            mPanoView.removeAllMarker();
            addImageMarker();
            addCellTextMarker();

            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),18));
        }




        mPanoView.setOnPanoViewMoveListener(new PanoView.OnPanoViewMoveListener() {
            @Override
            public void viewMoved() {
                mCameraMarker.update(360-mPanoView.getPanoramaHeading(), mPanoModel.latLng);
                mCameraMarker.showInMap(aMap);
            }
        });


    }


    private void initView() {
        mPanoView = (PanoView) findViewById(R.id.panorama);

        aMap = mCustomizedMapView.getMap();
         panoInfoTextView =(TextView)findViewById(R.id.pano_info);
        PanoramaView.ImageDefinition high = PanoramaView.ImageDefinition.ImageDefinitionMiddle;
        mPanoView.setPanoramaImageLevel(high);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            panoInfoTextView.setText(info+ msg.getData().get("distance"));
            mCameraMarker.update(360-mPanoView.getPanoramaHeading(), mPanoModel.latLng);
            mCameraMarker.showInMap(aMap);
        }
    };

    private void testPanoByType(Point point) {
        mPanoView.setShowTopoLink(true);
        Log.d(LTAG, "lat is" + lat + "lng is" + lng);
        mPanoView.setPanorama(point.x, point.y);
        // 测试回调函数,需要注意的是回调函数要在setPanorama()之前调用，否则回调函数可能执行异常
        mPanoView.setPanoramaViewListener(new PanoramaViewListener() {

            @Override
            public void onLoadPanoramaBegin() {
                Log.i(LTAG, "onLoadPanoramaStart...");
            }

            @Override
            public void onLoadPanoramaEnd(String json) {
                Log.d(LTAG, "onLoadPanoramaEnd : " + json);
                Gson gson = new Gson();
                mPanoModel = gson.fromJson(json, PanoModel.class);
                mPanoModel.transCoordinate();
                Log.d(LTAG, "getTransed x=" + mPanoModel.X + "y=" + mPanoModel.Y + "lat=" + mPanoModel.lat + "lng=" + mPanoModel.lng);
                if (isFirstLoad) {
                    float shouldHeading = DistanceUtils.getHeading(mPanoModel.latLng, cellLatLng);
                    mPanoView.setPanoramaHeading(shouldHeading);
                    isFirstLoad = false;
                }

                Message message= new Message();
                Bundle bundle = new Bundle();
                bundle.putString("distance",DistanceUtils.getDistance(mPanoModel.latLng,cellLatLng));
                message.setData(bundle);
                mHandler.sendMessage(message);

                //  final String strReq = gson.toJson(uploadEntity);
                // onLoadPanoramaEnd : {"ID":"02003800001412161513443307B","Mode":"day","MoveDir":209.386,"Rname":"滨河东路","Type":"street","Z":78.143,"X":12675778,"Y":3184014}
            }

            @Override
            public void onLoadPanoramaError(String error) {
                Log.i(LTAG, "onLoadPanoramaError : " + error);
            }
        });


        mPanoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LTAG, "getpitch " + mPanoView.getPanoramaPitch() + "getHeading" + mPanoView.getPanoramaHeading());
                return false;
            }
        });
    }


    private ImageMarker cellMarker;

    /**
     * 添加图片标注
     */
    private void addImageMarker() {
        // 天安门西南方向

        // 天安门东北方向
        cellMarker = new ImageMarker();
        cellMarker.setMarkerPosition(new Point(lng, lat));
        cellMarker.setMarker(getResources().getDrawable(R.drawable.icon_markb));
        cellMarker.setMarkerHeight(cellHigth + 1);
        cellMarker.setOnTabMarkListener(new OnTabMarkListener() {

            @Override
            public void onTab() {
                Toast.makeText(PanoActivity.this, "图片MarkerB标注已被点击", Toast.LENGTH_SHORT).show();
            }
        });
        mPanoView.addMarker(cellMarker);
    }

    /**
     * 删除图片标注
     */
    private void removeImageMarker() {
        if (cellMarker != null)
            mPanoView.removeMarker(cellMarker);
    }

    private TextMarker cellTextMark;

    /**
     * 添加文本标注
     */
    private void addCellTextMarker() {
        cellTextMark = new TextMarker();
        cellTextMark.setMarkerPosition(new Point(lng, lat));
        cellTextMark.setFontColor(0xFFFF0000);
        cellTextMark.setText(cellName);
        cellTextMark.setFontSize(12);
        // cellTextMark.setPadding(10, 20, 15, 25);
        cellTextMark.setMarkerHeight(cellHigth);
        cellTextMark.setOnTabMarkListener(new OnTabMarkListener() {

            @Override
            public void onTab() {
                Toast.makeText(PanoActivity.this, "textMark1标注已被点击", Toast.LENGTH_SHORT).show();
            }
        });

        mPanoView.addMarker(cellTextMark);
    }

    /**
     * 删除文本标注
     */
    private void removeCellTextMarker() {
        if (cellTextMark != null)
            mPanoView.removeMarker(cellTextMark);
    }


    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mCustomizedMapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mCustomizedMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mCustomizedMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        aMap.clear();
        mCustomizedMapView.onDestroy();
        super.onDestroy();
    }

}

