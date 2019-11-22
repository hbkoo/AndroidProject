package com.example.acer.mynettest.Class;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库管理
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static String DBNAME = "load.db";
    private static int VERSION = 1;
    private String CREATE = "CREATE TABLE IF NOT EXITS filedownload ("
            + "id integer primary key autoincrement, "
            + "downpath varchar(100), "
            + "threadid INTEGER, "
            + "downlength INTEGER)";

    public DBOpenHelper(Context context) {
        super(context,DBNAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //当版本变化时，系统会调用该回调方法
        //删除数据表，在实际业务中一般需要备份数据
        db.execSQL("DROP TABLE IF EXITS FileDownload");
        //调用onCreate方法重新创建数据表
        onCreate(db);

    }
}
