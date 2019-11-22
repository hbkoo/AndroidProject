package com.example.acer.mynettest.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.mynettest.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageActivity extends AppCompatActivity {

    private String path;
    private String[] url;
    private String[] url0;
    String url2 = "http://xiaobaitu.net.k.90qh.com/wp-content/uploads/2016/06/";
    String url3 = "http://xiaobaitu.net.k.90qh.com/wp-content/uploads/2017/03/";
    int num = 0, num_ = 0, NUM2 = 0, NUM3 = 0;
    TextView previous, next, previous_, next_, head_next, head_previous;
    EditText image_text_et;
    ImageView image_img;
    Button getImage_btn, previous_btn, next_btn, set_btn;
    Bitmap bitmap = null;
    ProgressDialog progressDialog;
    int isHeader = -1;

    String PATH = "";          //图片地址
    String prefix_address = "";//地址前缀名
    int NUM = 0;

    int imageSize = 0;

    String SavePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        getPermission();

        image_text_et = (EditText) findViewById(R.id.image_text_et);
        image_img = (ImageView) findViewById(R.id.image_img);
        getImage_btn = (Button) findViewById(R.id.getImage_btn);
        previous_btn = (Button) findViewById(R.id.getImage_previous_btn);
        next_btn = (Button) findViewById(R.id.getImage_next_btn);
        set_btn = (Button) findViewById(R.id.set_btn);
        previous = (TextView) findViewById(R.id.previous);
        next = (TextView) findViewById(R.id.next);
        previous_ = (TextView) findViewById(R.id.previous_);
        next_ = (TextView) findViewById(R.id.next_);
        head_next = (TextView) findViewById(R.id.header_next);
        head_previous = (TextView) findViewById(R.id.header_previous);
        progressDialog = new ProgressDialog(this);

        image_img.setImageBitmap(BitmapFactory.decodeFile(SavePath));

        image_text_et.setText(url[0]);

        getImage_btn.setOnClickListener(new mCLick());
        previous_btn.setOnClickListener(new mCLick());
        next_btn.setOnClickListener(new mCLick());
        set_btn.setOnClickListener(new mCLick());
        image_img.setOnClickListener(new mCLick());

        previous_.setOnLongClickListener(new mLongClick());
        next_.setOnLongClickListener(new mLongClick());
        previous.setOnLongClickListener(new mLongClick());
        next.setOnLongClickListener(new mLongClick());
        head_previous.setOnLongClickListener(new mLongClick());
        head_next.setOnLongClickListener(new mLongClick());
    }

    private class mLongClick implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.previous_:
                    isHeader = 3;
                    if (NUM3 <= 1) {
                        Toast.makeText(getApplicationContext(), "第一张", Toast.LENGTH_SHORT).show();
                    } else {
                        NUM3--;
                        String path3 = url3;
                        path3 += NUM3 + ".jpg";
                        getUrlBitmap(path3);
                    }
                    break;
                case R.id.next_:
                    isHeader = 3;
                    NUM3++;
                    String path3 = url3;
                    path3 += NUM3 + ".jpg";
                    getUrlBitmap(path3);
                    break;
                case R.id.previous:
                    if (num <= 0) {
                        Toast.makeText(getApplicationContext(), "最后一张", Toast.LENGTH_SHORT).show();
                        num = url0.length;
                        getUrlBitmap(url0[--num]);
                    } else {
                        getUrlBitmap(url0[--num]);
                    }
                    break;
                case R.id.next:
                    if (num >= url0.length - 1) {
                        Toast.makeText(getApplicationContext(), "第一张", Toast.LENGTH_SHORT).show();
                        num = 0;
                        getUrlBitmap(url0[num]);
                    } else {
                        getUrlBitmap(url0[++num]);
                    }
                    break;
                case R.id.header_previous:
                    isHeader = 2;
                    if (NUM2 <= 1) {
                        Toast.makeText(getApplicationContext(), "第一张", Toast.LENGTH_SHORT).show();
                    } else {
                        NUM2--;
                        String path2 = url2;
                        path2 += NUM2 + ".jpg";
                        getUrlBitmap(path2);
                    }
                    break;
                case R.id.header_next:
                    isHeader = 2;
                    NUM2++;
                    String path2 = url2;
                    path2 += NUM2 + ".jpg";
                    getUrlBitmap(path2);
                    break;
            }
            return false;
        }
    }

    private class mCLick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isHeader = -1;
            switch (v.getId()) {
                case R.id.set_btn:
                    NUM = 0;
                    prefix_address = image_text_et.getText().toString();
                    if (prefix_address.endsWith(".jpg")) {
                        prefix_address = "";
                    }
                    Toast.makeText(getApplicationContext(), "设置成功" + prefix_address, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.getImage_btn:
                    String str = image_text_et.getText().toString();
                    if (!str.equals("")) {
                        if (str.startsWith("http://")) {
                            path = str;
                        } else {
                            path = "http://" + str;
                        }
                    }
                    getUrlBitmap(path);
                    break;
                case R.id.getImage_previous_btn:
                    PreviousImage();
                    break;
                case R.id.getImage_next_btn:
                    NextImage();
                    break;
                case R.id.image_img:
                    openFile();
                    break;
            }
        }
    }

    //打开图片文件
    private void openFile() {

        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//会新创建一个页面，对于此处会打开图库来显示选择的图片
        intent.setAction(Intent.ACTION_VIEW);

        File f = new File(SavePath);
        long len = f.length();
        Toast.makeText(getApplicationContext(), f.getName() + "大小：" + len, Toast.LENGTH_SHORT).show();
        String type = getMIMEType(f);//获取文件的类型

        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);

    }

    //获取文件类型
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        //获取扩展名
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

        //根据扩展名获取决定MIME类型
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        //如果无法直接打开，就跳出软件列表给用户选择
        if (end.equals("apk")) {

        } else {
            type += "/*";
        }
        return type;
    }

    private void PreviousImage() {

        if (!prefix_address.equals("")) {
            isHeader = 0;
            if (NUM-- > 0) {
                path = prefix_address + NUM;
                getUrlBitmap(path);
                return;
            }
        }

        if (num_ <= 0) {
            Toast.makeText(getApplicationContext(), "最后一张", Toast.LENGTH_SHORT).show();
            num_ = url.length;
            getUrlBitmap(url[--num_]);
        } else {
            getUrlBitmap(url[--num_]);
        }
    }

    private void NextImage() {

        if (!prefix_address.equals("")) {
            isHeader = 0;
            NUM++;
            path = prefix_address + NUM;
            getUrlBitmap(path);
            return;
        }


        if (num_ >= url.length - 1) {
            Toast.makeText(getApplicationContext(), "第一张", Toast.LENGTH_SHORT).show();
            num_ = 0;
            getUrlBitmap(url[num_]);
        } else {
            getUrlBitmap(url[++num_]);
        }
    }


    private void getUrlBitmap(String url) {
        imageSize = 0;
        PATH = url;
        if (!url.endsWith(".jpg")) {
            PATH = url + ".jpg";
        }
        if (!URLUtil.isNetworkUrl(PATH)) {
            Toast.makeText(ImageActivity.this, "输入的网址无效！", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在获取图片...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL image_url = new URL(PATH);
                    connection = (HttpURLConnection) image_url.openConnection();
                    connection.setConnectTimeout(5 * 1000);
                    connection.setReadTimeout(5 * 1000);
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() != 200) {
                        Message message = new Message();
                        message.what = -1;
                        handler.sendMessage(message);
                        return;
                    }
                    imageSize = connection.getContentLength();
                    InputStream is = connection.getInputStream();
                    //bitmap = BitmapFactory.decodeStream(is);
                    //SaveFile(is);
                    SAVE(is);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);


                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                } finally {
                    File file = new File(SavePath);
                    if (file.exists()) {
                        Log.d("123", "文件存在" + file.length());
                        long len = file.length();
                        long a = len;
                    }
//                    if (connection != null) {
//                        connection.disconnect();
//                    }
                }
            }
        }).start();

    }

    private void SAVE(InputStream is) {

        File file;
        FileOutputStream outputStream = null;
        try {
            Log.d("下载路径：", SavePath);
            file = new File(SavePath);
            outputStream = new FileOutputStream(file, false);
            Log.d("准备写入...", "1234");
            byte buffer[] = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                Log.d("正在写入...", "1234");
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            Message message = new Message();
            message.what = 5;
            handler.sendMessage(message);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeFile(SavePath);
        }
    }

    private void SaveFile(InputStream inputStream) {
        Log.d("1234路径：", SavePath);
        File imageFile = new File(SavePath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            int count;
            byte[] buffer = new byte[1024];
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeFile(SavePath);
        }

    }

    private void initData() {
        //SavePath = getFilesDir().getPath() + "/" + "test.jpg";
        SavePath = Environment.getExternalStorageDirectory() + "/" + "image.jpg";
        //SavePath = getCacheDir().getAbsolutePath() + "/" + "test.jpg";
        //SavePath = Environment.getDownloadCacheDirectory().getAbsolutePath() + "/image.jpg";
        //SavePath = Environment.getDataDirectory().getAbsolutePath() + "/image.jpg";
        //SavePath = getExternalCacheDir().getAbsolutePath() + "/image.jpg";
        url = new String[]{"http://p2.so.qhimgs1.com/t0182f48bdcd29f9272.jpg",
                "http://p1.so.qhimgs1.com/t0161a053e9baea405d.jpg",
                "http://p4.so.qhmsg.com/t01880ae4e7fa922479.jpg",
                "http://p2.so.qhimgs1.com/t012f6df78009fa26c2.jpg",
                "http://p2.so.qhimgs1.com/t01c314b4579f50e59e.jpg",
                "http://p2.so.qhmsg.com/t013f69f374159b7047.jpg",
                "http://p2.so.qhimgs1.com/t0199f5eddf397b5732.jpg",
                "http://p0.so.qhmsg.com/t011af17ee6215e6415.jpg",
                "http://p3.so.qhmsg.com/t0140b9befd09e78018.jpg",
                "http://p4.so.qhmsg.com/t010f2c8a04a78c1f09.jpg",
                "http://p0.so.qhmsg.com/t0161a973b9bf3c2cf5.jpg",
                "http://p3.so.qhimgs1.com/t0196916fa2fab8f3c3.jpg",
                "http://p2.so.qhimgs1.com/t011d1d7815c456f39e.jpg",
                "http://p1.so.qhmsg.com/t0185ec9919620efb96.jpg",
                "http://p5.so.qhimgs1.com/t0119a62328db954ccb.jpg",
                "http://p1.so.qhmsg.com/t01016539bcbab6b626.jpg",
                "http://p2.so.qhimgs1.com/t010f94b8e96387675e.jpg",
                "http://p0.so.qhmsg.com/t0151c25f6faeb346c5.jpg",
                "http://p5.so.qhimgs1.com/t01ef0e538b47078f0b.jpg",
                "http://p4.so.qhmsg.com/t0155f8cac3f52c10b9.jpg",
                "http://p0.so.qhimgs1.com/t01fc2ffcc41248644c.jpg",
                "http://p2.so.qhimgs1.com/sdr/1728_1080_/t0167b3e75bb79d5a12.jpg",
                "http://p2.so.qhimgs1.com/sdr/1728_1080_/t01856df75a5410586e.jpg",
                "http://p1.so.qhimgs1.com/sdr/1728_1080_/t014b7ffbf2c6a6d561.jpg",
                "http://p1.so.qhimgs1.com/sdr/1728_1080_/t016ae56189a6cc0c91.jpg",
                "http://p0.so.qhmsg.com/sdr/1728_1080_/t013ea03fe14e66647f.jpg"};
        url0 = new String[]{"http://p0.so.qhimgs1.com/t019153e6fd6c24d29c.jpg",
                "http://p4.so.qhmsg.com/t01ce7f662e508c2319.jpg",
                "http://p0.so.qhimgs1.com/sdr/648_1080_/t0103a0d02b9eaee6bc.jpg",
                "http://p0.so.qhimgs1.com/t015ee2c02e90657070.jpg",
                "http://p1.so.qhimgs1.com/t018180b81fc8dae85d.jpg",
                "http://p1.so.qhmsg.com/t01ae5f3f1a1f72e1a6.jpg",
                "http://p2.so.qhimgs1.com/t0106d068ad46fdc08e.jpg",
                "http://p3.so.qhmsg.com/t01711bfe292d73c6f8.jpg",
                "http://p1.so.qhmsg.com/t017c7b112f46a94496.jpg",
                "http://p4.so.qhmsg.com/t01403e2657612787a9.jpg",
                "http://p1.so.qhimgs1.com/sdr/648_1080_/t01ba12c34172591e61.jpg",
                "http://p0.so.qhimgs1.com/sdr/648_1080_/t01d4e3ffec132fcfc0.jpg",
                "http://p5.so.qhimgs1.com/sdr/648_1080_/t01878a3d00be9ea92b.jpg",
                "http://p0.so.qhmsg.com/sdr/648_1080_/t010d94ba80fcbb82ff.jpg",
                "http://p3.so.qhmsg.com/sdr/648_1080_/t012e593c328b3c7f08.jpg",
                "http://p2.so.qhimgs1.com/t017c2fe275f9ff72ee.jpg",
                "http://p1.so.qhimgs1.com/t01cdcf5c00a52b4901.jpg",
                "http://p0.so.qhmsg.com/t0178d66b38f01ef185.jpg",
                "http://p0.so.qhmsg.com/sdr/720_1080_/t01a57f9b06ae3b8125.jpg",
                "http://www.shaimn.com/uploads/allimg/160203/1-160203123236.jpg",
                "http://www.shaimn.com/uploads/allimg/161125/1-1611251K142.jpg",
                "http://www.shaimn.com/uploads/allimg/160517/1-16051G01H9.jpg",
                "http://p1.so.qhimgs1.com/t01f9b70a9dfa3c13b1.jpg",
                "http://www.shaimn.com/uploads/allimg/160919/1-160919112T4.jpg",
                "http://www.shaimn.com/uploads/allimg/160203/1-1602031A159.jpg",
                "http://g3.hexunimg.cn/2012-06-05/142116512.jpg",
                "http://www.shaimn.com/uploads/allimg/160203/1-160203124447.jpg",
                "http://www.shaimn.com/uploads/allimg/160418/1-16041Q14259.jpg",
                "http://p1.so.qhimgs1.com/t01ae4acb18ec6c9e91.jpg",
                "http://pic.7kk.com/simg/1/800_0/0/bb/e1dddcd90e6a9a64924fe8a73e92f.jpg",
                "http://pic.7kk.com/simg/1/800_0/f/d3/795b5c9f3e96941430b15ac9b9264.jpg",
                "http://pic.7kk.com/simg/1/800_0/1/12/f60e407fd618959740d44587804c1.jpg",
                "http://pic.7kk.com/simg/1/800_0/6/f2/fa794dbebe96a01deda5b833c4bf9.jpg",
                "http://pic.7kk.com/simg/1/800_0/a/0f/3ad5c66f830f1b05bba1545b7d0df.jpg",
                "http://pic.7kk.com/simg/1/800_0/c/2f/51ca5fb967850949a5e4c16b5e6e8.jpg",
                "http://pic.7kk.com/simg/1/800_0/2/77/edffbd4fac2be73a703dbab39139e.jpg",
                "http://pic.7kk.com/simg/1/800_0/e/75/455a0f2f96832f769b908eaafd5d8.jpg",
                "http://pic.7kk.com/simg/1/800_0/2/4d/5717d2172ef88ae4abcc9dd36c6f2.jpg",
                "http://pic.7kk.com/simg/1/800_0/3/be/a08c7cddab14dd1b1b0e1a81a4225.jpg",
                "http://pic.7kk.com/simg/1/800_0/c/5b/f31e579fd1831b06fbec99f14c37a.jpg",
                "http://pic.7kk.com/simg/1/800_0/5/ad/2c33ee362c945fe1f64368f3c3c8a.jpg",
                "http://pic.7kk.com/simg/1/800_0/f/d7/d240b2df8ed7d4b3883805c323bb0.jpg",
                "http://pic.7kk.com/simg/1/800_0/a/97/7f36871bd285829892be47fee77ec.jpg",
                "http://pic.7kk.com/simg/1/800_0/e/29/fc2659d8ea988818fd19476218df3.jpg",
                "http://pic.7kk.com/simg/1/800_0/f/5b/88a5bad6986866b206ca2e50422f0.jpg",
                "http://pic.7kk.com/simg/1/800_0/3/33/08869b75a7aeaa6d92caa11ed5f46.jpg",
                "http://pic.7kk.com/simg/1/800_0/d/1b/122897180ac4d9bc70ccca22194b8.jpg",
                "http://pic.7kk.com/simg/1/800_0/c/c8/794ec425c10efe51aa5e81efc7022.jpg",
                "http://pic.7kk.com/simg/1/800_0/9/9f/ec936f5bb7e6de8f9ef1fb5bab85e.jpg",
                "http://pic.7kk.com/simg/1/800_0/d/fb/753dc56889b9fef4d5182c573e570.jpg",
                "http://pic.7kk.com/simg/1/800_0/3/77/c7ce3577ce0057bb8f21ad0b566a0.jpg",
                "http://pic.7kk.com/simg/1/800_0/8/0f/2f7c91d4a04a0c4716518cad28406.jpg",
                "http://pic.7kk.com/simg/1/800_0/9/9c/6173b3af46da7166c7226b298dc13.jpg",
                "http://pic.7kk.com/simg/1/800_0/0/b9/7c79207ada87b16cf72ea38305ec3.jpg",
                "http://pic.7kk.com/simg/1/800_0/a/26/5fc5a768a1ea3abc8e3ddabcbd6a1.jpg",
                "http://img.7160.com/uploads/allimg/170410/12-1F410153150.jpg"};
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this,new String[] {Manifest.permission
                    .WRITE_EXTERNAL_STORAGE},0);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    image_text_et.setText(PATH);
                    progressDialog.dismiss();

                    image_img.setImageBitmap(bitmap);
                    Toast.makeText(ImageActivity.this, "图片大小" + imageSize, Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    progressDialog.dismiss();
                    Toast.makeText(ImageActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(ImageActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                case -1:
                    progressDialog.dismiss();
                    Toast.makeText(ImageActivity.this, "无效的网址!", Toast.LENGTH_SHORT).show();
                    switch (isHeader) {
                        case 5:
                            if (NUM > 300) {
                                return;
                            }
                            NUM++;
                            String path = prefix_address + NUM + ".jpg";
                            getUrlBitmap(path);
                            break;
                        case 2:
                            if (NUM2 > 300) {
                                return;
                            }
                            NUM2++;
                            String path2 = url2;
                            path2 += NUM2 + ".jpg";
                            getUrlBitmap(path2);
                            break;
                        case 3:
                            if (NUM3 > 300) {
                                return;
                            }
                            NUM3++;
                            String path3 = url3;
                            path3 += NUM3 + ".jpg";
                            getUrlBitmap(path3);
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;//多线程
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                    Toast.makeText(ImageActivity.this,"只有同意了权限才能运行！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
