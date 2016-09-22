package com.wenruisong.basestationmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.wenruisong.basestationmap.common.BackInterruptFlag;
import com.wenruisong.basestationmap.fragment.BackPressHandledFragment;
import com.wenruisong.basestationmap.fragment.BaseFragment;
import com.wenruisong.basestationmap.fragment.BasestationSettingFragment;
import com.wenruisong.basestationmap.fragment.CommonAddressFragment;
import com.wenruisong.basestationmap.fragment.GroupFragment;
import com.wenruisong.basestationmap.fragment.MapFragment;
import com.wenruisong.basestationmap.fragment.OfflineMapFragment;
import com.wenruisong.basestationmap.fragment.SettingFragment;
import com.wenruisong.basestationmap.fragment.ToolBoxFragment;
import com.wenruisong.basestationmap.listener.IMapCommon;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;


public class MapCoreActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, IMapCommon.FragShower {

    public static final String TAG = MapCoreActivity.class.getSimpleName();

    private static int instanceCount = 0; //acitivity的实例计数，当最后一个destory的时候，释放系统资源。


    public static final String FRAG_AR = "ar_frag";
    public static final String FRAG_AR_GROUP = "ar_group_frag";
    public static final String FRAG_BUS_DETAIL = "bus_detail_frag";
    public static final String FRAG_AR_EDIT = "ar_edit_frag";
    public static final String FRAG_BUS_REAL_REMIND = "bus_real_remind_frag";
    public static final String FRAG_MAP_VIEW = "map_view_frag";
    public static final String FRAG_KEYWORD_SEARCH = "keyword_search_frag";
    public static final String FRAG_AROUND_SEARCH = "around_search_frag";
    public static final String FRAG_COMMON_ADDRESS = "common_address_frag";
    public static final String FRAG_SETTINGS = "settings_frag";
    public static final String FRAG_BTS_SETTING = "settings_bts_frag";
    public static final String FRAG_TOOL_BOX = "tool_box";
    public static final String FRAG_GROUP= "group_frag";
    public static final String FRAG_OFFLINE_MAP = "offline_map_frag";
    public static final String FRAG_SETTING = "setting_frag";
    public static final String FRAG_POI_DETAIL = "poi_detail_frag";
    public static final String FRAG_SEARCH_RESULT_LIST = "search_result_list_frag";
    public static final String FRAG_ROUTE = "route_frag";
    public static final String FRAG_SUBWAY_CITY_LIST = "subway_city_list_frag";
    public static final String FRAG_SUBWAY_WEB = "subway_web_frag";
    public static final String FRAG_NAV_SEARCH = "frag_nav_search";
    private BackInterruptFlag mInterruptFlag;

    protected BaseFragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private MapFragment mMapViewFragment;



    protected void onCreated(Bundle savedInstanceState) {
        showFragments(FRAG_MAP_VIEW, false, null);
        showFragment();
    }

    /**
     * 携带Intent启动非MapViewFragment时needBack需设置为true
     */
    protected void showFragment() {
        if (getIntent() != null && getIntent().getAction() != null) {
            if (getIntent().getAction().equals(Constants.ACTION_AR_NAV)) {
                showFragments(FRAG_AR, true);
                mInterruptFlag = new BackInterruptFlag(BackInterruptFlag.Flag.FLAG_AR_NAV_INTERRUPT);
                mInterruptFlag.setUntilString(FRAG_AR);
            }  else if (getIntent().getAction().equals(Constants.ACTION_AR_NAV_GROUP)) {
                showFragments(FRAG_AR_GROUP, true);
                mInterruptFlag = new BackInterruptFlag(BackInterruptFlag.Flag.FLAG_AR_NAV_GROUP_INTERRUPT);
                mInterruptFlag.setUntilString(FRAG_AR_GROUP);
            } else if (getIntent().getAction().equals(Constants.ACTION_MAP_VIEW)) {
                showFragments(FRAG_MAP_VIEW, false, null);
            }
        }
    }

