package com.example.acer.enterprise.unit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 企业信息数据库
 */

public class SQLEnterprise extends SQLiteOpenHelper {

    public static String TABLE_NAME = "enterprise";

    private final String CREATE_ENTERPRISE = "Create table " + TABLE_NAME + "(" +
            "Eid integer  primary key autoincrement," +
            "Etitle text NOT NULL," +
            "Econtent text NOT NULL," +
            "Edate date NOT NULL);";

    public SQLEnterprise(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ENTERPRISE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
