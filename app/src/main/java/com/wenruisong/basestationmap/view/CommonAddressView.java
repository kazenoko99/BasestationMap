package com.wenruisong.basestationmap.view;

/**
 * Created by zhaowenjie on 15-5-21.
 * 常用地址小控件，具有完整的UI，ui以及操作相应。
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.database.CommonAddressSqliteHelper;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CommonAddressView extends LinearLayout implements View.OnClickListener {


    private static final String TAG = CommonAddressView.class.getSimpleName();
    private CommonAddressSqliteHelper mCommonAddressSqliteHelper;

    public void setDatebase(CommonAddressSqliteHelper sqliteHelper){
        mCommonAddressSqliteHelper = sqliteHelper;
    }

    public interface CommonAddressClickListener {
        public void onClickListener(CommonAddress address, boolean isEditButton);
    }

    public interface CommonAddressOnClickListener {
        public void onAddressClick(CommonAddress address);
    }

    private String[] mTitles;

    private View root;

    // 在画面中列表项的内容
    protected List<CommonAddress> addresses = new ArrayList<CommonAddress>(Constants.COMMON_ADDRESS_SUM);

    // 记录用于响应点击
    List<TextView> addressTextViews = new ArrayList<TextView>(Constants.COMMON_ADDRESS_SUM);

    protected int clickButtonId;
    protected CommonAddressOnClickListener mCommonAddressOnClickListener = null;

    public CommonAddressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createView(context);
    }

    private void createView(Context context) {
        TextView textView;
        ImageView titlePic;
        CommonAddress address;
        View itemView;
        ViewGroup group;

        //既是中文显示，也是数据库记录的唯一标识
        mTitles = context.getResources().getStringArray(R.array.common_address);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < Constants.COMMON_ADDRESS_SUM; i++) {

            int j;
            j = i & 1;
            if (j == 1) {
                itemView = inflater.inflate(R.layout.common_address_separator, null);
                this.addView(itemView);
            }

            itemView = inflater.inflate(R.layout.common_address_item, null);
            this.addView(itemView);

            //title
            textView = (TextView) itemView.findViewById(R.id.title);
            textView.setText(mTitles[i]);

            if (i == 1) {
                titlePic = (ImageView) itemView.findViewById(R.id.titlePic);
                titlePic.setImageResource(R.drawable.company);
            }

            //address 先设置默认值
            textView = (TextView) itemView.findViewById(R.id.addressName);
            textView.setText(R.string.common_address_adjust);
            address = new CommonAddress();
            address.name = mTitles[i];
            //group
            group = (ViewGroup) itemView.findViewById(R.id.group);
            group.setTag(i);
            group.setOnClickListener(this);


            addressTextViews.add(textView);
            addresses.add(address);
        }
    }

    public List<CommonAddress> getAddressData() {

        CommonAddress mAddress;
        List<CommonAddress> beans = new ArrayList<>(Constants.COMMON_ADDRESS_SUM);
        String searchType;
        for (int i = 0; i < Constants.COMMON_ADDRESS_SUM; i++) {
            searchType = mTitles[i];
            mAddress = mCommonAddressSqliteHelper.queryCommonAddressByName(searchType);
            beans.add(mAddress);
        }
        return beans;
    }

    public void updateAddress(List<CommonAddress> beans) {
        TextView textView;
        CommonAddress address;
        CommonAddress bean;
        int index;
        String searchType;

        for (int i = 0; i < beans.size(); i++) {
            bean = beans.get(i);

//            index = findBeanByIdentification(identification);
//            if (index != -1) {
//
//                textView = addressTextViews.get(index);
//                String text = bean.getName();
//                if (TextUtils.isEmpty(text)) {
//                    text = bean.getAddress();
//                }
//                textView.setText(text);
//
//                //替换旧的
//                addresses.remove(index);
//                address = new CommonAddress(identification, true, bean.getName(), bean.getAddress(), bean.getLatitude(), bean.getLongitude());
//                addresses.add(index, address);
//            }
        }
    }



    @Override
    public void onClick(View v) {
        // 哪一行被点中
        clickButtonId = (Integer) v.getTag();
        CommonAddress address = addresses.get(clickButtonId);

        // 地址栏被点中
        if (v instanceof ViewGroup) {
                if (mCommonAddressOnClickListener != null) {
                    mCommonAddressOnClickListener.onAddressClick(address);
                    return;
                }
        }

        enterAddressEdit();
    }

    protected void enterAddressEdit() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(KeywordSearchFragment.ASK_SEARCH_RESULT, KeywordSearchFragment.ASK_FOR_ADDRESS);
//        if (getContext() instanceof IMapCommon.FragShower) {
//            ((IMapCommon.FragShower) getContext()).showFragments(MapCoreActivity.FRAG_KEYWORD_SEARCH, true, true, bundle);
//        }
    }

    public void updateAddress(PoiItem item) {
//        if (item != null) {
//            Logs.d(TAG, TAG + "updateAddress =" + item.getSnippet());
//
//            double latitude = item.getLatLonPoint().getLatitude();
//            double longitude = item.getLatLonPoint().getLongitude();
//
//            //更新地址显示
//            TextView text = addressTextViews.get(clickButtonId);
//            String name = item.getTitle();
//            String addr = MzMapUtil.getFormatAddress(this.getContext(), item);
//            if (!TextUtils.isEmpty(name)) {
//                text.setText(name);
//            } else {
//                text.setText(addr);
//            }
//
//            CommonAddress address = addresses.get(clickButtonId);
//
//            //替换旧的列表项
//            address = new CommonAddress(address.getIdentification(), true, item.getTitle(), addr, latitude, longitude);
//            addresses.remove(clickButtonId);
//            addresses.add(clickButtonId, address);
//
//            CommonAddress bean = new CommonAddress(address.getIdentification(), item.getTitle(), addr, latitude, longitude);
//            //更新数据库里面的地址
//            mMapDataManager.updateCommonAddress(bean);
//
//        }
    }

    public void setCommonAddressOnClickListener(CommonAddressOnClickListener listener) {
        mCommonAddressOnClickListener = listener;
    }
}





