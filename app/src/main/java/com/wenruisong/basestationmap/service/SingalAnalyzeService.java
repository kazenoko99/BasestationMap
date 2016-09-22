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
import android.os.IBinder;
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

import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.basestation.LTECell;
import com.wenruisong.basestationmap.basestation.Marker.ServiceCellMarker;
import com.wenruisong.basestationmap.common.Settings;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.model.PhoneState;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SingalAnalyzeService extends Service implements Settings.SignalNotifcationOnListener{
    private static final int NOTIFICATION_FLAG = 1;
    private final String TAG = SingalAnalyzeService.this.getClass().getSimpleName();
    private NetworkStateListener mNetworkStateListener;
    private TelephonyManager mTelephonyManager;
    private SQLiteDatabase mDatebase;
    private PhoneState mPhoneState = new PhoneState();
    private NotificationManager mNotificationManager;
    private Notification notification;
    private GsmCellLocation mGsmCellLocation;
    private Timer timer;
    private static boolean isNeedShowNotification = true;
    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    public static void setOnCellInfoChange(OnCellInfoChange onCellInfoChange) {
        mOnCellInfoChange = onCellInfoChange;
    }

    private static OnCellInfoChange mOnCellInfoChange;


    public SingalAnalyzeService() {
    }

    public PhoneState getPhoneState() {
        return mPhoneState;
    }



    public class PhoneStateBinder extends Binder {
        public SingalAnalyzeService getService() {
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
        mTelephonyManager.listen(mNetworkStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        mDatebase = BasestationManager.getInstance().basestationDB;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initNotification();
        listenProgress();
            Settings.getInstance().setSignalNotifcationOnListener(this);
    }

    private class NetworkStateListener extends PhoneStateListener {


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
            mPhoneState.gsmSign = String.valueOf(gsmdbm);
            String[] parts = signalStrength.toString().split(" ");

            int type = mTelephonyManager.getNetworkType();
            mGsmCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
            if ((mGsmCellLocation != null) && (mTelephonyManager.getCellLocation() instanceof GsmCellLocation)) {
                mPhoneState.netType = "GSM";
                if (mGsmCellLocation.getCid() < 100000) {
                    mPhoneState.gsmCid = Integer.toString(mGsmCellLocation.getCid());
                    mPhoneState.gsmLac = Integer.toString(mGsmCellLocation.getLac());
                }
            }
            if (type == TelephonyManager.NETWORK_TYPE_LTE) {
                mPhoneState.netType = "LTE";
                mPhoneState.lteRSRP = parts[9];
                try {
                    if (!(mTelephonyManager.getAllCellInfo() == null)) {
                        List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                        for (CellInfo cellInfo : cellInfoList) {
                            if ((cellInfo instanceof CellInfoLte) && cellInfo.isRegistered()) {
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                                if (cellIdentity != null) {
                                    mPhoneState.ltePCI = Integer.toString(cellIdentity.getPci());
                                    mPhoneState.lteCI = Integer.toString(cellIdentity.getCi());
                                }
                            }
                        }
                    }

                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void initNotification() {
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
                .setTicker("基站信号详情显示在下拉菜单");
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
            mNotificationManager.notify(7, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    TimerTask task = new TimerTask() {
        private Cursor cursor1, cursor2;
        private String sqlGsm = "select * from gsm_cells_" + BasestationManager.getCurrentShowCity() + " where CID = \"" + mPhoneState.gsmCid + "\"";
       GSMCell mGSMCell;
        LTECell mLTECell;
        LatLng currentLatLng;
        public void run() {
            if (Settings.isTableExsit(BasestationManager.getCurrentShowCity(),"GSM")){
                cursor1 = mDatebase.rawQuery(sqlGsm, null);
                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    mGSMCell = BasestationManager.db2GsmCell(cursor1);

                        mPhoneState.gsmCellname = mGSMCell.cellName;
                    if(LocationHelper.location!=null) {
                        currentLatLng = new LatLng(LocationHelper.location.getLatitude(),LocationHelper.location.getLongitude());
                        mPhoneState.gsmCellDistance = DistanceUtils.getDistance(mGSMCell.aMapLatLng,currentLatLng );
                    }
                        cursor1.close();
                }
            }

                if (Settings.isTableExsit(BasestationManager.getCurrentShowCity(),"LTE")){
                cursor2 = mDatebase.rawQuery("select * from lte_cells_" + BasestationManager.getCurrentShowCity() + " where CI = \"" + mPhoneState.lteCI + "\"", null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToFirst();
                    mLTECell = BasestationManager.db2LteCell(cursor2);
                    ServiceCellMarker.getInstance().addInMap(mLTECell.aMapLatLng);
                    mPhoneState.lteCellname = mLTECell.cellName;
                    if(LocationHelper.location!=null) {
                        currentLatLng = new LatLng(LocationHelper.location.getLatitude(),LocationHelper.location.getLongitude());
                        mPhoneState.lteCellDistance = DistanceUtils.getDistance(mLTECell.aMapLatLng,currentLatLng );
                    }
                }
                cursor2.close();
                    mOnCellInfoChange.onChanged(mPhoneState);
                 if(isNeedShowNotification)
                    updateNotification();
            }
        }
    };

    public void listenProgress() {
        timer = new Timer(true);
        timer.schedule(task, 1000, 1000); //延时1000ms后执行，1000ms执行一次

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel(); //退出计时器
    }

    public  interface OnCellInfoChange{
        void onChanged(PhoneState phoneState);
    }

    @Override
    public void onSignalNotificationChange(boolean show) {
        isNeedShowNotification = show;
       if(show){
           mNotificationManager.notify(7, notification);
       } else {
           mNotificationManager.cancel(7);

       }

    }
}
