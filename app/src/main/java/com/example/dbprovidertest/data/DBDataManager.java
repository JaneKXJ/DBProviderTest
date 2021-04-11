package com.example.dbprovidertest.data;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.DropBoxManager;

import com.example.dbprovidertest.Utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DBDataManager {
    public static final String TAG = DBDataManager.class.getSimpleName();
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.hisense.fridge.foodmanagedata");
    public static final Uri FOOD_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"food");
    public static DBDataManager instance;
    private Context mContext;

    public DBDataManager(Context context) {
        this.mContext = context;
    }

    public static DBDataManager getInstance(Context context) {
        if (null == instance) {
            synchronized (DBHelper.class) {
                if (null == instance) {
                    instance = new DBDataManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }


    /**
     * 添加成员信息
     * @param info
     */
    public Observable<Boolean> addUser(final UserInfo info) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- addUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = UserInfoTable.getContentUri();
                ContentValues values = UserInfoTable.putValues(info);
                Uri res = contentResolver.insert(uri, values);
                LogUtil.i(TAG," subscribe --- addUser --- res " + res);
                if (res == null){
                    e.onNext(false);
                } else {
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名字更新用户信息
     * @param name
     * @param info
     */
    public Observable<Boolean> updateUser(final String name, final UserInfo info) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- updateUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = UserInfoTable.getContentUri();
                ContentValues values = new ContentValues();
                values.put(UserInfoTable.AGE, info.getAge());
                values.put(UserInfoTable.JOB, info.getJob());
                values.put(UserInfoTable.NAME, info.getName());
                int res = contentResolver.update(uri, values, UserInfoTable.NAME + "=?", new String[]{name});
                LogUtil.i(TAG," subscribe --- updateUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名称删除某个成员信息
     * @param name
     */
    public Observable<Boolean> deleteUser(final String name) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- deleteUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = UserInfoTable.getContentUri();
                int res = contentResolver.delete(uri, UserInfoTable.NAME + "=?", new String[]{name});
                LogUtil.i(TAG," subscribe --- deleteUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名称删除某个成员信息
     */
    public Observable<Boolean> deleteAllUser() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- deleteAllUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = UserInfoTable.getContentUri();
                int res = contentResolver.delete(uri, null, null);
                LogUtil.i(TAG," subscribe --- deleteAllUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名称获取成员信息
     *
     * @param userName
     * @return
     */
    public Observable<UserInfo> getOneUser(final String userName) {
        return Observable.create(new ObservableOnSubscribe<UserInfo>() {
            @Override
            public void subscribe(ObservableEmitter<UserInfo> emitter) throws Exception {
                LogUtil.i(TAG," subscribe --- getOneUser --- thread: " + Thread.currentThread().getName());
                UserInfo userInfo = new UserInfo(null,0,null);
                Uri uri = UserInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, UserInfoTable.NAME + "=?", new String[]{userName}, null);

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        userInfo = UserInfoTable.getValues(cursor);
                    }
                    cursor.close();
                }
                emitter.onNext(userInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 某个用户是否存在
     *
     * @param userName
     * @return
     */
    public Observable<Boolean> isUserExitInDb(final String userName) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                LogUtil.i(TAG," subscribe --- isUserExitInDb --- thread: " + Thread.currentThread().getName());
                Uri uri = UserInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, UserInfoTable.NAME + "=?", new String[]{userName}, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.close();
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    cursor.close();
                    emitter.onNext(false);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 获取全部user
     * @return
     */
    public Observable<List<UserInfo>> getAllUsers() {
        return Observable.create(new ObservableOnSubscribe<List<UserInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserInfo>> e) throws Exception {
                LogUtil.i(TAG," subscribe --- getAllUsers --- thread: " + Thread.currentThread().getName());
                List<UserInfo> list = new ArrayList<>();
                Uri uri = UserInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        UserInfo info = UserInfoTable.getValues(cursor);
                        list.add(info);
                    }
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

}
