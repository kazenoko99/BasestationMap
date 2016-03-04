package com.wenruisong.basestationmap.listener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.wenruisong.basestationmap.fragment.BaseFragment;


/**
 * Created by wuxuexiang on 15-6-12.
 */
public interface IMapCommon {
    public interface Global {

        public void openOrCloseDrawers();

        public BaseFragment getCurrentFragment();

        public void setLock(boolean lock);

        public Toolbar getToolbar();
    }

    public interface FragShower {
        public void showFragments(String tag, boolean needback);

        public void showFragments(String tag, boolean needback, Bundle bundle);

        public void showFragments(String tag, boolean needback, boolean resultBack);

        public void showFragments(String tag, boolean needback, boolean resultBack, Bundle bundle);

        public void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle);

        public void backResultToRequestActivity(Bundle bundle);


        /**
         * @param tag             Fragment标签
         * @param needback        是否保存到后退栈,一般操作为true
         * @param clearHistory    是否清空后退栈，目前仅针对MapViewFragment有效
         * @param resultBack      是否返回结果给启动该fragment的fragment
         * @param bundle          启动fragment时给进去的参数
         * @param forceInvalidate 强制重启,查看#CrashHandler#中调用
         */
        public void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle, boolean forceInvalidate);
    }

    public interface DrawerFragChangeListener {
        public void onDrawerFragChange(int position);
    }

    public interface NavOnClickListener {
        public void onNavClick();
    }

    public interface NavOnClickProvider {
        public void updateNavOnClickListener(NavOnClickListener listener);
    }

    public interface BarChangeListener {
        public void onBarChanged(Fragment fragment);
    }

}
