package com.wenruisong.basestationmap.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.wenruisong.basestationmap.listener.CallBack;
import com.wenruisong.basestationmap.utils.Logs;


public abstract class BaseFragment extends Fragment implements View.OnTouchListener, CallBack {
    protected Bundle mBundle;
    protected View root;

//    public static interface BackPressFragmentListener {
//        public MapServiceImpl getServiceManager();
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (BackPressFragmentListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement BackPressFragmentListener");
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        root = null;
//        mListener = null;
//        super.onDestroy();
//    }

    public BaseFragment() {

    }

    public void setTargetFrag(BaseFragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    public void setParamter(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mBundle = bundle;
    }

    public boolean handleBackPress() {
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    protected MapServiceImpl getServiceManager() {
//        if (mListener != null)
//            return mListener.getServiceManager();
//        return null;
//    }

    /**
     * 查看#inflateView方法
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            root = inflateView(inflater, container, savedInstanceState);
        }
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null) {
                parent.removeView(root);
            }
        }
        return root;
    }

    /**
     * 子类重写该方法布局，#onCreateView方法中已经重用了布局操作
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public abstract View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void backResult(final Bundle bundle) {
        Logs.e("BaseFragment", "backResult getTargetFragment=" + getTargetFragment());
        if (getTargetFragment() == null) {
           return;
        }
        if (!(getTargetFragment() instanceof CallBack)) {
            throw new RuntimeException("targetFragment must be instance of CallBack");
        }
    }

    @Override
    public void onBackResult(Bundle bundle) {

    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle mBundle) {
        this.mBundle = mBundle;
    }
}
