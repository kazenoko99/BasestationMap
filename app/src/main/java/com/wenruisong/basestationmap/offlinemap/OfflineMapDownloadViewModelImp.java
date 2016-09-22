package com.wenruisong.basestationmap.offlinemap;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.fragment.BackPressHandledFragment;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.NetUtil;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
import com.wenruisong.basestationmap.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于处理OfflineMapDownload页面的UI逻辑
 * <p/>
 * Created by yinjianhua on 15-6-12.
 */
public class OfflineMapDownloadViewModelImp implements OfflineMapDownloadViewModel {

    private BackPressHandledFragment mFragment;
    //    private String mDownloadingCityName = "";
//    private int mDownloadingComplete = 0;
//    private String mUnzipingCityName = "";
//    private int mUnzipingComplete = 0;
    private OfflineMapDownloadView mView;
    private List<OfflineMapCity> mAllDownloadCityList;
    private boolean isShouldShowRecommendView = false;

    public OfflineMapDownloadViewModelImp(BackPressHandledFragment fragment, OfflineMapDownloadView view) {
        mFragment = fragment;
        mView = view;
        // 注册下载监听
        OfflineMapLoader.getInstance().registerOfflineMapDownloadListener(this);
        OfflineMapLoader.getInstance().registerOfflineMapPauseAllListener(this);
        mAllDownloadCityList = OfflineMapLoader.getInstance().getAllDownloadCityList();
        if ((mAllDownloadCityList == null) || (mAllDownloadCityList.size() == 0)) {
            isShouldShowRecommendView = true;
        } else {
            isShouldShowRecommendView = false;
        }
    }

    @Override
    public List<OfflineMapCity> getAllDownloadCitys() {
        mAllDownloadCityList = OfflineMapLoader.getInstance().getAllDownloadCityList();
        return mAllDownloadCityList;
    }

    @Override
    public HashMap<String, Integer> getCityDownloadCompleteMap() {
        return OfflineMapLoader.getInstance().getCityDownloadCompleteMap();
    }

    @Override
    public HashMap<String, Integer> getCityUnzipCompleteMap() {
        return OfflineMapLoader.getInstance().getCityUnzipCompleteMap();
    }

//    @Override
//    public String getDownloadingCityName() {
//        return mDownloadingCityName;
//    }
//
//    @Override
//    public int getDownloadingComplete() {
//        return mDownloadingComplete;
//    }
//
//    @Override
//    public String getUnzipingCityName() {
//        return mUnzipingCityName;
//    }
//
//    @Override
//    public int getUnzipingComplete() {
//        return mUnzipingComplete;
//    }

    @Override
    public void onDownloadStart(OfflineMapCity city) {

    }

