package com.wenruisong.basestationmap.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.RemoteViews;

import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.model.PhoneState;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SingalAnalyzeService extends Service {
    private static final int NOTIFICATION_FLAG = 1;
    private final  String TAG = SingalAnalyzeService.this.getClass().getSimpleName();
    private NetworkStateListener mNetworkStateListener;
    private TelephonyManager mTelephonyManager;
    private SQLiteDatabase mDatebase;
    private PhoneState mPhoneState = new PhoneState();
    private  NotificationManager mNotificationManager;
    private Notification notification;
    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public SingalAnalyzeService() {
    }

    public PhoneState getPhoneState() {
        return mPhoneState;
    }

    public class PhoneStateBinder extends Binder {
        public SingalAnalyzeService getService(){
            return SingalAnalyzeService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new PhoneStateBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mNetworkStateListener = new NetworkStateListener();
        mTelephonyManager.listen(mNetworkStateListener , PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mDatebase =  BasestationManager.getInstance().basestationDB;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        listenProgress();
        initNotification();
    }

    private class NetworkStateListener extends PhoneStateListener{
        private Cursor cursor1,cursor2;
        private String gsmcid="  N/A";
        private String lteci ="  N/A";;
        private String sqlGsm = "select * from gsm_cells where  CID is "+gsmcid+";";
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);


            int gsmdbm;
            int gsmSignalStrength=signalStrength.getGsmSignalStrength();
            int asu = (gsmSignalStrength == 99 ? -1 : gsmSignalStrength);
            if (asu != -1) {
                gsmdbm = -113 + (2 * asu);
            } else {
                gsmdbm = -1;
            }
            mPhoneState.gsmSign=String.valueOf(gsmdbm);
            String[] parts = signalStrength.toString().split(" ");

            int type=mTelephonyManager.getNetworkType();
            GsmCellLocation location = (GsmCellLocation)mTelephonyManager.getCellLocation();
            if((location != null)&&(mTelephonyManager.getCellLocation() instanceof GsmCellLocation))
            {
                mPhoneState.netType="GSM";
                if(location.getCid()<100000)
                {
                    gsmcid = Integer.toString(location.getCid());
                    mPhoneState.gsmCid = gsmcid;
                    mPhoneState.gsmLac = Integer.toString(location.getLac());
                    cursor1= mDatebase.rawQuery(sqlGsm,null);
                    if(cursor1.getCount()>0)
                    {
                        cursor1.moveToFirst();
                        try {
                            mPhoneState.gsmCellname = new String(cursor1.getBlob(0),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        cursor1.close();
                    }
                }
            }
            if ( type == TelephonyManager.NETWORK_TYPE_LTE){
                mPhoneState.netType="LTE";
                mPhoneState.lteRSRP = parts[9];
                try{
                    if(!(mTelephonyManager.getAllCellInfo()==null))
                    {
                        List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                        for (CellInfo cellInfo : cellInfoList) {
                            if(( cellInfo instanceof CellInfoLte)&&cellInfo.isRegistered())
                            {
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                                if(cellIdentity!=null)
                                {
                                    mPhoneState.ltePCI = Integer.toString(cellIdentity.getPci());

                                    lteci=Integer.toString(cellIdentity.getCi());
                                    mPhoneState.lteCI = lteci;
                                    //"select * from lte_cells where BS = '" + cell.bsName + "';"
                                    cursor2= mDatebase.rawQuery("select NAME from lte_cells where CI = \""+lteci+"\"",null);
                                    if(cursor2.getCount()>0)
                                    {
                                        cursor2.moveToFirst();
                                        try {
                                            mPhoneState.lteCellname = new String(cursor2.getBlob(0),"UTF-8");} catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();}
                                    }

                                }
                            }
                        }
                    }
                    cursor2.close();
                }catch(NullPointerException e1){e1.printStackTrace();}
            }
        }
    }

   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   void initNotification(){
       RemoteViews remoteViews = new RemoteViews(getPackageName(),
               R.layout.notification_phone_state);
       TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
       // Adds the back stack
       stackBuilder.addParentStack(MainActivity.class);
       // Adds the Intent to the top of the stack
       Intent resultIntent = new Intent(this, MainActivity.class);
       stackBuilder.addNextIntent(resultIntent);
       // Gets a PendingIntent containing the entire back stack
       PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
               PendingIntent.FLAG_UPDATE_CURRENT);
       remoteViews.setOnClickPendingIntent(R.id.go_map, resultPendingIntent);
       NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
       builder.setContent(remoteViews).setSmallIcon(R.drawable.ic_launcher)
               .setOngoing(true)
               .setTicker("music is playing");
       notification = builder.build();
       notification.bigContentView = remoteViews;
   }

    @SuppressLint("NewApi")
    private void updateNotification() {
        try {
            notification.bigContentView.setTextViewText(R.id.lteCellName, mPhoneState.lteCellname);
            notification.bigContentView.setTextViewText(R.id.lteRSRP, mPhoneState.lteRSRP);
            notification.bigContentView.setTextViewText(R.id.ltePCI, mPhoneState.ltePCI);
            notification.bigContentView.setTextViewText(R.id.lteENB, mPhoneState.lteENB);
            notification.bigContentView.setTextViewText(R.id.lteSINR, mPhoneState.lteSINR);

            notification.bigContentView.setTextViewText(R.id.gsmCellName, mPhoneState.gsmCellname);
            notification.bigContentView.setTextViewText(R.id.gsmCid, mPhoneState.gsmCid);
            notification.bigContentView.setTextViewText(R.id.gsmLac, mPhoneState.gsmLac);
            notification.bigContentView.setTextViewText(R.id.gsmSign, mPhoneState.gsmSign);
            mNotificationManager.notify(7,notification );



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(BasestationMapApplication.isPhonteStateNotificationShow){
                    try {
                        Thread.sleep(1000);
                        mHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateNotification();
        }
    };
}
