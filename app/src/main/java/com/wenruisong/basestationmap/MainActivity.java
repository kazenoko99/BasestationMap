package com.wenruisong.basestationmap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.wenruisong.basestationmap.fragment.BaseFragment;
import com.wenruisong.basestationmap.fragment.NavigationDrawerFragment;
import com.wenruisong.basestationmap.listener.IMapCommon;
import com.wenruisong.basestationmap.utils.CompatUtils;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DeviceUtils;

import java.lang.reflect.Field;

public class MainActivity extends MapCoreActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, IMapCommon.Global,
        IMapCommon.NavOnClickProvider, IMapCommon.BarChangeListener {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout dl_navigator;
    private Context mContext;

    private final String mPageName = "MainActivity";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        initWindow();
        setSystemBarTintDrawable(new ColorDrawable(getResources().getColor(R.color.filter_expand_menu_divider_color)));
        setContentView(R.layout.activity_main);
        dl_navigator =  (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,dl_navigator);
        dl_navigator.closeDrawers();
        onCreated(savedInstanceState);
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switchFragments(position);
    }

    void switchFragments(int position)
    {
        switch (position) {
            case Constants.DRAWER_MAP:
                showFragments(FRAG_MAP_VIEW, false, true, false, null);
                break;
            case Constants.DRAWER_TOOLS:
                break;
            case Constants.FRAG_BTS_SETTING:
//                ToastUtil.show(this, "敬请期待");
                showFragments(FRAG_BTS_SETTING, true, true, false, null);
                break;
            case Constants.DRAWER_COMMON_ADDRESS:
                showFragments(FRAG_COMMON_ADDRESS, true, true, true, null);
                break;
            case Constants.DRAWER_OFFlINE_MAP:
                showFragments(FRAG_OFFLINE_MAP, true, true, false, null);
                break;
            case Constants.DRAWER_SETTING:
                showFragments(FRAG_SETTINGS, true, true, false, null);
                break;
            default:
                break;
        }
    }


    @SuppressLint("InlinedApi")
    private void initWindow() {
        DeviceUtils.collapseStatusBar(getApplicationContext());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Field fTYPE_KEYGUARD = CompatUtils.getField(
                WindowManager.LayoutParams.class, "TYPE_KEYGUARD");
        int TYPE_KEYGUARD = (int) CompatUtils.getFieldValue(
                WindowManager.LayoutParams.class, 0, fTYPE_KEYGUARD);
        getWindow().setType(TYPE_KEYGUARD);
    }

    public void setSystemBarTintDrawable(Drawable tintDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            if (tintDrawable != null) {
                mTintManager.setStatusBarTintEnabled(true);
                mTintManager.setTintDrawable(tintDrawable);
            } else {
                mTintManager.setStatusBarTintEnabled(false);
                mTintManager.setTintDrawable(null);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (dl_navigator.isDrawerOpen(Gravity.LEFT)) {
            dl_navigator.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBarChanged(Fragment fragment) {

    }

    @Override
    public void openOrCloseDrawers() {
        if (dl_navigator.isDrawerOpen(Gravity.LEFT)) {
            dl_navigator.closeDrawers();
        } else {
            dl_navigator.openDrawer(GravityCompat.START);
        }

    }

    @Override
    public BaseFragment getCurrentFragment() {
        return null;
    }

    @Override
    public void setLock(boolean lock) {

    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public void updateNavOnClickListener(IMapCommon.NavOnClickListener listener) {

    }
}