    @Override
    public void onDownloadFail(String cityName, String errorMessage) {
//        ToastUtil.show(mFragment.getContext(), cityName + ": errorMessage: "+errorMessage);
        Log.d("OfflineMapModule", cityName + ": errorMessage: " + errorMessage);

        if (AMapException.ERROR_NOT_ENOUGH_SPACE.equals(errorMessage)) {
            AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                    .setMessage(ResourcesUtil.getString(R.string.offline_map_space_not_enough))
                    .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).create();
            dialog.show();
        }
    }

    @Override
    public void onDownload(int status, int completeCode, String cityName) {
        Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp onDownload: status: "
                + status + " completeCode:  " + completeCode + " cityName: " + cityName);

        if (isShouldShowRecommendView) {
            isShouldShowRecommendView = false;
            mView.dismissRecommendView();
        }

        switch (status) {
            case OfflineMapStatus.CHECKUPDATES:
                mView.updateList();
                break;
            case OfflineMapStatus.LOADING:
//                mDownloadingCityName = cityName;
//                mDownloadingComplete = completeCode;
                mView.updateList();
                break;
            case OfflineMapStatus.UNZIP:

                mView.updateList();
                break;
            case OfflineMapStatus.SUCCESS:
                mView.updateList();
                break;
            case OfflineMapStatus.WAITING:
                mView.updateList();
                break;
            case OfflineMapStatus.PAUSE:
                mView.updateList();
                break;
        }
    }

    @Override
    public void onCityCanUpdate(String cityName) {

    }

    @Override
    public void onOfflineMapListChange() {
        if (!isShouldShowRecommendView) {
            mView.updateList();
        }else{
            List<OfflineMapCity> downloadList = getAllDownloadCitys();
            if((downloadList!=null)&&(downloadList.size()>0)) {
                isShouldShowRecommendView = false;
                mView.dismissRecommendView();
            }
        }
    }


    @Override
    public View.OnClickListener getStopClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp StopClick");

                Object obj = v.getTag();
                String cityName = (String) obj;
                OfflineMapLoader.getInstance().cancelDownload(cityName);
            }
        };
    }

    @Override
    public void onDestory() {
        Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp onDestory");

        if (OfflineMapLoader.getInstance() != null) {
            OfflineMapLoader.getInstance().unregisterOfflineMapDownloadListener(this);
            OfflineMapLoader.getInstance().unregisterOfflineMapPauseAllListener(this);
        }

    }


    @Override
    public boolean isShouldShowRecommendView() {
        if ((mAllDownloadCityList == null) || mAllDownloadCityList.size() == 0) {
            return true;
        }

        return false;
    }


    @Override
    public List<OfflineMapCity> getRecommendCitys() {

        List<OfflineMapCity> list = new ArrayList<>();
        OfflineMapCity city = OfflineMapLoader.getInstance().getItemByCityName(
                ResourcesUtil.getString(R.string.offline_map_city_gaiyaotu));
//        city.setState(OfflineMapLoader.EMPTY_VALUE);

        list.clear();
        list.add(city);

        return list;
    }

    @Override
    public View.OnClickListener getDownloadCtrlClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj = v.getTag();

                OfflineMapCity city = (OfflineMapCity) obj;
                final String cityName = city.getCity();
                int downloadStatue = city.getState();

                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick: "
                        + " cityName: " + cityName + " downloadStatue: " + downloadStatue);
                NetUtil.NetType netType = NetUtil.getInstance().getCurrentNetType();

                switch (downloadStatue) {
                    case OfflineMapStatus.SUCCESS:
                        // 判断是进行更新还是进入查看
                        if ((getCanUpdateCityList() != null) && (getCanUpdateCityList().contains(cityName))) {
                            // 是否有足够的存储空间
                            if (OfflineMapLoader.getInstance().isHavEnoughSpace(cityName)) {
                                if (netType == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                    Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload no wifi");
                                    ToastUtil.show(mFragment.getContext(),"网络接连出错");

                                    return;
                                } else if (netType == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {
                                    Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload 使用移动流量");

                                    new AlertDialog.Builder(mFragment.getContext())
                                            .setIconAttribute(android.R.attr.alertDialogIcon)
                                            .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                            .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    OfflineMapLoader.getInstance().updateDownloadedCity(cityName);
                                                }
                                            })
                                            .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .create()
                                            .show();

                                } else {
                                    OfflineMapLoader.getInstance().updateDownloadedCity(cityName);
                                }
                            } else {    // 手机空间不足的弹框
                                AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                                        .setMessage(ResourcesUtil.getString(R.string.offline_map_space_not_enough))
                                        .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok)
                                                , new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub

                                            }
                                        }).create();
                                dialog.show();
                            }
                        }

                        break;
                    case OfflineMapStatus.UNZIP:
                        break;
