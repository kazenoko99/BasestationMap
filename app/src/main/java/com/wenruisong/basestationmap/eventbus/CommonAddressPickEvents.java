package com.wenruisong.basestationmap.eventbus;

import com.squareup.otto.Bus;
import com.wenruisong.basestationmap.model.CommonAddress;

/**
 * Created by wen on 2016/2/18.
 */
public class CommonAddressPickEvents {
    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

    private CommonAddressPickEvents() {
        // No instances.
    }

    public static class OnAddressPick{
        public CommonAddress mCommonAddress;
        public OnAddressPick(CommonAddress commonAddress) {
            this.mCommonAddress = commonAddress;
        }
    }
}