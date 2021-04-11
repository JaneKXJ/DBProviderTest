package com.example.dbprovidertest.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dbprovidertest.Utils.LogUtil;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class DBInfoProvider extends ContentProvider {
    private static final String TAG = DBInfoProvider.class.getSimpleName();

    private DBHelper mDBHelper;
    private Context mContext;
    //ContentProvider的唯一标识
    public static final String AUTHORITY = "com.example.dbprovidertestdata";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final int USER_CODE = 1;
    public static final int JOB_CODE = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 若URI资源路径 = content://com.example.dbprovidertestdata/user ，则返回注册码User_Code
        // 若URI资源路径 = content://com.example.dbprovidertestdata/job ，则返回注册码Job_Code
        uriMatcher.addURI(AUTHORITY, DBHelper.USER_TABLE_NAME, USER_CODE);
        uriMatcher.addURI(AUTHORITY, DBHelper.JOB_TABLE_NAME, JOB_CODE);
    }

    private SQLiteDatabase getWritableDatabase() {
        mDBHelper = DBHelper.getInstance(getContext());
        return mDBHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreate() {
        LogUtil.i(TAG, " ---- onCreate ---- ");
        initDatabase();
        return true;
    }

    private void initDatabase(){
        LogUtil.i(TAG, " ---- initDatabase ---- ");
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                mDBHelper = DBHelper.getInstance(getContext());
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        LogUtil.i(TAG, " ---- query ---- table: " + table);
        SQLiteDatabase db = getWritableDatabase();
        return db.query(table, projection, selection, selectionArgs, null, null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        LogUtil.i(TAG, " ---- insert ---- ");
        String table = getTableName(uri);
        SQLiteDatabase db = getWritableDatabase();
        //返回新添加记录的行号，该行号是一个内部值，与主键id 无关，发生错误返回-1，第一条数据返回1
        long id = db.insert(table, null, contentValues);
        LogUtil.i(TAG, " ---- insert ---- id: " + id);
        if (id == -1) {
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        LogUtil.i(TAG, " ---- delete ---- ");
        String table = getTableName(uri);;
        SQLiteDatabase db = getWritableDatabase();
        //返回的是一个 int 型，表示成功删除几条数据 （一条没更新则返回0，不会报错）
        return db.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        LogUtil.i(TAG, " ---- update ---- ");
        String table = getTableName(uri);;
        SQLiteDatabase db = getWritableDatabase();
        //返回的是一个 int 型，表示成功更新几条数据 （一条没更新则返回0，不会报错）
        return db.update(table, contentValues, selection, selectionArgs);
    }


    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final int numOperations = operations.size();
        final ContentProviderResult[] results = new ContentProviderResult[numOperations];
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
        } catch (Throwable t) {
            LogUtil.e(TAG, "Exception while executing applyBatch()");
            t.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return results;
    }

    /**
     * 根据URI匹配 URI_CODE，从而匹配ContentProvider中相应的表名
     */
    private String getTableName(Uri uri){
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case USER_CODE:
                tableName = DBHelper.USER_TABLE_NAME;
                break;
            case JOB_CODE:
                tableName = DBHelper.JOB_TABLE_NAME;
                break;
        }
        return tableName;
    }

}
