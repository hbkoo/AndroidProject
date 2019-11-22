package com.example.acer.mynettest.Class;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载管理类
 */

public class FileService {

    private DBOpenHelper dbOpenHelper;

    public FileService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }


    public Map<Integer, Integer> getData(String path) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        //获取可读的数据库句柄，一般情况下在该操作的内部实现中其返回的其实是可写的数据库句柄
        Cursor cursor = db.rawQuery("select threadid , downlength from filedownload where " +
                "downpath=?", new String[]{path});
        //根据下载路径查询所有线程下载数据，返回的cursor指向第一条数据之前
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        while (cursor.moveToNext()) {
            data.put(cursor.getInt(0), cursor.getInt(1));
            //把线程id和该线程已下载的长度设置进data哈希表中
            data.put(cursor.getInt(cursor.getColumnIndexOrThrow("threadid")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("downlength")));
        }
        cursor.close();
        db.close();
        return data;
    }

    //保存每条线程已经下载的文件长度
    public void save(String path, Map<Integer, Integer> map) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();//开始事务，因为此处要插入多批数据
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                //插入特定下载路径特定线程id已经下载的数据
                db.execSQL("insert into filedownload(downpath,threadid,downlength)" +
                        "values(?,?,?)", new Object[]{path, entry.getKey(), entry.getValue()});
            }
            db.setTransactionSuccessful();//设置事务执行标志位成功
        } finally {
            db.endTransaction();//结束一个事务；如果事务设置了成功标志，则提交事务，否则回滚事务
        }
        db.close();
    }

    //实时更新每条线程已经下载的文件长度
    public void update(String path, int threadid, int pos) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update filedownload set downlength = ? where downpath = ? " +
                "and threadid = ?", new Object[]{pos, path, threadid});
        db.close();
    }

    //当文件下载完成后，删除对应的下载记录
    public void delete(String path) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from filedownload where downpath = ?", new Object[]{path});
        db.close();
    }

}
