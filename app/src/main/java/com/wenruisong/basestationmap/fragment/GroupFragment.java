package com.wenruisong.basestationmap.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.GroupAdapter;
import com.wenruisong.basestationmap.group.Group;
import com.wenruisong.basestationmap.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends BackPressHandledFragment implements View.OnClickListener {
    private final static String TAG = GroupFragment.class.getSimpleName();
    private RecyclerView mGroupRecyclerView;
    private GroupAdapter mGroupAdapter;
    private ArrayList<Group> mGroups = new ArrayList<>();
    private ImageView mSearchImageView, mNavigatorImageView,mGreateGroupImageView;
    private MainActivity mMainActivity;
    public GroupFragment() { }

    public static GroupFragment getInstance(Bundle bundle) {
        GroupFragment backPressHandledFragment = new GroupFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mMainActivity = (MainActivity) getActivity();
        mGroupRecyclerView = (RecyclerView) view.findViewById(R.id.my_group);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupAdapter = new GroupAdapter(getContext(),mGroups);
        mGroupRecyclerView.setAdapter(mGroupAdapter);
//        mSearchImageView = (ImageView)view.findViewById(R.id.search_group) ;
//        mGreateGroupImageView = (ImageView)view.findViewById(R.id.create_group) ;
//        mNavigatorImageView = (ImageView)view.findViewById(R.id.drawer_icon) ;
//        mNavigatorImageView.setOnClickListener(this);
//        mGreateGroupImageView.setOnClickListener(this);
//        mSearchImageView.setOnClickListener(this);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
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
        LoadingGroupDate();
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

    public void LoadingGroupDate() {
        BmobQuery<Group> query = new BmobQuery<Group>();
        query.order("-createdAt");
//		query.setCachePolicy(CachePolicy.NETWORK_ONLY);
//        query.setLimit(Constants.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        Log.i(TAG, "TIME:" + date.toString());
        query.findObjects(getContext(), new FindListener<Group>() {

            @Override
            public void onSuccess(List<Group> list) {
                // TODO Auto-generated method stub
                Log.i(TAG, "find success." + list.size());
                if (list.size() != 0) {
                    mGroups.addAll(list);
                    mGroupAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(getContext(), "暂无更多数据~");
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastUtil.show(getContext(), "网络连接出错~");
            }


        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mToolbar.inflateMenu(R.menu.menu_frangment_group);
    }
}
