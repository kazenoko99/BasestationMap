package com.wenruisong.basestationmap.basestation;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wen on 2016/1/25.
 */
//public  int cellid;
//public String cellName;
//public LatLng latLng;
//public float azimuth;
//public float tatal_downtilt;
//public float downtilt;
public class GSMCell extends Cell implements Parcelable {
    public int lac;
    public int bcch;
    public int bsId;
    public boolean isSelected =false;

    public GSMCell(){

    }

    protected GSMCell(Parcel in) {
        super(in);
        lac = in.readInt();
        bcch = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(lac);
        dest.writeInt(bcch);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GSMCell> CREATOR = new Creator<GSMCell>() {
        @Override
        public GSMCell createFromParcel(Parcel in) {
            return new GSMCell(in);
        }

        @Override
        public GSMCell[] newArray(int size) {
            return new GSMCell[size];
        }
    };
}
