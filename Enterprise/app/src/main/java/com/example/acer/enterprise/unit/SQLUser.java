package com.example.acer.enterprise.unit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用户信息的数据库
 */

public class SQLUser extends SQLiteOpenHelper {

    public static String TABLE_NAME = "user";

    private final String CREATE_USER = "Create table " + TABLE_NAME + "(\n" +
            "Uid integer primary key," +
            "Uname char(20) unique," +
            "Upassword char(20) NOT NULL," +
            "Urole char(20) check (Urole in ('normal','administrator')));";

    public SQLUser(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
