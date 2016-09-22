package com.wenruisong.basestationmap.utils;

import android.os.Environment;
import android.os.StatFs;

import com.amap.api.maps.AMap;

import java.io.File;
import java.util.ArrayList;

public class OfflineMapUtils {

    private static ArrayList<AMap> mAMapList = new ArrayList<>();

    /**
     * 获取map 缓存和读取目录
     */
    public static String getSdCacheDir() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File fExternalStorageDirectory = Environment
                    .getExternalStorageDirectory();
            File autonaviDir = new File(
                    fExternalStorageDirectory, Constants.MAP_MAIN_PATH_NAME);
            boolean result = false;
            if (!autonaviDir.exists()) {
                result = autonaviDir.mkdir();
            }

            if ((null != Constants.MAP_OFFLINE_MAP_PATH_NAME) && (Constants.MAP_OFFLINE_MAP_PATH_NAME.length() > 0)) {
                File minimapDir = new File(autonaviDir,
                        Constants.MAP_OFFLINE_MAP_PATH_NAME);
                if (!minimapDir.exists()) {
                    result = minimapDir.mkdir();
                }
                return minimapDir.toString() + "/";
            } else {
                return autonaviDir.toString() + "/";
            }


        } else {
            return "";
        }
    }

    /**
     * 读取SD卡的剩余空间(单位:B)
     *
     * @return
     */
    public static long getSDCardSurplusSpace() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
//            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
//            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
//            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
            long surplusSpace = availCount * blockSize;
            return surplusSpace;
        }

        return 0;
    }

    public static void addAMap(AMap aMap){
        mAMapList.add(aMap);
    }

    public static void removeAMap(AMap aMap){
        if(mAMapList.contains(aMap)){
            mAMapList.remove(aMap);
        }
    }

    public static ArrayList<AMap> getAMapList(){
        return mAMapList;
    }
}
