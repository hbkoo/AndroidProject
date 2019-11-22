package com.example.acer.qrcode;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mob.MobSDK;

import java.io.File;
import java.io.FileNotFoundException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //二维码图片地址
    String filePath = null;

    Button open_camera,qrcode_btn,logo_btn;
    EditText text_et;
    ImageView image_iv;
    CreateQRImageTest Test = null;
    public static final int CHOOSE_PHOTO = 1;
    public static final int CIRCLE_PHOTO = 3;


    //logo信息
    private String imagePath;//选择的图片位置
    private File imageFile;//获取裁剪的图片保存地址
    private Bitmap imageLogo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        open_camera = (Button) findViewById(R.id.open_camera_btn);
        qrcode_btn = (Button) findViewById(R.id.qrcode_btn);
        logo_btn = (Button) findViewById(R.id.logo_btn);
        text_et = (EditText) findViewById(R.id.text_et);
        image_iv = (ImageView) findViewById(R.id.image);
        findViewById(R.id.btn_video).setOnClickListener(this);
        open_camera.setOnClickListener(this);
        qrcode_btn.setOnClickListener(this);
        logo_btn.setOnClickListener(this);
        image_iv.setOnClickListener(this);

        filePath = getFileRoot(MainActivity.this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_camera_btn:
                startActivity(new Intent(MainActivity.this,CaptureActivity.class));
                break;
            case R.id.qrcode_btn:
                if (Test == null) {
                    Test = new CreateQRImageTest(MainActivity.this);
                }
                Test.createQRImage(text_et.getText().toString().trim(),filePath,imageLogo);
                image_iv.setImageBitmap(BitmapFactory.decodeFile(filePath));

                break;

            case R.id.logo_btn:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                } else {
                    GetPhotos();
                }
                break;
            case R.id.image:
                openFile();
                break;
            case R.id.btn_video:
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                startActivity(intent);
                break;
        }
    }

    //打开图片文件
    private void openFile() {

        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//会新创建一个页面，对于此处会打开图库来显示选择的图片
        intent.setAction(Intent.ACTION_VIEW);

        File f = new File(filePath);
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



    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }




    //打开相册
    private void GetPhotos() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    //调用相机、相册、设置保存的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    imagePath = handleImageKitKat(data);
                    crop(imagePath);
                }
                break;
            case CIRCLE_PHOTO:
                try {
                    imageLogo = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(Uri.fromFile(imageFile)));

                    Toast.makeText(MainActivity.this,"设置成功",
                            Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    //处理选择的图片
    @TargetApi(19)
    private String handleImageKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (Build.VERSION.SDK_INT < 19) {
            imagePath = getImagePath(uri,null);
            //displayImage(imagePath);
            return imagePath;
        }
        if (DocumentsContract.isDocumentUri(this,uri)) {
            //如果是document类型的uri，则通过document id处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if ("com.android.providers.downloads.documents"
                    .equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads//public_downloads"),Long.valueOf(docID));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通处理类型方法
            imagePath = getImagePath(uri,null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //displayImage(imagePath);
        return imagePath;
    }

    //通过Uri和selection来获取真实的图片路径
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore
                        .Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //获取裁剪的图片保存地址
    private File getmCropImageFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        }
        return null;
    }

    //裁剪原始的图片
    private void crop(String imagePath) {
        //获取裁剪的图片保存地址
        imageFile = getmCropImageFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getImageContentUri(new File(imagePath)), "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CIRCLE_PHOTO);
    }

    //把fileUri转换成ContentUri
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,

                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    GetPhotos();
                } else {
                    Toast.makeText(MainActivity.this,"只有同意了权限才能打开相册",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void InitMob() {
        MobSDK.init(this);

        SMSSDK.setAskPermisionOnReadContact(true);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理成功得到验证码的结果
                                // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达

                                Toast.makeText(MainActivity.this,"验证码已发送...",Toast.LENGTH_SHORT).show();

                            } else {
                                // TODO 处理错误的结果
                                ((Throwable) data).printStackTrace();
                            }
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理验证码验证通过的结果
                                Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_SHORT).show();

                            } else {
                                // TODO 处理错误的结果
                                ((Throwable) data).printStackTrace();
                            }
                        }
                        // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                        return false;
                    }
                }).sendMessage(msg);
            }
        };

        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);

        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode("86","15927037762");
//        SMSSDK.gtVerificationCode("86", phone);

        // 提交验证码，其中的code表示验证码，如“1357”
//        SMSSDK.submitVerificationCode(country, phone, code);

        SMSSDK.submitVerificationCode("86","15927037762","2341");

    }


}
