package com.wenruisong.basestationmap.offlinemap;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
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
 * Created by yinjianhua on 15-6-12.
 */
public class OfflineMapCityViewModelImp implements OfflineMapCityViewModel {

    private final int ZHIXIASHI_INDEX = 0;
    private final int GANGAO_INDEX = 1;

    private OfflineMapLoader mLoader;
    private List<OfflineMapProvince> mCityGroupList = new ArrayList<>();// 保存一级目录的省直辖市
    private HashMap<Object, List<OfflineMapCity>> mCityMap = new HashMap<>();// 保存二级目录的市
    private List<OfflineMapCity> mSearchResultList = new ArrayList<>(); // 保存搜索结果
    private OfflineMapCityView mView;
    private String mOldDownName = null;
    private int mOldDownloadStatus = OfflineMapStatus.CHECKUPDATES;
    private boolean[] mGroupOpenArray;// 记录一级目录是否打开
    private BackPressHandledFragment mFragment;

    private String mDownloadingCityName = "";
    private int mDownloadingComplete = 0;
    private String mUnzipingCityName = "";
    private int mUnzipingComplete = 0;

    public OfflineMapCityViewModelImp(BackPressHandledFragment fragment, OfflineMapCityView view) {
        mView = view;
        mFragment = fragment;
        mLoader = OfflineMapLoader.getInstance();
        mLoader.registerOfflineMapDownloadListener(this);   // 注册下载监听
    }

    public void initData() {
        Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp initData");

        initCityList();
        updateProvinceState();
    }

