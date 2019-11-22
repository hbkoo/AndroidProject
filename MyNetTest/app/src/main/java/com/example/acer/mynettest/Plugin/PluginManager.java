package com.example.acer.mynettest.Plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by acer on 2017/7/31.
 */

public class PluginManager {

    public static void loadPlugin(Context context) {

        InputStream inputStream = null;
        FileOutputStream out = null;

        int len;
        byte[] buffer = new byte[1024];

        try {
            File apkFile = new File(Contants.FilePath);
            inputStream = context.getAssets().open(Contants.PLUGIN_NAME);
            out = new FileOutputStream(apkFile);

            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            Toast.makeText(context, "插件下载成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //利用反射的原理加载系统内的方法获取系统内的AssetManager实例
    public static AssetManager getPluginAssetManager(File apkFile) throws Exception {

        Class<?> forName = Class.forName("android.content.res.AssetManager");

        Method[] declareMethods = forName.getMethods();
        for (Method method : declareMethods) {
            if (method.getName().equals("addAssetPath")) {
                /**
                 * 通过反射机制找到 addAssetPath(String path) 方法
                 */
                AssetManager assetManager = AssetManager.class.newInstance();

                /**
                 * 调用addAssetPath(String path) 方法
                 * method.invoke(assetManager,apkFile.getAbsolutePath());中第二个参数即是addAssetPath方法的参数
                 */
                method.invoke(assetManager,apkFile.getAbsolutePath());
                return assetManager;
            }
        }

        return null;
    }

}
