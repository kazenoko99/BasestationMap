package com.wenruisong.basestationmap.common;


public class BackInterruptFlag {
    public  static interface Flag{
        public static final int FLAG_METRO_INTERRUPT = 0x0010;
        public static final int FLAG_KEYWORD_SEARCH_INTERRUPT = 0x0020;
        public static final int FLAG_AR_NAV_INTERRUPT = 0x0030;
        public static final int FLAG_BUS_DETAIL_INTERRUPT = 0x0040;
        public static final int FLAG_AR_NAV_GROUP_INTERRUPT = 0x0050;
    }

    private int mFlag;
    private String mUntilString;

    public BackInterruptFlag(){
        this(0);
    }

    public BackInterruptFlag(int flag){
        mFlag = flag;
    }

    public int getInterruptFlag() {
        return mFlag;
    }

    public void setUntilString(String mUntilString) {
        this.mUntilString = mUntilString;
    }

    public String getUntilString() {
        return mUntilString;
    }
}
