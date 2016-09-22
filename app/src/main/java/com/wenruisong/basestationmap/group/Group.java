package com.wenruisong.basestationmap.group;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by wen on 2016/5/16.
 */
public class Group extends BmobObject implements Serializable {
    public String groupName;
    public String groupId;
    public String groupBelonging;
    public boolean isSearchVisable;
    public int groupMemberCount;
    public User groupCreator;
    public BmobRelation groupMembers;
    public BmobRelation csvFiles;
    public String groupDiscption= " ";

    public Group(){

    }
}
