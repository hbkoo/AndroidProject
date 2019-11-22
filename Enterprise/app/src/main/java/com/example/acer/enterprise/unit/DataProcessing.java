package com.example.acer.enterprise.unit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问数据库操作类
 */

public class DataProcessing {

    /**
     * 登录判断
     *
     * @param database 数据库信息
     * @param username 用户名
     * @param password 密码
     * @return 用户的角色
     */
    public static String Login(SQLiteDatabase database, String username, String password) {
        String role = null;

        if (database == null) {
            return null;
        }

        Cursor cursor = database.query(SQLUser.TABLE_NAME, null, "Uname=? and Upassword=?",
                new String[]{username, password}, null, null, null);
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex("Urole")).toLowerCase();
        }
        cursor.close();

        return role;
    }

    /**
     * 注册新用户
     *
     * @param database 数据库信息
     * @param username 用户名
     * @param password 密码
     * @param role     角色
     * @return 注册是否成功
     */
    public static Boolean Register(SQLiteDatabase database, String username,
                                   String password, String role) {
        Boolean isSuccess;

        if (database == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Uname", username);
        values.put("Upassword", password);
        values.put("Urole", role);
        long result = database.insert(SQLUser.TABLE_NAME, null, values);
        isSuccess = result != -1;
        return isSuccess;
    }

    /**
     * 获取对信息管理的数据库对象
     *
     * @param context 上下文
     * @param tag     获取标志，即获取读操作还是写操作的数据库对象
     *                0---读操作对象
     *                1---写操作对象
     * @return 返回获取的数据库对象
     */
    public static SQLiteDatabase getINFDatabase(Context context, int tag) {

        SQLiteDatabase database = null;
        String INFORMATION_DB_NAME = "Information";
        SQLEnterprise enterprise = new SQLEnterprise(context, INFORMATION_DB_NAME, null, 1);

        if (tag == 0) {
            database = enterprise.getReadableDatabase();
        } else if (tag == 1) {
            database = enterprise.getWritableDatabase();
        }

        return database;
    }

    /**
     * 获取数据库中的全部企业发布的信息
     *
     * @param database 数据库读操作对象
     * @return 数据库中的信息
     */
    public static List<Information> LoadAllInformation(SQLiteDatabase database) {
        List<Information> informationList = new ArrayList<>();
        Information information;

        Cursor cursor = database.query(SQLEnterprise.TABLE_NAME, null, null,
                null, null, null, "Edate DESC");
        if (cursor.moveToFirst()) {
            do {
                information = new Information();
                information.setId(cursor.getInt(cursor.getColumnIndex("Eid")));
                information.setDate(cursor.getString(cursor.getColumnIndex("Edate")));
                information.setContent(cursor.getString(cursor.getColumnIndex("Econtent")));
                information.setTitle(cursor.getString(cursor.getColumnIndex("Etitle")));
                informationList.add(information);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return informationList;
    }

    /**
     * 新发布企业的信息插入到数据库
     *
     * @param database    信息数据库对象
     * @param information 要发布的信息对象
     * @return 返回是否插入成功标记
     */
    public static boolean InsertInformation(SQLiteDatabase database, Information information) {
        boolean isSuccess;

        if (database == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Etitle", information.getTitle());
        values.put("Econtent", information.getContent());
        values.put("Edate", information.getDate());

        long tag = database.insert(SQLEnterprise.TABLE_NAME, null, values);
        isSuccess = tag != -1;
        return isSuccess;
    }

    /**
     * 修改某条信息
     *
     * @param database    可执行写操作的数据库对象
     * @param information 要写入到数据库中的信息
     * @return 是否修改成功
     */
    public static boolean UpdateInformation(SQLiteDatabase database, Information information) {
        boolean isSuccess;

        if (database == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Etitle", information.getTitle());
        values.put("Econtent", information.getContent());
        values.put("Edate", information.getDate());

        int result = database.update(SQLEnterprise.TABLE_NAME, values, "Eid = ?",
                new String[]{String.valueOf(information.getId())});
        isSuccess = result != 0;
        return isSuccess;
    }

    /**
     * 删除用户选中的多条信息记录
     *
     * @param database        可执行写操作的数据库对象
     * @param informationList 要删除的多条消息就
     * @return 是否删除成功
     */
    public static boolean DeleteInformation(SQLiteDatabase database, List<Information> informationList) {
        boolean isSuccess;
        if (database == null) {
            return false;
        }

        int result = 0;
        for (Information information : informationList) {
            result = database.delete(SQLEnterprise.TABLE_NAME, "Eid = ?",
                    new String[]{String.valueOf(information.getId())});
        }

        isSuccess = result != 0;

        return isSuccess;
    }

}
