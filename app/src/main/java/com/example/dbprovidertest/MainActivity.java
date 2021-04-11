package com.example.dbprovidertest;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dbprovidertest.Utils.LogUtil;
import com.example.dbprovidertest.adapter.UserInfoAdapter;
import com.example.dbprovidertest.data.DBDataManager;
import com.example.dbprovidertest.data.JobInfo;
import com.example.dbprovidertest.data.UserInfo;
import com.example.dbprovidertest.data.UserInfoTable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<UserInfo> mUserInfos = new ArrayList<>();
    private List<JobInfo> mJobInfos = new ArrayList<>();
    private RecyclerView mDataDisplay;
    private Button mAddBtn, mDeleteBtn, mDeleteAllBtn, mSelectOneBtn, mSelectAllBtn, mUpdateBtn;
    private EditText mUserNameEt, mAgeEt, mJobEt, mJobName, mJobDes;
    private UserInfoObserver mUserInfoObserver;
    private UserInfoAdapter mUserInfoAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mUserInfoObserver = new UserInfoObserver(new Handler());
        this.getContentResolver().registerContentObserver(UserInfoTable.getContentUri(), true, mUserInfoObserver);
        initView();
    }

    public class UserInfoObserver extends ContentObserver {
        public UserInfoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            LogUtil.i(TAG, "onChange");
            super.onChange(selfChange);
            refreshUserDataDisplay();
        }
    }

    private void initView() {
        mAddBtn = findViewById(R.id.add_data_btn);
        mDeleteBtn = findViewById(R.id.delete_data_btn);
        mDeleteAllBtn = findViewById(R.id.delete_all_data_btn);
        mUpdateBtn = findViewById(R.id.update_data_btn);
        mSelectOneBtn = findViewById(R.id.search_data_btn);
        mSelectAllBtn = findViewById(R.id.search_all_btn);


        mUserNameEt = findViewById(R.id.name_et);
        mAgeEt = findViewById(R.id.age_et);
        mJobEt = findViewById(R.id.job_et);
        mJobName = findViewById(R.id.job_name_et);
        mJobDes = findViewById(R.id.job_des_et);

        mDataDisplay = findViewById(R.id.data_display_rv);
        mUserInfoAdapter = new UserInfoAdapter(mUserInfos);
        mDataDisplay.setAdapter(mUserInfoAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataDisplay.setLayoutManager(layoutManager);

        mAddBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mDeleteAllBtn.setOnClickListener(this);
        mUpdateBtn.setOnClickListener(this);
        mSelectOneBtn.setOnClickListener(this);
        mSelectAllBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.add_data_btn:
                insertUserData();
                break;
            case R.id.update_data_btn:
                updateUserData();
                break;
            case R.id.delete_data_btn:
                deleteOneUserData();
                break;
            case R.id.delete_all_data_btn:
                deleteAllUserData();
                break;
            case R.id.search_data_btn:
                getOneUserData();
                break;
            case R.id.search_all_btn:
                getAllUserData();
                break;
        }
    }

    /**
     * 刷新界面数据
     */
    private void refreshUserDataDisplay() {
        LogUtil.i(TAG, "refreshDataDisplay()");
        getAllUserData();
        mUserInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 获取界面输入信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        String name = mUserNameEt.getText().toString();
        if (TextUtils.isEmpty(name)) {
            name = "default";
        }
        String age = mAgeEt.getText().toString();
        if (TextUtils.isEmpty(age)) {
            age = "0";
        }
        String job = mJobEt.getText().toString();
        if (TextUtils.isEmpty(job)) {
            job = "Java";
        }
        UserInfo userInfo = new UserInfo(name, Integer.parseInt(age), job);
        LogUtil.i(TAG,"getUserInfo userInfo: " + userInfo);
        return userInfo;
    }

    /**
     * 获取界面输入信息
     *
     * @return
     */
    public JobInfo getJobInfo() {
        String job_name = mJobName.getText().toString();
        if (TextUtils.isEmpty(job_name)) {
            job_name = "default";
        }
        String job_des = mJobDes.getText().toString();
        if (TextUtils.isEmpty(job_des)) {
            job_des = "default";
        }
        JobInfo jobInfo = new JobInfo(job_name,job_des);
        LogUtil.i(TAG,"getJobInfo jobInfo: " + jobInfo);
        return jobInfo;
    }

    /**
     * 添加数据信息
     */
    public void insertUserData() {
        LogUtil.i(TAG, "insertUserData");
        DBDataManager.getInstance(mContext).addUser(getUserInfo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "插入成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "插入失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 删除某个User信息
     */
    public void deleteOneUserData() {
        String name = mUserNameEt.getText().toString();
        LogUtil.i(TAG, "deleteOneUserData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "名称不能是空", Toast.LENGTH_SHORT).show();
            return;
        }
        DBDataManager.getInstance(mContext).deleteUser(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 删除所有数据
     */
    public void deleteAllUserData() {
        LogUtil.i(TAG, "deleteAllUserData: ");
        DBDataManager.getInstance(mContext).deleteAllUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 根据名字更新某个User的信息
     */
    public void updateUserData() {
        String name = mUserNameEt.getText().toString();
        LogUtil.i(TAG, "updateUserData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Observable<Boolean> isUserExit = DBDataManager.getInstance(mContext).isUserExitInDb(name);
        Observable<Boolean> update = DBDataManager.getInstance(mContext).updateUser(name,getUserInfo());

        Observable.concat(isUserExit,update)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "更新成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 查询所有数据
     */
    public void getAllUserData() {
        LogUtil.i(TAG, "getAllUserData");
        DBDataManager.getInstance(mContext).getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserInfo>>() {
                    @Override
                    public void accept(List<UserInfo> userInfos) throws Exception {
                        if (userInfos != null ) {
                            LogUtil.i(TAG, "userInfos.size(): " + userInfos.size());
                            mUserInfos.clear();
                            mUserInfos.addAll(userInfos);
                            mUserInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });

    }

    /**
     * 查询单个数据
     */
    public void getOneUserData() {
        String name = mUserNameEt.getText().toString();
        LogUtil.i(TAG, "getOneUserData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "名称不能是空", Toast.LENGTH_SHORT).show();
            return;
        }
        DBDataManager.getInstance(mContext).getOneUser(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        LogUtil.i(TAG, "userInfo :" + userInfo);
                        if (userInfo.getName() != null) {
                            mUserInfos.clear();
                            mUserInfos.add(userInfo);
                            mUserInfoAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "没有您要查找的信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(mUserInfoObserver);
    }
}
