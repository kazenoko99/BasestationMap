package com.wenruisong.basestationmap.listener;

import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * Created by wuxuexiang on 15-6-12.
 */
public interface IMapCommon {


    interface FragShower {
        void showFragments(String tag, boolean needback);

        void showFragments(String tag, boolean needback, Bundle bundle);

        void showFragments(String tag, boolean needback, boolean resultBack);

        void showFragments(String tag, boolean needback, boolean resultBack, Bundle bundle);

        void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle);

        void backResultToRequestActivity(Bundle bundle);


        /**
         * @param tag             Fragment标签
         * @param needback        是否保存到后退栈,一般操作为true
         * @param clearHistory    是否清空后退栈，目前仅针对MapViewFragment有效
         * @param resultBack      是否返回结果给启动该fragment的fragment
         * @param bundle          启动fragment时给进去的参数
         * @param forceInvalidate 强制重启,查看#CrashHandler#中调用
         */
        void showFragments(String tag, boolean needback, boolean clearHistory, boolean resultBack, Bundle bundle, boolean forceInvalidate);
    }

    interface DrawerFragChangeListener {
        void onDrawerFragChange(int position);
    }

    interface NavOnClickListener {
        void onNavClick();
    }

    interface NavOnClickProvider {
        void updateNavOnClickListener(NavOnClickListener listener);
    }

    interface BarChangeListener {
        void onBarChanged(Fragment fragment);
    }

}
