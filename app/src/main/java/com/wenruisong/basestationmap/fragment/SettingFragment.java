package com.wenruisong.basestationmap.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.common.Settings;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends BackPressHandledFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mChangeCityView;
    private TextView mCityView;
    private Switch mKeepScreenSwitch;
    private View mKeepScreenView;
    private View mZoomButtonView;
    private View mAboutAndHelp;
    private View mShowSignalNotification;
    private Switch mShowSignalNotificationSwitch;
    private MainActivity mMainActivity;
    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SettingFragment getInstance(Bundle bundle) {
        SettingFragment backPressHandledFragment = new SettingFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle("群组");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mMainActivity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.mz_titlebar_ic_list);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMainActivity.openOrCloseDrawers();
            }
        });
        mChangeCityView = v.findViewById(R.id.settings_city_change_layout);
        mCityView = (TextView) v.findViewById(R.id.settings_city_change_summary);
        mKeepScreenSwitch = (Switch) v.findViewById(R.id.settings_keep_screen_on_switch);
        mKeepScreenView = v.findViewById(R.id.settings_keep_screen_on_layout);
        mAboutAndHelp = v.findViewById(R.id.settings_about_help);
        mShowSignalNotification =v.findViewById(R.id.settings_show_singal_notification_layout);
        mChangeCityView.setOnClickListener(this);
        mKeepScreenView.setOnClickListener(this);
        mShowSignalNotification.setOnClickListener(this);
        mShowSignalNotificationSwitch  = (Switch) v.findViewById(R.id.settings_show_singal_notification_switch);
        mShowSignalNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeSingalNotificationOnSettings(isChecked);
            }
        });
        mKeepScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeKeepScreenOnSettings(isChecked);
            }
        });


        return v;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settings_keep_screen_on_layout  :
                mKeepScreenSwitch.performClick();
                break;
            case R.id.settings_about_help:
                break;
            case R.id.settings_city_change_layout:
                //    getFragShower().showFragments(MainMapActivity.FRAG_SWITCH_CITY, true, false);
                break;
            case R.id.settings_show_singal_notification_layout:
                mShowSignalNotificationSwitch.performClick();
                break;
        }
    }

    private void changeKeepScreenOnSettings(boolean keep) {
        Settings.getInstance().setKeepScreenOn(keep);
    }
    private void changeSingalNotificationOnSettings(boolean keep) {
        Settings.getInstance().showSingalNotification(keep);
    }

}
