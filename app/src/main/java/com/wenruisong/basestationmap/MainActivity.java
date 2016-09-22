package com.wenruisong.basestationmap;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import com.wenruisong.basestationmap.fragment.NavigationDrawerFragment;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.service.SingalAnalyzeService;
import com.wenruisong.basestationmap.utils.CompatUtils;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.DeviceUtils;

import java.lang.reflect.Field;

public class MainActivity extends MapCoreActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,  DrawerListener {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout dl_navigator;
    private Context mContext;
    private static int sCurrentDrawerPosition = 0;
    private SystemBarTintManager mTintManager;
    private final String mPageName = "MainActivity";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent bindIntent = new Intent(this, SingalAnalyzeService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        initWindow();
        initSystemBar();
        setContentView(R.layout.activity_main);
        dl_navigator = (DrawerLayout) findViewById(R.id.drawer_layout);
        dl_navigator.addDrawerListener(this);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, dl_navigator);
        dl_navigator.closeDrawers();
        mNavigationDrawerFragment.initUser();
        onCreated(savedInstanceState);
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        Log.d("starttime","MainActivity onCreate"+(System.currentTimeMillis()-BasestationMapApplication.startTime));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("starttime","MainActivity onResume"+(System.currentTimeMillis()-BasestationMapApplication.startTime));
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
        LocationHelper.StartLocating();
        Log.d("starttime","MainActivity onResume finish"+(System.currentTimeMillis()-BasestationMapApplication.startTime));

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
        LocationHelper.StopLocating();
    }

    @Override
    protected void onDestroy() {
 //     this.unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        sCurrentDrawerPosition = position;
    }

    void switchFragments(int position) {
        switch (position) {
            case Constants.DRAWER_MAP:
                setSystemBarAlpha(0.3f);
                showFragments(FRAG_MAP_VIEW, false, true, false, null);
                return;
            case Constants.DRAWER_TOOLS:
                showFragments(FRAG_TOOL_BOX, true, true, false, null);
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
            case Constants.DRAWER_GROUP:
                showFragments(FRAG_GROUP, true, true, false, null);
                break;

            case Constants.DRAWER_SETTING:
                showFragments(FRAG_SETTINGS, true, true, false, null);
                break;
            default:
                break;
        }
        setSystemBarAlpha(0);
    }


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
        mTintManager = new SystemBarTintManager(this);

    }

    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
            setSystemBarAlpha(0.3f);
        }
    }

    public void setSystemBarAlpha(float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTintManager.setTintAlpha(alpha);
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


    public void openOrCloseDrawers() {
        if (dl_navigator.isDrawerOpen(Gravity.LEFT)) {
            dl_navigator.closeDrawers();
        } else {
            dl_navigator.openDrawer(GravityCompat.START);
        }

    }

//    @Override
//    public BaseFragment getCurrentFragment() {
//        return null;
//    }
//
//    @Override
//    public void setLock(boolean lock) {
//
//    }
//
//    @Override
//    public Toolbar getToolbar() {
//        return null;
//    }
//
//    @Override
//    public void updateNavOnClickListener(IMapCommon.NavOnClickListener listener) {
//
//    }


    public SingalAnalyzeService mSingalAnalyzeService;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSingalAnalyzeService = ((SingalAnalyzeService.PhoneStateBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSingalAnalyzeService = null;
        }

    };

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        switchFragments(sCurrentDrawerPosition);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