//                    case OfflineMapStatus.CHECKUPDATES:
                    case OfflineMapStatus.LOADING:
                        OfflineMapLoader.getInstance().pauseCurDownload(cityName);
                        break;
                    case OfflineMapStatus.PAUSE:
                        // 是否有足够的存储空间
                        if (OfflineMapLoader.getInstance().isHavEnoughSpace(cityName)) {
                            if (netType == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload no wifi");
                                ToastUtil.show(mFragment.getContext(),"网络接连出错");

                                return;
                            } else if (netType == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {
                                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload 使用移动流量");

                                new AlertDialog.Builder(mFragment.getContext())
                                        .setIconAttribute(android.R.attr.alertDialogIcon)
                                        .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                        .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                OfflineMapLoader.getInstance().continueDownload(cityName);
                                            }
                                        })
                                        .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        })
                                        .create()
                                        .show();

                            } else {
                                OfflineMapLoader.getInstance().continueDownload(cityName);
                            }
                        } else {    // 手机空间不足的弹框
                            AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                                    .setMessage(ResourcesUtil.getString(R.string.offline_map_space_not_enough))
                                    .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                        }
                                    }).create();
                            dialog.show();
                        }


                        break;
                    case OfflineMapStatus.WAITING:
                        OfflineMapLoader.getInstance().cancelDownload(cityName);
                        break;
                    case OfflineMapStatus.ERROR:
                        Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload");

                        // 是否有足够的存储空间
                        if (OfflineMapLoader.getInstance().isHavEnoughSpace(cityName)) {
                            if (netType == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload no wifi");
                                ToastUtil.show(mFragment.getContext(),"网络接连出错");

                                return;
                            } else if (netType == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {
                                Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick redownload 使用移动流量");

                                new AlertDialog.Builder(mFragment.getContext())
                                        .setIconAttribute(android.R.attr.alertDialogIcon)
                                        .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                        .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                OfflineMapLoader.getInstance().reDownload(cityName);
                                            }
                                        })
                                        .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        })
                                        .create()
                                        .show();

                            } else {
                                OfflineMapLoader.getInstance().reDownload(cityName);
                            }
                        } else {    // 手机空间不足的弹框
                            AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                                    .setMessage(ResourcesUtil.getString(R.string.offline_map_space_not_enough))
                                    .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub

                                        }
                                    }).create();
                            dialog.show();
                        }


                        break;
                    case OfflineMapStatus.CHECKUPDATES:
                        if (isShouldShowRecommendView) {
                            // 是否有足够的存储空间
                            if (OfflineMapLoader.getInstance().isHavEnoughSpace(cityName)) {
                                if (netType == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                    Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick download no wifi");
                                    ToastUtil.show(mFragment.getContext(),"网络接连出错");

                                    return;
                                } else if (netType == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {
                                    Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp DownloadCtrlClick download 使用移动流量");

                                    new AlertDialog.Builder(mFragment.getContext())
                                            .setIconAttribute(android.R.attr.alertDialogIcon)
                                            .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                            .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    isShouldShowRecommendView = false;
                                                    OfflineMapLoader.getInstance().downloadOfflineCityMap(cityName);
                                                    mView.dismissRecommendView();
                                                }
                                            })
                                            .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .create()
                                            .show();

                                } else {
                                    isShouldShowRecommendView = false;
                                    OfflineMapLoader.getInstance().downloadOfflineCityMap(cityName);
                                    mView.dismissRecommendView();
                                }
                            } else {

                                AlertDialog dialog = new AlertDialog.Builder(mFragment.getContext())
                                        .setMessage(ResourcesUtil.getString(R.string.offline_map_space_not_enough))
                                        .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub

                                            }
                                        }).create();
                                dialog.show();

                            }
                        }

                        break;
                }
            }
        };
    }

    @Override
    public boolean onItemLongClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
//        mFragment.getFragShower().showFragments(MainMapActivity.FRAG_OFFLINE_MAP_DELETE, true, false);

        final String cityName;
        final boolean isUnzipIng;

        if ((mAllDownloadCityList != null) && (mAllDownloadCityList.size() > position)) {
            cityName = mAllDownloadCityList.get(position).getCity();
            int downloadState = mAllDownloadCityList.get(position).getState();

            Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp ItemLongClick: "
                    + " cityName: " + cityName + " downloadState: " + downloadState);

            if (downloadState == OfflineMapStatus.UNZIP) {
                isUnzipIng = true;
            } else {
                isUnzipIng = false;
            }
        }


        return true;
    }

    @Override
    public List<String> getCanUpdateCityList() {
        return OfflineMapLoader.getInstance().getCanUpdateCityList();
    }


    @Override
    public void pauseAllDownloadCauseWifiStop() {
        Logs.d("OfflineMapModule", "OfflineMapDownloadViewModelImp pauseAllDownloadCauseWifiStop");

        if ((mFragment != null) && (mFragment.getContext() != null) && (mFragment.isResumed())) {
//            ToastUtil.show(mFragment.getContext(),
//                    ResourcesUtil.getString(R.string.offline_map_download_null_network_pause_all));

        }
    }
}
