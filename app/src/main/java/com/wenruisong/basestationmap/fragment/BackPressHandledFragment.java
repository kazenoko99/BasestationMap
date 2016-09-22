package com.wenruisong.basestationmap.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wenruisong.basestationmap.listener.CallBack;
import com.wenruisong.basestationmap.listener.IMapCommon;
import com.wenruisong.basestationmap.offlinemap.AynscWorkListener;
import com.wenruisong.basestationmap.utils.Logs;

import java.util.List;

public abstract class BackPressHandledFragment extends BaseFragment implements CallBack {
    public static final String TAG = BackPressHandledFragment.class.getSimpleName();

    protected Handler mUnMainHandler;
    protected HandlerThread mHandlerThread;
    protected Handler mMainHandler;
    protected IMapCommon.FragShower mFragShower;
    protected IMapCommon.NavOnClickProvider mNavOnClickProvider;
    protected IMapCommon.BarChangeListener mBarChangeListener;
    protected boolean mHasSetToolBarAfterViewCreated = false;//bug#203525

    public Toolbar mToolbar;

    public BackPressHandledFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHandler = new Handler();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mBarChangeListener != null && !hidden) {
            mBarChangeListener.onBarChanged(this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBarChangeListener != null) {
            mBarChangeListener.onBarChanged(this);
        }
        if (getContext().getSupportActionBar() != null) {
            mHasSetToolBarAfterViewCreated = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    public void setFragShower(IMapCommon.FragShower mFragShower) {
        this.mFragShower = mFragShower;
    }


    public IMapCommon.FragShower getFragShower() {
        return mFragShower;
    }

    public boolean hasFragShower() {
        return mFragShower == null ? false : true;
    }

    /**
     * 携带结果的返回到前一个fragment,会自己处理back返回,如果在#handleBackPress方法中回调，return true
     *
     * @param bundle
     */
    @Override
    public void backResult(final Bundle bundle) {
        if (getTargetFragment() == null) {
            return;
        }
        super.backResult(bundle);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getTargetFragment() == null) {
                    return;
                }
                ((CallBack) getTargetFragment()).onBackResult(bundle);
                setTargetFrag(null, 0);
                getContext().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public void onBackResult(final Bundle bundle) {
        super.onBackResult(bundle);
        startUnMainThreadifNeed();
    }

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
        startUnMainThreadifNeed();
        View root = inflate(inflater, container, savedInstanceState);
        return root;
    }

    protected View inflate(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public AppCompatActivity getContext() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        return activity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        quitUnMainThread();
    }

    @Override
    public void onDestroy() {
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler = null;
        mFragShower = null;
        mNavOnClickProvider = null;
        mBarChangeListener = null;
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void quitUnMainThread() {
        if (mHandlerThread != null) {
            mUnMainHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quitSafely();
            mHandlerThread = null;
            mUnMainHandler = null;
        }
    }

    protected void startUnMainThreadifNeed() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread(getClass().getSimpleName());
            mHandlerThread.start();
            mUnMainHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    public boolean handleBackPress() {
        return false;
    }

    public Handler getUnMainHandler() {
        startUnMainThreadifNeed();
        return mUnMainHandler;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    protected void doWorkAynsc(final AynscWorkListener aynscWorkListener) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Logs.e("MapViewFragment", "doWorkAsync");
               // showDialog(ResourcesUtil.getString(R.string.map_loading), null);
                aynscWorkListener.onPreExcuted();
                getUnMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Object temp = null;
                        if (aynscWorkListener != null) {
                            temp = aynscWorkListener.doInBackground();
                        }
                        final Object object = temp;
                        if (mMainHandler == null) {
                            return;
                        }
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (aynscWorkListener != null && object != null) {
                                        if (object instanceof List && ((List) object).size() <= 0) {
                                            aynscWorkListener.onPostExcuted();
                                        } else {
                                            aynscWorkListener.onPostResult(object);
                                        }

                                    } else if (aynscWorkListener != null && object == null) {
                                        aynscWorkListener.onPostExcuted();
                                    }
                                   // dismissDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
            }
        });
    }

    protected void doWorkAynsc(final AynscWorkListener aynscWorkListener, final boolean showDialogInner) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (showDialogInner) {
                    //showDialog(ResourcesUtil.getString(R.string.map_loading), null);
                }
                aynscWorkListener.onPreExcuted();
                getUnMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Object temp = null;
                        if (aynscWorkListener != null) {
                            temp = aynscWorkListener.doInBackground();
                        }
                        final Object object = temp;
                        if (mMainHandler == null) {
                            return;
                        }
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (aynscWorkListener != null && object != null) {
                                        if (object instanceof List && ((List) object).size() <= 0) {
                                            aynscWorkListener.onPostExcuted();
                                        } else {
                                            aynscWorkListener.onPostResult(object);
                                        }

                                    } else if (aynscWorkListener != null && object == null) {
                                        aynscWorkListener.onPostExcuted();
                                    }
                                    if (showDialogInner) {
                                      //  dismissDialog();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
            }
        });
    }


}
