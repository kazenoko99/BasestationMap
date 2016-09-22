package com.wenruisong.basestationmap.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.MapCoreActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.NetworkCameraActivity;
import com.wenruisong.basestationmap.eventbus.MapToolsEvents;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.view.ShortCutGridView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToolBoxFragment extends BackPressHandledFragment implements View.OnClickListener {
    private final static String TAG = ToolBoxFragment.class.getSimpleName();
    private ShortCutGridView mToolboxGridView;
    private MainActivity mMainActivity;
    private String[] toolsName={"地图标尺","指南针","水平仪","下倾角测量","地图量角","经纬度找点","网络勘察相机","伪基站捕获","网速测试"};
    private int[] toolIcons={R.drawable.ic_tool_ruler,R.drawable.ic_tool_level,R.drawable.ic_tool_compass,
            R.drawable.ic_tool_incline,R.drawable.ic_tool_angel,R.drawable.ic_tool_gps_point,
            R.drawable.ic_tool_camera,R.drawable.ic_tool_fake_station,R.drawable.ic_tool_speedtest};
    public ToolBoxFragment() { }

    public static ToolBoxFragment getInstance(Bundle bundle) {
        ToolBoxFragment backPressHandledFragment = new ToolBoxFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toolbox, container, false);
        mMainActivity = (MainActivity) getActivity();
        mToolboxGridView = (ShortCutGridView) view.findViewById(R.id.tool_grid);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle("工具箱");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mMainActivity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.mz_titlebar_ic_list);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               mMainActivity.openOrCloseDrawers();
           }
       });
        GridAdapter gridAdapter = new GridAdapter(getContext());
        mToolboxGridView.setAdapter(gridAdapter);
        mToolboxGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case Constants.TOOL_RULER :
                        MapToolsEvents.getBus().post(new MapToolsEvents.OnClickTools(position));
                        mMainActivity.showFragments(MapCoreActivity.FRAG_MAP_VIEW, false, true, false, null);
                        break;
                    case Constants.TOOL_COMPASS :
                        MapToolsEvents.getBus().post(new MapToolsEvents.OnClickTools(position));
                        mMainActivity.showFragments(MapCoreActivity.FRAG_MAP_VIEW, false, true, false, null);
                    case Constants.TOOL_LEVEL :
                        break;
                    case Constants.TOOL_INCLINE :
                        break;
                    case Constants.TOOL_ANGLE :
                        break;
                    case Constants.TOOL_GPS_POINT :
                        MapToolsEvents.getBus().post(new MapToolsEvents.OnClickTools(position));
                        mMainActivity.showFragments(MapCoreActivity.FRAG_MAP_VIEW, false, true, false, null);
                    case Constants.TOOL_CAMERA :
                        Intent intent = new Intent();
                        intent.setClass(getContext(), NetworkCameraActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    case Constants.TOOL_FAKE_BS :
                        break;
                    case Constants.TOOL_SPEED_TEST :
                        break;
                    default:
                        break;
                }

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
//          case R.id.search_group:
//              startActivity(new Intent(mMainActivity, GroupSearchActivity.class));
//              break;
//          case R.id.create_group:
//              startActivity(new Intent(mMainActivity, GroupCreateActivity.class));
//              break;
//          case R.id.drawer_icon:
//              mMainActivity.openOrCloseDrawers();
//              break;
      }
    }
    public class GridAdapter extends BaseAdapter {
        private Context mContext;

        // Constructor
        public GridAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return toolsName.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView toolImage;
            TextView toolName;
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.item_toolbox, null);
            }
            toolImage = (ImageView) convertView.findViewById(R.id.gridView_image);
            toolName = (TextView) convertView.findViewById(R.id.gridView_text);
            toolImage.setImageDrawable(mContext.getResources().getDrawable(toolIcons[position]));
            toolName.setText(toolsName[position]);
            return convertView;
        }

    }

}
