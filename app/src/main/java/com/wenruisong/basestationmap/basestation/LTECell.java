package com.wenruisong.basestationmap.basestation;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wen on 2016/1/25.
 */
public class LTECell extends Cell implements Parcelable {
    public int tac;
    public int pci;
    public int enb;
    public int earfcn;

    public LTECell(){

    }

    protected LTECell(Parcel in) {
        super(in);
        tac = in.readInt();
        pci = in.readInt();
        enb = in.readInt();
        earfcn = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(tac);
        dest.writeInt(pci);
        dest.writeInt(enb);
        dest.writeInt(earfcn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LTECell> CREATOR = new Creator<LTECell>() {
        @Override
        public LTECell createFromParcel(Parcel in) {
            return new LTECell(in);
        }

        @Override
        public LTECell[] newArray(int size) {
            return new LTECell[size];
        }
    };
}
