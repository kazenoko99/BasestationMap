package com.wenruisong.basestationmap.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.database.CsvToSqliteHelper;
import com.wenruisong.basestationmap.eventbus.FileExplorerEvents;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/1/23.
 */
public class BasestationSettingFragment extends BackPressHandledFragment {
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private NetTypePagerAdapter netTypePagerAdapter;
    private TabLayout mTabLayout;
    BasestationImportFragment fragment2g;
    BasestationImportFragment fragment3g;
    BasestationImportFragment fragment4g;
    private String updatePath;
    private int updateFragmentIndex;
    private Boolean updateFlag = false;
    private String[] mTitle = new String[3];


    public static BasestationSettingFragment getInstance(Bundle bundle) {
        BasestationSettingFragment backPressHandledFragment = new BasestationSettingFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        FileExplorerEvents.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FileExplorerEvents.getBus().unregister(this);
    }

    private void doOpenDirectory(String path, int fragmentIndex) {
        updatePath = path;
        updateFragmentIndex = fragmentIndex;
        updateFlag = true;
        netTypePagerAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onClickFile(FileExplorerEvents.OnClickFile event) {
        Log.d("hahaha", "onEvnet");
        File f = event.mFile;
        int fragmentIndex = event.mIndex;
        try {
            f = f.getAbsoluteFile();
            f = f.getCanonicalFile();
            if (TextUtils.isEmpty(f.toString()))
                f = new File("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (f.isDirectory()) {
            String path = f.toString();
            doOpenDirectory(path, fragmentIndex);
        } else if (f.exists()) {
            Cell.CellType cellType;
           switch (updateFragmentIndex) {
               case 0:
                   cellType = Cell.CellType.GSM;
                   break;
               case 2:
                   cellType = Cell.CellType.LTE;
                   break;
               default:
                   cellType = Cell.CellType.LTE;
                   break;
           }
             CsvToSqliteHelper dbhelp=new CsvToSqliteHelper(getActivity(),Constants.DBNAME,cellType,f.toString(),1);
             SQLiteDatabase mydb= dbhelp.getReadableDatabase();

           // CsvToSqliteHelper.createGsmTable(mydb);
            CsvToSqliteHelper.createCellTable(mydb, f.toString(), cellType);

        }
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logs.d("OfflineMapModule", "OfflineMapFragment onCreateView");
        View root = initView(inflater);
        return root;
    }

    private View initView(LayoutInflater inflater) {
        Logs.d("OfflineMapModule", "OfflineMapFragment initView");
        View root = inflater.inflate(R.layout.fragment_basestationsetting, null);
        mViewPager = (ViewPager) root.findViewById(R.id.vp_container);
        View drawerIcon = root.findViewById(R.id.drawer_icon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openOrCloseDrawers();
            }
        });
        mTitle = new String[]{
                "GSM",
                "TDS-CDMA",
                "TD-LTE"
        };
        String path = "/";
        fragment2g = BasestationImportFragment.newInstance(path, 0);
        fragment3g = BasestationImportFragment.newInstance(path, 1);
        fragment4g = BasestationImportFragment.newInstance(path, 2);
        mFragments.add(fragment2g);
        mFragments.add(fragment3g);
        mFragments.add(fragment4g);
        netTypePagerAdapter = new NetTypePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(netTypePagerAdapter);
        mTabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        mTabLayout.setOnTabSelectedListener(mTabListener);
        mTabLayout.setupWithViewPager(mViewPager);
        final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
        mViewPager.addOnPageChangeListener(listener);
        doOpenDirectory("/sdcard/", 0);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class NetTypePagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;

        public NetTypePagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container,
                    position);
            //得到tag，这点很重要
            String fragmentTag = fragment.getTag();
            if (updateFragmentIndex == position && updateFlag == true) {
                //如果这个fragment需要更新
                Logs.d("hahaha", " update enter");
                FragmentTransaction ft = fm.beginTransaction();
                //移除旧的fragment
                ft.remove(fragment);
                //换成新的fragment
                fragment = BasestationImportFragment.newInstance(updatePath, position);
                //添加新fragment时必须用前面获得的tag，这点很重要
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();
                //复位更新标志
                updateFlag = false;
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