    private boolean onBackInterrupt() {
        switch (mInterruptFlag.getInterruptFlag()) {
            case BackInterruptFlag.Flag.FLAG_METRO_INTERRUPT:
            case BackInterruptFlag.Flag.FLAG_BUS_DETAIL_INTERRUPT:
            case BackInterruptFlag.Flag.FLAG_KEYWORD_SEARCH_INTERRUPT: {
                if (TextUtils.equals(mCurrentFragment.getTag(), mInterruptFlag.getUntilString())) {
                    this.finish();
                    mInterruptFlag = null;
                    return true;
                }
            }
            break;
            default:
                break;
        }
        return false;
    }

    public void onParentBackPress() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment == null) {
            super.onBackPressed();
            return;
        }

        int count = mFragmentManager.getBackStackEntryCount();
        if (count == 1 && mInterruptFlag != null && onBackInterrupt()) {
            //如果后退栈只剩自己了，判断"中断"的有木有，有就退出Activity
            return;
        }

        if (mCurrentFragment.handleBackPress() == false) {
            if (mCurrentFragment instanceof MapFragment) {
                if (count == 0) {
                    finish();
                } else {
                    super.onBackPressed();//onBackPressed里面有f
                    // ragment popBackStackImmediate操作
                }
            } else {
                if (!mFragmentManager.popBackStackImmediate()) {
                    mCurrentFragment = mMapViewFragment;
                    showFragmentAnyWay(mMapViewFragment, FRAG_MAP_VIEW);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.e(TAG, "oncreate coreeee");
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.addOnBackStackChangedListener(this);
        instanceCount++;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {


        mCurrentFragment = null;
        mMapViewFragment = null;

        instanceCount--;
        super.onDestroy();
    }

    public void showFragments(String tag, boolean needback) {
        showFragments(tag, needback, false, false, null);
    }

    public void showFragments(String tag, boolean needback, Bundle bundle) {
        showFragments(tag, needback, false, false, bundle);
    }

    public void showFragments(String tag, boolean needback, boolean resultBack) {
        showFragments(tag, needback, false, resultBack, null);
    }

    public void showFragments(String tag, boolean needback, boolean resultBack, Bundle bundle) {
        showFragments(tag, needback, false, resultBack, bundle);
    }

    public void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle) {
        showFragments(tag, needback, clearHistory, resultBack, bundle, false);
    }

    @Override
    public void backResultToRequestActivity(Bundle bundle) {

    }

    /**
     * @param tag             Fragment标签
     * @param needback        是否保存到后退栈,一般操作为true, 被弃用
     * @param clearHistory    是否清空后退栈，目前仅针对MapViewFragment有效
     * @param resultBack      是否返回结果给启动该fragment的fragment
     * @param bundle          启动fragment时给进去的参数
     * @param forceInvalidate 强制重启,查看#CrashHandler#中调用
     */
    public void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle, boolean forceInvalidate) {
        BaseFragment newFragment = getFragmentByTag(tag, bundle, resultBack);
        if (forceInvalidate && newFragment instanceof MapFragment) {
            FragmentTransaction trans = mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                trans.remove(mCurrentFragment);
            }
            if (clearHistory) {//进入了mapviewFragment都把后退栈清掉
                int count = mFragmentManager.getBackStackEntryCount();
                mFragmentManager.removeOnBackStackChangedListener(this);
                for (int i = 0; i < count; i++) {
                    mFragmentManager.popBackStackImmediate();
                }
                mFragmentManager.addOnBackStackChangedListener(this);
            }
            trans.add(R.id.Container, newFragment, tag);
            trans.commit();
            mCurrentFragment = newFragment;
            return;
        }
        if (mCurrentFragment == newFragment) {
            //has been already showed.
            mCurrentFragment = newFragment;
            return;
        } else {
            FragmentTransaction trans = mFragmentManager.beginTransaction();
            if (newFragment instanceof MapFragment) {
                if (mCurrentFragment == null) {//第一次添加mapviewFragment
                    if (!newFragment.isAdded()) {
                        trans.add(R.id.Container, newFragment, tag);
                    } else {
                        //考虑到Activity重建但fragmentManager仍然管理fragment的情况,场景设置系统字体大小
                        trans.show(newFragment);
                    }
                } else {
                    if (clearHistory) {//进入了mapviewFragment都把后退栈清掉
/*                        mFragmentManager.removeOnBackStackChangedListener(this);
                        mFragmentManager.popBackStackImmediate(FRAG_MAP_VIEW, 0);*/
                        int count = mFragmentManager.getBackStackEntryCount();
                        mFragmentManager.removeOnBackStackChangedListener(this);
                        for (int i = 0; i < count; i++) {
                            mFragmentManager.popBackStackImmediate();
                        }
                        mFragmentManager.addOnBackStackChangedListener(this);
                    }
                    trans.show(newFragment);
                    if (!clearHistory /*&& needback*/ && trans.isAddToBackStackAllowed()) {
                        trans.addToBackStack(tag);
                    }
                }
            } else {
                if (newFragment.isDetached()) {
                    trans.attach(newFragment);
                } else {
                    trans.add(R.id.Container, newFragment, tag);
                }

                if (/*needback &&*/ trans.isAddToBackStackAllowed()) {
                    trans.addToBackStack(tag);
                }
            }

            if (mCurrentFragment != null && !(mCurrentFragment instanceof MapFragment)) {
                trans.detach(mCurrentFragment);
            } else if (mCurrentFragment != null && mCurrentFragment instanceof MapFragment) {
                trans.hide(mCurrentFragment);
                mMapViewFragment = (MapFragment) mCurrentFragment;
            }
            trans.commit();
//            mCurrentFragment = newFragment;
            replaceCurFragment(newFragment);
        }
    }


    private void replaceCurFragment(BaseFragment baseFragment) {
        mCurrentFragment = baseFragment;
    }


    private BaseFragment getFragmentByTag(String tag, Bundle bundle, boolean resultBack) {
        BaseFragment backPressHandledFragment = getFragmentByTag(tag, bundle);
        if (resultBack) {
            backPressHandledFragment.setTargetFrag(mCurrentFragment, 0);
        }
        return backPressHandledFragment;
    }


    protected BaseFragment getFragmentByTag(String tag, Bundle bundle) {
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            BackPressHandledFragment backPressHandledFragment = (BackPressHandledFragment) fragment;
            if (fragment instanceof MapFragment) {
                mMapViewFragment = (MapFragment) backPressHandledFragment;
            }
            return backPressHandledFragment;
        }
       if (FRAG_MAP_VIEW.equals(tag)) {//主fragment
            if (mMapViewFragment == null) {
                mMapViewFragment = MapFragment.getInstance(bundle);
            }
            return mMapViewFragment;
        } else if (FRAG_OFFLINE_MAP.equals(tag)) {
            return OfflineMapFragment.getInstance(bundle);
        }else if (FRAG_BTS_SETTING.equals(tag)) {
           return BasestationSettingFragment.getInstance(bundle);
       }else if (FRAG_SETTINGS.equals(tag)) {
           return SettingFragment.getInstance(bundle);
       }else if (FRAG_GROUP.equals(tag)) {
           return GroupFragment.getInstance(bundle);
       }else if(FRAG_COMMON_ADDRESS.equals(tag)){
           return CommonAddressFragment.getInstance(bundle);
       }else if(FRAG_TOOL_BOX.equals(tag)){
           return ToolBoxFragment.getInstance(bundle);
       }

        return null;
    }

    /**
     * 监听 fragment 主动退出
     */
    @Override
    public void onBackStackChanged() {
        // 监听到有 fragment 自己弹出堆栈
        int count = mFragmentManager.getBackStackEntryCount();
        Logs.d(TAG, "onBackStackChanged() backStack count =" + count);
        if (count == 0) {
            replaceCurFragment(mMapViewFragment);
            //MapView可能由于加入到历史记录中被pop出来了
//            showFragmentAnyWay(mCurrentFragment, FRAG_MAP_VIEW);
        } else {
            FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(count - 1);
            BaseFragment baseFragment = (BaseFragment) mFragmentManager.findFragmentByTag(entry.getName());
            replaceCurFragment(baseFragment);
        }
    }

    private void showFragmentAnyWay(BaseFragment fragment, String tag) {
        FragmentTransaction trans = mFragmentManager.beginTransaction();
        fragment.setParamter(null);
        if (!fragment.isAdded()) {
            trans.add(R.id.Container, fragment, tag);
        } else if (fragment.isDetached()) {
            trans.attach(fragment);
        } else if (fragment.isHidden()) {
            trans.show(fragment);
        } else {
            trans.show(fragment);
        }
        if (/*needback &&*/ trans.isAddToBackStackAllowed()) {
            trans.addToBackStack(tag);
        }
        trans.commit();
    }
}














