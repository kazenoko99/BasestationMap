package com.wenruisong.basestationmap.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenruisong.basestationmap.LoginActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.DrawerListAdapter;
import com.wenruisong.basestationmap.group.User;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

public class NavigationDrawerFragment extends Fragment {
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private TextView mLogin;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout)inflater.inflate(
                R.layout.drawer_view, container, false);
        mDrawerListView = (ListView)rootView.findViewById(R.id.drawer_list);
        mLogin = (TextView)rootView.findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        initList();
        mDrawerListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       selectItem(position);
                                                   }
                                               });

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return rootView;
    }

    public void initUser(){
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        if (user != null) {
            mLogin.setText(user.getEmail());
        }

    }
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
        // Defer code dependent on restoration of previous instance state.
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    private void initList() {
        // 初始化名字
        String[] arrays = getResources().getStringArray(R.array.drawer_list);
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < arrays.length; i++) {
            arrayList.add(arrays[i]);
        }

        // 初始化图标
        ArrayList<Integer> iconList = new ArrayList<>();
        iconList.add(R.drawable.drawer_map);
        iconList.add(R.drawable.map_icon_basestation);
        iconList.add(R.drawable.drawer_collected);
        iconList.add(R.drawable.drawer_offlinemap);
        iconList.add(R.drawable.drawer_tools);
        iconList.add(R.drawable.drawer_group);
        iconList.add(R.drawable.drawer_setting);

        DrawerListAdapter adapter = new DrawerListAdapter(getActivity(), arrayList, iconList);
        mDrawerListView.setFooterDividersEnabled(false);
        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
        });
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
