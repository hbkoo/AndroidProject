package com.example.acer.mynettest.Activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.acer.mynettest.Plugin.Contants;
import com.example.acer.mynettest.Plugin.PluginManager;
import com.example.acer.mynettest.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageSource,imageCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageSource = (ImageView) findViewById(R.id.imageSource);
        imageCloud = (ImageView) findViewById(R.id.imageCloud);

        imageCloud.setOnClickListener(this);
        imageSource.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageSource:
                handleAnim(v);
                break;
            case R.id.imageCloud:
                File apkFile = new File(Contants.FilePath);
                if (apkFile.exists()) {
                    //执行帧动画
                    Drawable background = v.getBackground();
                    if (background instanceof AnimationDrawable) {
                        handleAnim(v);
                    } else {

                        //反射机制获取插件apk内的资源
//                        AssetManager.copyAllAssetsApk(context);
//                        File dir = context.getDir(AssetsManager.APK_DIR, Context.MODE_PRIVATE);
//                        String apkPath = dir.getAbsolutePath()+"/BundleApk.apk";

                        DexClassLoader classLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                                this.getDir(Contants.PLUGIN_NAME, Context.MODE_PRIVATE).getAbsolutePath(),
                                null,getClassLoader());
                        try {
                            Class<?> addClass = classLoader.loadClass(Contants.PackageName + ".MainActivity");
                            Method[] methods = addClass.getMethods();
                            for (Method method:methods) {
                                if (method.getName().equals("add")) {
                                    int a = (int) method.invoke(AssetManager.class.newInstance(),3,4);

                                    Toast.makeText(this,"结果："+a,Toast.LENGTH_SHORT).show();
                                }
                            }

                            Class<?> loadClass = classLoader.loadClass(Contants.PackageName + ".R$drawable");
                            Field[] declareFileId = loadClass.getDeclaredFields();
                            for (Field field : declareFileId) {
                                if (field.getName().equals("cloud")) {
                                    int animId = field.getInt(R.drawable.class);

                                    AssetManager assetManager = PluginManager.getPluginAssetManager(apkFile);
                                    Resources resources = new Resources(assetManager,
                                            this.getResources().getDisplayMetrics(),this.getResources().getConfiguration());

                                    Drawable drawable = resources.getDrawable(animId);
                                    //imageCloud.setImageDrawable(drawable);
                                    imageCloud.setBackgroundDrawable(drawable);
                                    handleAnim(imageCloud);
                                    imageCloud.setImageBitmap(null);
                                    //imageCloud.setBackground(drawable);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //加载插件  黑白名单等等
                    PluginManager.loadPlugin(this);
                }
                break;
        }
    }

    private void handleAnim(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        if (animationDrawable != null) {
            if (animationDrawable.isRunning()) {
                animationDrawable.stop();
            } else {
                animationDrawable.start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
