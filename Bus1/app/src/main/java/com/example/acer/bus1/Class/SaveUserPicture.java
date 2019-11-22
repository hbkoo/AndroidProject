package com.example.acer.bus1.Class;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 *保存图片
 */

public class SaveUserPicture {

        private static final String SHARE_NAME = "information";
        private static final String KEY = "imageUri";


    // 将图片转换成base64编码
    public static String getBitmapBase64(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //压缩的质量为60%
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, out);
        //生成base64字符
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);

    }

    // 设置base64编码进shareP
    public static void putBase64(Context context, String base) {
        // 设置share的name和访问方式（私有）
        SharedPreferences share = context.getSharedPreferences(SHARE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit();
        // 将base存进去
        edit.putString(KEY, base);
        // 提交进文件
        edit.apply();
    }

    // 从shareP取出base64编码
    public static String getBase64(Context context) {
        SharedPreferences share = context.getSharedPreferences(SHARE_NAME,
                Context.MODE_PRIVATE);
        // 从文件中取出key对应的base64编码
        return share.getString(KEY, "");


    }

    // 将base64编码生成图片
    public static Bitmap getBitmap(String base64) {
        byte[] arr = Base64.decode(base64, Base64.DEFAULT);
        ByteArrayInputStream in = new ByteArrayInputStream(arr);
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        return bitmap;
    }
}
