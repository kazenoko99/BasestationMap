package com.wenruisong.basestationmap.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wen on 2016/5/12.
 */
public class PhoneState implements Parcelable{
    public String lteCellDistance = "";
    public String gsmCellDistance;
    public String netType="";
    public String gsmCid="  N/A";
    public String gsmLac="  N/A";
    public String gsmCellname="  N/A";
    public String gsmSign="";

    public String lteENB;
    public String lteSINR;
    public String lteTAC;
    public String ltePCI="N/A";
    public String lteCI="N/A";
    public String lteRSRP="N/A";
    public String lteCellname="  N/A";


    public PhoneState(){

    }


    protected PhoneState(Parcel in) {
        netType = in.readString();
        gsmCid = in.readString();
        gsmLac = in.readString();
        gsmCellname = in.readString();
        gsmSign = in.readString();
        lteENB = in.readString();
        lteSINR = in.readString();
        lteTAC = in.readString();
        ltePCI = in.readString();
        lteCI = in.readString();
        lteRSRP = in.readString();
        lteCellname = in.readString();
    }

    public static final Creator<PhoneState> CREATOR = new Creator<PhoneState>() {
        @Override
        public PhoneState createFromParcel(Parcel in) {
            return new PhoneState(in);
        }

        @Override
        public PhoneState[] newArray(int size) {
            return new PhoneState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(netType);
        dest.writeString(gsmCid);
        dest.writeString(gsmLac);
        dest.writeString(gsmCellname);
        dest.writeString(gsmSign);
        dest.writeString(lteENB);
        dest.writeString(lteSINR);
        dest.writeString(lteTAC);
        dest.writeString(ltePCI);
        dest.writeString(lteCI);
        dest.writeString(lteRSRP);
        dest.writeString(lteCellname);
    }
}
