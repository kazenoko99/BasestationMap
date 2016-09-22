package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.offlinemap.AynscWorkListener;
import com.wenruisong.basestationmap.offlinemap.OfflineMapLoader;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.NetUtil;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.ArrayList;
import java.util.List;


public class OfflineMapFragment extends BackPressHandledFragment implements OfflineMapLoader.OfflineMapRemoveListener,NetUtil.NetworkChangeListener{
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private OfflineMapPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private String[] mTitle= new String[2];

    public static OfflineMapFragment getInstance(Bundle bundle) {
        OfflineMapFragment backPressHandledFragment = new OfflineMapFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logs.d("OfflineMapModule", "OfflineMapFragment onCreateView");
        View root = initView(inflater);
        initData();
        // 注册网络监听
        NetUtil.getInstance().registerNetworkChangeListener(this);
        return root;
    }

    private View initView(LayoutInflater inflater) {
        Logs.d("OfflineMapModule", "OfflineMapFragment initView");
        View root = inflater.inflate(R.layout.fragment_offline_view_pager, null);
        mViewPager = (ViewPager) root.findViewById(R.id.vp_container);
        View drawerIcon = root.findViewById(R.id.drawer_icon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openOrCloseDrawers();
            }
        });
        mTitle= new String[]{
                ResourcesUtil.getString(R.string.offline_map_download_title),
                ResourcesUtil.getString(R.string.offline_map_title)
        } ;

        mAdapter = new OfflineMapPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout=(TabLayout)root.findViewById(R.id.tab_layout);

        return root;
    }

    private TabLayout.OnTabSelectedListener mTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }
        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };


    private void initData() {
        Logs.d("OfflineMapModule", "OfflineMapFragment initData");

        doWorkAynsc(new AynscWorkListener() {
            @Override
            public void onPreExcuted() {
                Logs.d("OfflineMapModule", "OfflineMapFragment initData doWorkAynsc onPreExcuted");
            }

            @Override
            public Object doInBackground() {
                // 由于创建OfflineMapManager比较耗时
                Logs.d("OfflineMapModule", "OfflineMapFragment initData doWorkAynsc doInBackground try initOfflineMapLoader");
                OfflineMapLoader.initOfflineMapLoader();

                return null;
            }

            @Override
            public void onPostResult(Object obj) {

            }

            @Override
            public void onPostExcuted() {
                Logs.d("OfflineMapModule", "OfflineMapFragment initData doWorkAynsc doInBackground onPostExcuted");
                if ((null != OfflineMapLoader.getInstance())&&(OfflineMapFragment.this.isVisible())) {
                    Logs.d("OfflineMapModule", "OfflineMapFragment initData doWorkAynsc doInBackground onPostExcuted add child Fragment");

                    OfflineMapDownloadFragment downloadFragment = (OfflineMapDownloadFragment) OfflineMapDownloadFragment.getInstance(null);
                    OfflineMapCityFragment cityFragment = (OfflineMapCityFragment) OfflineMapCityFragment.getInstance(null);

                    OfflineMapLoader.getInstance().registerOfflineMapRemoveListener(OfflineMapFragment.this);
                    // 由于高德方面API的问题,必须在构造OfflineMapManager之后一会儿后,才能调用其stop函数,这样才是有效的.
                    OfflineMapLoader.getInstance().finishInit();

                    mFragments.add(downloadFragment);
                    mFragments.add(cityFragment);

                    mAdapter = new OfflineMapPagerAdapter(getChildFragmentManager());
                    mViewPager.setAdapter(mAdapter);
                    mTabLayout.setOnTabSelectedListener(mTabListener);
                    mTabLayout.setupWithViewPager(mViewPager);
                    final TabLayout.TabLayoutOnPageChangeListener listener =
                            new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
                    mViewPager.addOnPageChangeListener(listener);
//                    mViewPager.setOnPageChangeListener(OfflineMapFragment.this);

                    // 假如加载过程中点击Tab，加载完成就跳入对应的Tab
//                    if((mCurTab>=0)&&(mCurTab<mViewPager.getChildCount())) {
//                        mViewPager.setCurrentItem(mCurTab);
//                    }
                }
//                // 防止加载的前500ms的时候，点击第二个tab按钮，跳到城市列表的Tab
//                mActionBar.setTabScrolled(0,0,mState);

                // 隐藏加载框
//                mLoadingView.hide();
            }
        },false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRemoveStart() {

    }

    @Override
    public void onRemoveEnd() {

    }

    @Override
    public void onNetworkStatusChanged(NetUtil.NetType curNetType, NetUtil.NetType lastNetType) {

    }

    class OfflineMapPagerAdapter extends FragmentPagerAdapter {
        public OfflineMapPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle[position];
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
