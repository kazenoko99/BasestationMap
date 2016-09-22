package com.wenruisong.basestationmap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.group.Group;
import com.wenruisong.basestationmap.group.User;
import com.wenruisong.basestationmap.utils.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class GroupCreateActivity extends AppCompatActivity implements View.OnClickListener {
 private EditText mGroupName;
    private EditText mGroupBelonging;
    private Button mGroupCreateBtn;
    private  User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = BmobUser.getCurrentUser(this, User.class);
        setContentView(R.layout.activity_group_create);
        mGroupName = (EditText)findViewById(R.id.group_name);
        mGroupBelonging  = (EditText)findViewById(R.id.group_belonging);
        mGroupCreateBtn = (Button)findViewById(R.id.create_group);
        mGroupCreateBtn.setOnClickListener(this);
    }

    private void createNewGroup(){
        if(TextUtils.isEmpty(mGroupName.getText().toString())){
            ToastUtil.show(this,"请输入群组名称");
            return;
        }

        if(TextUtils.isEmpty(mGroupBelonging.getText().toString())){
            ToastUtil.show(this,"请输入所属单位");
            return;
        }


        if(user == null){
            ToastUtil.show(this,"请先登录");
            return;
        }

        final Group group= new Group();
        group.groupCreator = user;
        group.groupName = mGroupName.getText().toString();
        group.groupBelonging =mGroupBelonging.getText().toString();
        group.groupMemberCount =1;
        group.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                BmobRelation groupRelation = new BmobRelation();
                groupRelation.add(group);
                User user=BmobUser.getCurrentUser(GroupCreateActivity.this,User.class);
//                user.groupJoined = groupRelation;
//                user.groupCreated= groupRelation;
                user.accountLevel= 3;
                user.update(GroupCreateActivity.this, user.getObjectId(),new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ToastUtil.show(GroupCreateActivity.this, "创建群组" + group.groupName
                                + "成功！");
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastUtil.show(GroupCreateActivity.this, "创建群组" + group.groupName
                                + "失败！"+s);
                    }
                });

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtil.show(GroupCreateActivity.this, "创建群组" + mGroupName.getText().toString()
                        +"失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_group:
                createNewGroup();
                break;
        }
    }
}
