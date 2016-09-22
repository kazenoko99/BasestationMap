package com.wenruisong.basestationmap.basestation;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.utils.Constants;

/**
 * Created by wen on 2016/1/16.
 */
public class Cell implements Parcelable,Comparable {
    public int index;
    public String bsName;
    public  int cellid;
    public String cellName;
    public LatLng latLng;
    public LatLng aMapLatLng;
    public float azimuth;
    public float tatal_downtilt;
    public int highth;
    public float downtilt;
    public String address;
    public boolean isShow = false;
    public int type;//是否是室分 1/h为室分，0为宏站
    public double distance;
  public Cell(){

  }

    @Override
    public boolean equals(Object o) {
       // return super.equals(o);
        if (o instanceof Cell){
            if(((Cell) o).cellid==this.cellid && ((Cell) o).cellid == (this.cellid)){
                return true;
            } else {
                return false;
            }
        } else
            return false;

    }

    @Override public int hashCode() {
        return cellid;
    }

    protected Cell(Parcel in) {
        index = in.readInt();
        bsName = in.readString();
        cellid = in.readInt();
        cellName = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        aMapLatLng = in.readParcelable(LatLng.class.getClassLoader());
        azimuth = in.readFloat();
        tatal_downtilt = in.readFloat();
        highth = in.readInt();
        downtilt = in.readFloat();
        address = in.readString();
        isShow = in.readByte() != 0;
        type = in.readInt();
        distance = in.readInt();
    }

    public static final Creator<Cell> CREATOR = new Creator<Cell>() {
        @Override
        public Cell createFromParcel(Parcel in) {
            return new Cell(in);
        }

        @Override
        public Cell[] newArray(int size) {
            return new Cell[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(bsName);
        dest.writeInt(cellid);
        dest.writeString(cellName);
        dest.writeParcelable(latLng, flags);
        dest.writeParcelable(aMapLatLng, flags);
        dest.writeFloat(azimuth);
        dest.writeFloat(tatal_downtilt);
        dest.writeInt(highth);
        dest.writeFloat(downtilt);
        dest.writeString(address);
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeInt(type);
        dest.writeDouble(distance);
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }

    public enum CellType
    { GSM,TDS,LTE,CDMA,CDMA2000,WCDMA }

    public int getInstanceType(){
        if(this instanceof GSMCell)
            return Constants.GSM;
        else if (this instanceof LTECell)
            return Constants.LTE;
        else return Constants.TYPE_UNKNOWN;
    }

}
