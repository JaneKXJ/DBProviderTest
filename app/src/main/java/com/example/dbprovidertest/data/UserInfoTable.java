package com.example.dbprovidertest.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class UserInfoTable {

    //表格的基本信息的字符串
    public static final String _ID = "_id";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String JOB = "job";
    //需要进行操作的uri对象
    private static final Uri CONTENT_URI = Uri.withAppendedPath(DBInfoProvider.AUTHORITY_URI, DBHelper.USER_TABLE_NAME);

    //返回UserInfo表格操作的uri地址对象
    public static Uri getContentUri() {
        return CONTENT_URI;
    }

    //创建个人信息表格的字符串命令 ，四个属性自增主键id，姓名，年龄，身高，体重，工作
    public static String createUserInfoTable(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(DBHelper.USER_TABLE_NAME).append(" (");
        sb.append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(NAME).append(" TEXT,");
        sb.append(AGE).append(" INTEGER,  ");
        sb.append(JOB).append(" TEXT)");
        return sb.toString();
    }

    public static ContentValues putValues(UserInfo info) {
        ContentValues values = new ContentValues();
        values.put(NAME, info.getName());
        values.put(AGE, info.getAge());
        values.put(JOB, info.getJob());
        return values;
    }

    public static UserInfo getValues(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(_ID));
        String name = cursor.getString(cursor.getColumnIndex(NAME));
        int age = cursor.getInt(cursor.getColumnIndex(AGE));
        String job = cursor.getString(cursor.getColumnIndex(JOB));
        UserInfo userInfo = new UserInfo(id, name, age, job);
        return userInfo;
    }


}
