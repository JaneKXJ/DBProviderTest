package com.example.dbprovidertest.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class JobInfoTable {

    //表格的基本信息的字符串
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DES = "des";
    //需要进行操作的uri对象
    private static final Uri CONTENT_URI = Uri.withAppendedPath(DBInfoProvider.AUTHORITY_URI, DBHelper.JOB_TABLE_NAME);

    //返回UserInfo表格操作的uri地址对象
    public static Uri getContentUri() {
        return CONTENT_URI;
    }

    //创建个人信息表格的字符串命令 ，四个属性自增主键id，姓名，年龄，身高，体重，工作
    public static String createJobInfoTable(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(DBHelper.JOB_TABLE_NAME).append(" (");
        sb.append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(NAME).append(" TEXT,");
        sb.append(DES).append(" TEXT)");
        return sb.toString();
    }

    public static ContentValues putValues(JobInfo info) {
        ContentValues values = new ContentValues();
        values.put(NAME, info.getName());
        values.put(DES, info.getDes());
        return values;
    }

    public static JobInfo getValues(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(_ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        String des = cursor.getString(cursor.getColumnIndex(DES));
        JobInfo jobInfo = new JobInfo(id, name, des);
        return jobInfo;
    }


}