    private void initCityList() {
        List<OfflineMapCity> bigCityList = new ArrayList<>();// 以省格式保存直辖市、港澳、全国概要图
//        List<OfflineMapCity> cityList = new ArrayList<>();// 以市格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> gangaoList = new ArrayList<>();// 保存港澳城市
        List<OfflineMapCity> zhixiashiList = new ArrayList<>();// 保存直辖市
        final List<OfflineMapProvince> provinceList = mLoader.getOfflineMapProvinceList();


        mCityGroupList.clear();
        for (int i = 0; i < provinceList.size(); i++) {
            OfflineMapProvince offlineMapProvince = provinceList.get(i);
            List<OfflineMapCity> city = new ArrayList<>();
            OfflineMapCity aMapCity = getCicy(offlineMapProvince);  // 省或直辖市或港澳或全国概要图

            if (offlineMapProvince.getCityList().size() != 1) {
                city.add(aMapCity);
                for (OfflineMapCity offlineMapCity : offlineMapProvince.getCityList()) {
                    city.add(mLoader.getItemByCityName(offlineMapCity.getCity()));
                }
                mCityGroupList.add(offlineMapProvince);
                mCityMap.put(i + GANGAO_INDEX + 1, city); // 放在港澳列表下面

            } else {
                OfflineMapCity spicalCity = mLoader.getItemByCityName(aMapCity.getCity());
                bigCityList.add(spicalCity);
            }
        }

        OfflineMapProvince title;
        title = new OfflineMapProvince();
        title.setProvinceName(ResourcesUtil.getString(R.string.offline_map_city_zhixiashi));
        mCityGroupList.add(ZHIXIASHI_INDEX, title);
        title = new OfflineMapProvince();
        title.setProvinceName(ResourcesUtil.getString(R.string.offline_map_city_gangao));
        mCityGroupList.add(GANGAO_INDEX, title);


        for (OfflineMapCity bigCity : bigCityList) {
            if (bigCity != null) {
                if (bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_hongkong))
                        || bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_macau))) {
                    gangaoList.add(bigCity);

                } else if (bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_beijing))
                        || (bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_shanghai)))
                        || (bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_tianjin)))
                        || (bigCity.getCity().contains(ResourcesUtil.getString(R.string.offline_map_city_chongqing)))) {
                    zhixiashiList.add(bigCity);
                }
            }
        }
        mCityMap.put(ZHIXIASHI_INDEX, zhixiashiList);// 在HashMap中第0位置添加直辖市
        mCityMap.put(GANGAO_INDEX, gangaoList);// 在HashMap中第1位置添加港澳

        mGroupOpenArray = new boolean[mCityGroupList.size()];
    }

    @Override
    public void onDestory() {
        Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp onDestory");

        mLoader.unregisterOfflineMapDownloadListener(this);
    }

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }


    private void searchCity(String key) {
        mSearchResultList.clear();

        for (int i = 0; i < mCityMap.size(); i++) {
            List<OfflineMapCity> citys = mCityMap.get(i);

            for (OfflineMapCity city : citys) {
                if (city.getCity().contains(key)) {
                    mSearchResultList.add(city);
                }
            }
        }

        if (ResourcesUtil.getString(R.string.offline_map_city_gaiyaotu).contains(key)) {
            OfflineMapCity city = mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_gaiyaotu));
            mSearchResultList.add(city);
        }
        mView.updateSearchList(mSearchResultList);
    }

    private List<OfflineMapCity> updateSearchResultList(List<OfflineMapCity> resultList) {
        List<OfflineMapCity> temp = new ArrayList<>();

        for (OfflineMapCity city : resultList) {
            boolean isProvince = mLoader.isProvince(city.getCity());
            if (isProvince) {
                int downloadState = getProvinceDownloadState(city.getCity());
                city.setState(downloadState);
                temp.add(city);
            } else {
                temp.add(mLoader.getItemByCityName(city.getCity()));
            }
        }
        return temp;
    }

    /**
     * 获取省的下载状态
     *
     * @param provinceName
     * @return
     */
    private int getProvinceDownloadState(String provinceName) {
        OfflineMapProvince province = mLoader.getItemByProvinceName(provinceName);
        int provinceState = OfflineMapStatus.CHECKUPDATES;

        if (province == null) return provinceState;

        for (OfflineMapCity city : province.getCityList()) {
            String cityName = city.getCity();
            int state = mLoader.getItemByCityName(cityName).getState();

            // 以下判断的顺序不能变
            if (state == OfflineMapStatus.CHECKUPDATES) {
                provinceState = OfflineMapStatus.CHECKUPDATES;
                return provinceState;

            } else if ((state == OfflineMapStatus.LOADING)
                    || (state == OfflineMapStatus.UNZIP)) {

                provinceState = OfflineMapStatus.LOADING;

            } else if (provinceState != OfflineMapStatus.LOADING) {

                if (state == OfflineMapStatus.WAITING) {
                    provinceState = OfflineMapStatus.WAITING;
                } else if (state == OfflineMapStatus.PAUSE) {
//                    provinceState = OfflineMapStatus.PAUSE;
                    provinceState = OfflineMapStatus.CHECKUPDATES;
                    return provinceState;

                } else if (state == OfflineMapStatus.SUCCESS) {
                    provinceState = OfflineMapStatus.SUCCESS;
                } else if (state == OfflineMapStatus.ERROR) {
                    provinceState = OfflineMapStatus.CHECKUPDATES;
                    return provinceState;
                }
            }
        }
        return provinceState;
    }

    private void updateProvinceState() {
        for (int i = 0; i < mCityMap.size(); i++) {
            List<OfflineMapCity> cityList = mCityMap.get(i);
            if ((cityList != null) && (cityList.size() > 0) && (cityList.get(0) != null)) {
                String provinceName = cityList.get(0).getCity();
                boolean isProvince = mLoader.isProvince(provinceName);
                if (isProvince) {
                    int provinceState = getProvinceDownloadState(provinceName);
                    cityList.get(0).setState(provinceState);
                }
            }
        }
    }

    /**
     * 更新CityMap里面的直辖市,港澳和全国概要图
     */
    private void updateSpecialCity() {
        List<OfflineMapCity> zhixiashiList = new ArrayList<>();// 保存直辖市
        List<OfflineMapCity> gangaoList = new ArrayList<>();// 保存港澳城市

        zhixiashiList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_beijing)));
        zhixiashiList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_shanghai)));
        zhixiashiList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_tianjin)));
        zhixiashiList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_chongqing)));
        gangaoList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_hongkong)));
        gangaoList.add(mLoader.getItemByCityName(ResourcesUtil.getString(R.string.offline_map_city_macau)));

        mCityMap.put(ZHIXIASHI_INDEX, zhixiashiList);
        mCityMap.put(GANGAO_INDEX, gangaoList);
    }

    @Override
    public List<OfflineMapCity> updateHotCityList() {
        Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp updateHotCityList");
        List<OfflineMapCity> hotCityList = new ArrayList<>();

        String[] hotCityStrings = ResourcesUtil.getStringArray(R.array.offline_map_hot_citys);
        for (int i = 0; i < hotCityStrings.length; i++) {
//            hotCityList.add(mLoader.getItemByCityName(hotCityStrings[i]));
            OfflineMapCity city = mLoader.getItemByCityName(hotCityStrings[i]);
            if (null != city) {
                hotCityList.add(city);
            }
        }

        return hotCityList;
    }

    @Override
    public List<OfflineMapProvince> getProvinceList() {
        return mCityGroupList;
    }

    @Override
    public HashMap<Object, List<OfflineMapCity>> getCityMap() {
        return mCityMap;
    }


    @Override
    public void onDownloadStart(OfflineMapCity city) {

    }

    @Override
    public void onDownloadFail(String cityName, String errorMessage) {

    }

    @Override
    public void onDownload(int status, int completeCode, String cityName) {
        if ((!cityName.equals(mOldDownName)) || (mOldDownloadStatus != status)
                ) {   // 避免多次更改下载状态

            // 更新搜索结果列表
            if (mSearchResultList.size() > 0) {
//                mSearchResultList = updateSearchResultList(mSearchResultList);
                mView.updateSearchList(mSearchResultList);
            } else {
                // FIXME:以后要优化这块代码,优化更新列表的方式
                // 更新城市列表
//                updateSpecialCity();
                updateProvinceState();
                mLoader.getItemByCityName(cityName);
                mView.updateCityList();
            }

            mOldDownName = cityName;
            mOldDownloadStatus = status;
        }
    }

    @Override
    public void onCityCanUpdate(String cityName) {

    }

    @Override
    public void onOfflineMapListChange() {
        // 更新搜索结果列表
        if (mSearchResultList.size() > 0) {
            mSearchResultList = updateSearchResultList(mSearchResultList);
            mView.updateSearchList(mSearchResultList);
        } else {
            // 更新城市列表
//            updateSpecialCity();
            updateProvinceState();
            mView.updateCityList();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {   // 如果已经输入了关键字
            searchCity(s.toString());
            mView.setSearchEditHint(null);
            mView.setCityListViewVisibility(View.GONE);
            mView.setSearchListViewVisibility(View.VISIBLE);
        } else {
            mView.setSearchEditHint(ResourcesUtil.getString(R.string.offline_map_city_search_hint));
            mView.setCityListViewVisibility(View.VISIBLE);
            mView.setSearchListViewVisibility(View.GONE);
            mSearchResultList.clear();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        mGroupOpenArray[groupPosition] = false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        mGroupOpenArray[groupPosition] = true;
    }

    @Override
    public boolean[] getGroupOpenArray() {
        return mGroupOpenArray;
    }

    @Override
    public String getDownloadingCityName() {
        return mDownloadingCityName;
    }

    @Override
    public int getDownloadingComplete() {
        return mDownloadingComplete;
    }

    @Override
    public String getUnzipingCityName() {
        return mUnzipingCityName;
    }

    @Override
    public int getUnzipingComplete() {
        return mUnzipingComplete;
    }

    @Override
    public View.OnClickListener getDownloadBtnListener() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                NetUtil.NetType type = NetUtil.getInstance().getCurrentNetType();
                final OfflineMapCity city = (OfflineMapCity) v.getTag();
                final String cityName = city.getCity();
                final int status = city.getState();


                String name = cityName;
                if (name instanceof String) {
                    // 判断是按照省还是城市来下载
                    if (OfflineMapLoader.getInstance() != null) {

                        if (OfflineMapLoader.getInstance().isProvince(name)) {  // 按省份的下载

                            // 是否有足够的存储空间
                            if (OfflineMapLoader.getInstance().isHavEnoughSpace(name, true)) {

                                if (type == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download no wifi");
                                    ToastUtil.show(mFragment.getContext(),"网络连接失败！");
                                    return;

                                } else if (type == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {

                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download 使用移动流量");
                                    new AlertDialog.Builder(mFragment.getContext())
                                            .setIconAttribute(android.R.attr.alertDialogIcon)
                                            .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                            .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    String name = cityName;
                                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download Province" + name);

                                                    mLoader.downloadOfflineProvinceMap(name);
                                                    return;
                                                }
                                            })
                                            .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .create()
                                            .show();

                                } else {
                                    mLoader.downloadOfflineProvinceMap(name);
                                    return;
                                }
                            } else { // 存储空间不足
                                // 手机空间不足的弹框
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


                        } else {    // 按城市来下载
                            // 是否有足够的存储空间
                            if (OfflineMapLoader.getInstance().isHavEnoughSpace(name)) {
                                if (type == NetUtil.NetType.NETWORK_TYPE_NONE) {
                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download no wifi");
                                    ToastUtil.show(mFragment.getContext(),"网络连接失败！");
                                    return;

                                } else if (type == NetUtil.NetType.NETWOKR_TYPE_MOBILE) {

                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download 使用移动流量");
                                    new AlertDialog.Builder(mFragment.getContext())
                                            .setIconAttribute(android.R.attr.alertDialogIcon)
                                            .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
                                            .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    String name = cityName;
                                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download Province" + name);

                                                    if (OfflineMapStatus.ERROR == status) {
                                                        mLoader.reDownload(name);
                                                    } else {
                                                        mLoader.downloadOfflineCityMap(name);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .create()
                                            .show();

                                } else {
                                    if (OfflineMapStatus.ERROR == status) {
                                        mLoader.reDownload(name);
                                    } else {
                                        mLoader.downloadOfflineCityMap(name);
                                    }
                                    return;
                                }

                            } else { //存储空间不足

                                // 手机空间不足的弹框
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
                    }
                }


            }

//
//            if(type==NetUtil.NetType.NETWORK_TYPE_NONE)
//
//            {
//                Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download no wifi");
//                // 显示无网络的对话框
//                DialogUtil.showNoNetWorkDialog(mFragment.getContext());
//
//                return;
//            }
//
//            else if(type==NetUtil.NetType.NETWOKR_TYPE_MOBILE)
//
//            {
//                Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp ChildClick download 使用移动流量");
//                new AlertDialog.Builder(mFragment.getContext())
//                        .setIconAttribute(android.R.attr.alertDialogIcon)
//                        .setTitle(ResourcesUtil.getString(R.string.offline_map_mobile_download))
//                        .setPositiveButton(ResourcesUtil.getString(R.string.offline_map_ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                String name = cityName;
//                                if (name instanceof String) {
//                                    // 判断是按照省还是城市来下载
//                                    for (OfflineMapProvince province : mCityGroupList) {
//                                        if (province.getProvinceName().equals(name)) {
//                                            Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download Province" + name);
//
//                                            mLoader.downloadOfflineProvinceMap(name);
//                                            return;
//                                        }
//                                    }
//                                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download City " + name);
//
//                                    if (OfflineMapStatus.ERROR == status) {
//                                        mLoader.reDownload(name);
//                                    } else {
//                                        mLoader.downloadOfflineCityMap(name);
//                                    }
//                                }
//                            }
//                        })
//                        .setNegativeButton(ResourcesUtil.getString(R.string.offline_map_cancel), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                            }
//                        })
//                        .create()
//                        .show();
//
//            }
//
//            else
//
//            {
//                String name = cityName;
//
//                if (name instanceof String) {
//                    // 判断是按照省还是城市来下载
//                    for (OfflineMapProvince province : mCityGroupList) {
//                        if (province.getProvinceName().equals(name)) {
//                            Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download Province" + name);
//
//                            mLoader.downloadOfflineProvinceMap(name);
//                            return;
//                        }
//                    }
//                    Logs.d("OfflineMapModule", "OfflineMapCityViewModelImp download City " + name);
//                    if (OfflineMapStatus.ERROR == status) {
//                        mLoader.reDownload(name);
//                    } else {
//                        mLoader.downloadOfflineCityMap(name);
//                    }
//                }
//            }


//        }
        };


        return listener;
    }
}
