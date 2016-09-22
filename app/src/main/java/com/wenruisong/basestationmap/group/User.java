package com.wenruisong.basestationmap.group;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by wen on 2016/5/16.
 */
public class User extends BmobUser {
    private static final long serialVersionUID = 123456L;
    public int accountLevel;
    public String orgnization;
    public String section;
    public BmobFile avatar;
    public BmobRelation groupJoined;
    public BmobRelation groupCreated;
}
