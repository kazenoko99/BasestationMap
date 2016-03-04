package com.wenruisong.basestationmap.common;

import android.content.SharedPreferences;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.utils.Constants;

/**
 * Use tips
 * <p/>
 * --read some key-value
 * SharedPrefer.from(context)
 * .open("com.meizu.flyme.meepo.SOME_SHARED_PREFERENCE")  // optional
 * .read()
 * .getXxx(KYE, DEFAULT_VALUE);
 * <p/>
 * --edit some key-value
 * SharedPrefer.from(context)
 * .open("com.meizu.flyme.meepo.SOME_SHARED_PREFERENCE")  // optional
 * .edit()
 * .putAaa(aKey, aValue)
 * .putBbb(bKey, bValue)
 * .apply(); // or .commit();
 *
 * <p/>
 * Created by liujun on 11/30/14.
 */
public class SharedPrefer {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String fileName = FILE.DEFAULT_FILE_NAME;


    private SharedPrefer() {
    }

    public static SharedPrefer getInstance() {
        return new SharedPrefer();
    }

    public SharedPreferences read() {
        if (sp == null) {
            sp = BasestationMapApplication.getContext().getSharedPreferences(fileName, 0);
        }
        return sp;
    }

    public SharedPrefer open(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public SharedPrefer open() {
        this.fileName = FILE.DEFAULT_FILE_NAME;
        return this;
    }

    public SharedPreferences.Editor edit() {
        if (editor == null) {
            editor = read().edit();
        }
        return editor;
    }


    public static class FILE {
        private static final String DEFAULT_FILE_NAME = Constants.APPLICATION_PACKAGE_NAME + ".default";
        public static final String AR_GROUP = Constants.APPLICATION_PACKAGE_NAME + ".ar_group";
    }


}
