package com.wenruisong.basestationmap.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.CsvFileAdapter;
import com.wenruisong.basestationmap.adapter.GroupFileAdapter;
import com.wenruisong.basestationmap.adapter.GroupMemberAdapter;
import com.wenruisong.basestationmap.common.CsvFile;
import com.wenruisong.basestationmap.common.SearchCsv;
import com.wenruisong.basestationmap.group.Group;
import com.wenruisong.basestationmap.group.User;
import com.wenruisong.basestationmap.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class GroupDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "GroupDetailActivity";
    private CsvFileAdapter mLocalCsvFileAdapter;
    private GroupFileAdapter mGroupFileAdapter;
    private GroupMemberAdapter mGroupMemberAdapter;
    private ListView mListView;
    private LinearLayout mLoadingLayout;
    private TextView mLoadingText;
    private ProgressBar mLoadingProgress;
    private ArrayList<CsvFile> mLocalCsvFiles = new ArrayList<>();
    private ArrayList<CsvFile> mGroupCsvFiles;
    private ArrayList<User> mGroupMember;
    private Group mGroup;

    private final static int LOCALFILE = 0;
    private final static int GROUPFILE = 1;
    private final static int GROUPMEMBER = 2;
    private static int LISTVIEWSTATE = GROUPFILE;


    private boolean isGroupMemberLoaded = false;
    private boolean isGroupFileLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
        Bundle bundle = getIntent().getExtras();
        mGroup = (Group)bundle.getSerializable("GROUP");
        mListView = (ListView)findViewById(R.id.list);
        mLoadingLayout = (LinearLayout)findViewById(R.id.loading_layout);
        mLoadingText = (TextView)findViewById(R.id.loading_text);
        mLoadingProgress = (ProgressBar)findViewById(R.id.progress);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.group_file).setOnClickListener(this);
        findViewById(R.id.group_menber).setOnClickListener(this);
        findViewById(R.id.add_file).setOnClickListener(this);
        mLocalCsvFileAdapter = new CsvFileAdapter(this,mLocalCsvFiles);
        mGroupFileAdapter = new GroupFileAdapter(this,mGroupCsvFiles);
        mGroupMemberAdapter = new GroupMemberAdapter(this,mGroupMember);
        LoadCsvTask loadCsvTask = new LoadCsvTask();
        loadCsvTask.execute();
        mLoadingProgress.animate();
        showPorgress();
        LISTVIEWSTATE = GROUPFILE;
        mListView.setAdapter(mGroupFileAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (LISTVIEWSTATE){
                    case GROUPFILE:
                        break;
                    case GROUPMEMBER:
                        break;
                    case LOCALFILE:
                        publish(mLocalCsvFiles.get(position));
                        break;
                }
            }
        });
        loadGroupFile();
    }

    private void loadGroupFile() {
        if(isGroupFileLoaded)
        {
            return;
        }
        showPorgress();
        BmobQuery<CsvFile> query = new BmobQuery<>();
        query.addWhereRelatedTo("csvFiles", new BmobPointer(mGroup));
        query.include("user");
        query.order("createdAt");
        query.findObjects(this, new FindListener<CsvFile>() {

            @Override
            public void onSuccess(List<CsvFile> data) {
                // TODO Auto-generated method stub
                Log.i(TAG, "get comment success!" + data.size());
                if (data.size() != 0 && data.get(data.size() - 1) != null) {

                  ToastUtil.show(GroupDetailActivity.this, "加载群组基站文件完成");

                    mGroupFileAdapter.setDates(data);
                    Log.i(TAG, "refresh");
                } else {
                    ToastUtil.show(GroupDetailActivity.this, "该群组没有基站文件，快去上传吧");
                }
                isGroupFileLoaded = true;
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtil.show(GroupDetailActivity.this, "啊哦，加载群组文件失败 "+arg1);
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void loadGroupMembers() {
        if(isGroupMemberLoaded)
        {
            return;
        }
        showPorgress();
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("groupMembers", new BmobPointer(mGroup));
       // query.include("user");
        query.order("createdAt");
        query.findObjects(this, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> data) {
                // TODO Auto-generated method stub
                Log.i(TAG, "get User success!" + data.size());
                if (data.size() != 0 && data.get(data.size() - 1) != null) {


                    mGroupMemberAdapter.setDates(data);
                    Log.i(TAG, "refresh");
                }
                isGroupMemberLoaded = true;
                mLoadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtil.show(GroupDetailActivity.this, "啊哦，加载群组文件失败 "+arg1);
                mLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showPorgress(){
        mLoadingLayout.setVisibility(View.VISIBLE);
        switch (LISTVIEWSTATE){
            case GROUPFILE:
                mLoadingText.setText("正在加载群组基站文件");
            case GROUPMEMBER:
                mLoadingText.setText("正在加载群组成员");
            case LOCALFILE:
                mLoadingText.setText("正在上传");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.group_file:
                LISTVIEWSTATE = GROUPFILE;
                mListView.setAdapter(mGroupFileAdapter);
                loadGroupFile();
                break;
            case R.id.group_menber:
                LISTVIEWSTATE = GROUPMEMBER;
                mListView.setAdapter(mGroupMemberAdapter);
                loadGroupMembers();
                break;
            case R.id.add_file:
                LISTVIEWSTATE = LOCALFILE;
                mListView.setAdapter(mLocalCsvFileAdapter);
                break;
        }
    }



    private class LoadCsvTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            SearchCsv.getCsvFile(mLocalCsvFiles, Environment.getExternalStorageDirectory());// 获得视频文件
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLoadingLayout.setVisibility(View.GONE);
            mLocalCsvFileAdapter.notifyDataSetChanged();
        }
    }


    private void publish(final CsvFile csvFile) {
       File mFile = new File(csvFile.csvPath);
        final BmobFile file = new BmobFile(mFile);

        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                publishCsvFile(csvFile, file);
            }
            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.i(TAG, "上传文件失败。" + arg1);
            }
        });

    }

    private void publishCsvFile(final CsvFile csvFile,
                                      final BmobFile file) {
        User user = BmobUser.getCurrentUser(this, User.class);
        csvFile.creator = user;
        if (file != null) {
            csvFile.file = file;
        }

        csvFile.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

                BmobRelation bmobRelation = new BmobRelation();
                bmobRelation.add(csvFile);
                mGroup.csvFiles = bmobRelation;
                mGroup.update(GroupDetailActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        ToastUtil.show(GroupDetailActivity.this, "上传成功！");
                        Log.i(TAG, "创建成功。");
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        ToastUtil.show(GroupDetailActivity.this, "上传失败！yg" + s);
                        Log.i(TAG, "创建失败。" + s);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtil.show(GroupDetailActivity.this, "上传失败！yg" + arg1);
                Log.i(TAG, "创建失败。" + arg1);
            }
        });
    }
}
