package com.wenruisong.basestationmap.common;

import com.wenruisong.basestationmap.group.User;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wen on 2016/5/27.
 */
public class CsvFile extends BmobObject {
    public String csvName;
    public String csvPath;
    public User creator;
    public String discription;
    public BmobFile file;
}
