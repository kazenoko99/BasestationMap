package com.wenruisong.basestationmap.group;

/**
 * Created by wen on 2016/5/21.
 */
public class UserManager {
    private static UserManager instance;

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public UserManager() {
    }
}
