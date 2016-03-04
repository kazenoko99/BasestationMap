package com.wenruisong.basestationmap.basestation;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.wenruisong.basestationmap.BasestationMapApplication;

import java.util.List;

/**
 * Created by wen on 2016/1/16.
 */
public class SignalListener extends PhoneStateListener {
    private TelephonyManager tm;

    SignalListener() {
        tm = (TelephonyManager) BasestationMapApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private String ltestate = "";
    private String gsmstate = "";
    private Cursor cursor1, cursor2;
    private String gsmcid = "  N/A";
    private String gsmlac = "  N/A";
    private String gsmsign = "-   ";
    private String gsmcellname = "  N/A";
    private String ltepci = "N/A";
    private String ltersrp = "N/A";
    private String ltecellname = "  N/A";
    private String ciString;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        int gsmdbm;
        int gsmSignalStrength = signalStrength.getGsmSignalStrength();
        int asu = (gsmSignalStrength == 99 ? -1 : gsmSignalStrength);
        if (asu != -1) {
            gsmdbm = -113 + (2 * asu);
        } else {
            gsmdbm = -1;
        }
        gsmsign = String.valueOf(gsmdbm);
        String[] parts = signalStrength.toString().split(" ");

        int type = tm.getNetworkType();
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        if ((location != null) && (tm.getCellLocation() instanceof GsmCellLocation)) {
            if (location.getCid() < 100000) {
                gsmcid = Integer.toString(location.getCid());
                gsmlac = Integer.toString(location.getLac());
//                cursor1= mydb.rawQuery("select bsname from bs2g where ci like \"%-"+gsmcid+"-%\"",null);
//                if(cursor1.getCount()>0)
//                {
//                    cursor1.moveToFirst();
//                    try {
//                        gsmcellname=new String(cursor1.getBlob(0),"UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    cursor1.close();
//                }
//            }
            }
            if (type == TelephonyManager.NETWORK_TYPE_LTE) {
                ltersrp = parts[9];
                try {
                    if (!(tm.getAllCellInfo() == null)) {
                        List<CellInfo> cellInfoList = tm.getAllCellInfo();
                        for (CellInfo cellInfo : cellInfoList) {
                            if ((cellInfo instanceof CellInfoLte) && cellInfo.isRegistered()) {
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                                if (cellIdentity != null) {
                                    ltepci = Integer.toString(cellIdentity.getPci());
                                    ciString = Integer.toString(cellIdentity.getCi());
//                                cursor2= mydb.rawQuery("select cellname from table4g where ci=\""+ciString+"\"",null);
//                                if(cursor2.getCount()>0)
//                                {
//                                    cursor2.moveToFirst();
//                                    try {
//                                        ltecellname=new String(cursor2.getBlob(0),"UTF-8");} catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();}
//                                }

                                }
                            }
                        }
                    }
                    cursor2.close();
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
            }

//        gsmstate= String.format("GSM小区=%s   Sign=%s   CI=%s",gsmcellname,gsmsign, gsmcid);
//        GSMtextView.setText(gsmstate);
//        ltestate= String.format("LTE小区=%s  RSRP=%s  PCI=%s ",ltecellname,ltersrp, ltepci);
//        LTEtextView.setText(ltestate);
        }
    }
}
