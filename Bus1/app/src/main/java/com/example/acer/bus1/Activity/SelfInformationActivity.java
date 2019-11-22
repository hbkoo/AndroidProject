package com.example.acer.bus1.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.bus1.Class.SaveUserPicture;
import com.example.acer.bus1.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.acer.bus1.Activity.MainActivity.isTask;

public class SelfInformationActivity extends AppCompatActivity {

    public static boolean isNameChange = false;
    public static boolean isImageChange = false;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CIRCLE_PHOTO = 3;

    private Toolbar toolbar;
    private CircleImageView user_image;
    private ImageView user_bgimage;
    private LinearLayout Name_layout,Sex_layout,IDCard_layout,Introduce_layout;
    private TextView ID,Name,Sex,IDCard,Introduce;
    private Button exit_button;

    private Uri imageUri;
    private String imagePath;
    private File imageFile;
    private Bitmap imageBitmap;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_information);
        toolbar = (Toolbar) findViewById(R.id.toolbar_information);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        InitControls();
        InitPage();
    }

    //初始化控件
    @SuppressLint("WrongConstant")
    private void InitControls() {
        preferences = getSharedPreferences("information",MODE_APPEND);
        user_bgimage = (ImageView) findViewById(R.id.user_bgimage);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        Name_layout = (LinearLayout) findViewById(R.id.Name_layout);
        Sex_layout = (LinearLayout) findViewById(R.id.Sex_layout);
        IDCard_layout = (LinearLayout) findViewById(R.id.IDCard_layout);
        Introduce_layout = (LinearLayout) findViewById(R.id.Introduce_layout);
        ID = (TextView) findViewById(R.id.ID);
        Name = (TextView) findViewById(R.id.Name);
        Sex = (TextView) findViewById(R.id.Sex);
        IDCard = (TextView) findViewById(R.id.IDCard);
        Introduce = (TextView) findViewById(R.id.Introduce);
        exit_button = (Button) findViewById(R.id.exit_btn);
        user_image.setOnClickListener(new mClick());
        Name_layout.setOnClickListener(new mClick());
        Sex_layout.setOnClickListener(new mClick());
        IDCard_layout.setOnClickListener(new mClick());
        Introduce_layout.setOnClickListener(new mClick());
        exit_button.setOnClickListener(new mClick());
    }

    //初始化界面数据
    private void InitPage() {
        Bitmap bitmap;
        isNameChange = false;
        isImageChange = false;
        ID.setText(MainActivity.UserID);
        Name.setText(preferences.getString("name","未设置"));
        Sex.setText(preferences.getString("sex","未设置"));
        IDCard.setText(preferences.getString("IDCard","未设置"));
        Introduce.setText(preferences.getString("introduce","未设置"));

        String ImageUri = preferences.getString("imageUri","");
        if (ImageUri.equals("")) {
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user_image);
        } else {
            bitmap = SaveUserPicture.getBitmap(ImageUri);
        }
        user_bgimage.setImageBitmap(fastblur(bitmap,25));
        user_image.setImageBitmap(bitmap);
    }

    //点击事件的响应
    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_image:
                    ImageChange(user_image);
                    break;
                case R.id.Name_layout:
                    NameChange();
                    break;
                case R.id.Sex_layout:
                    SexChange();
                    break;
                case R.id.IDCard_layout:
                    IDCardChange();
                    break;
                case R.id.Introduce_layout:
                    IntroduceChange();
                    break;
                case R.id.exit_btn:
                    ExitLogin();
                    break;
            }
        }
    }

    //设置昵称
    private void NameChange() {
        LinearLayout dialog_layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_name,null);
        final EditText name_et = (EditText) dialog_layout.findViewById(R.id.name_et);
        TextView OK_dialog = (TextView) dialog_layout.findViewById(R.id.OK_tv);
        TextView cancel_dialog = (TextView) dialog_layout.findViewById(R.id.cancel_tv);
        AlertDialog.Builder dialog = new AlertDialog.Builder(SelfInformationActivity.this);
        dialog.setView(dialog_layout);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.show();
        if (!Name.getText().equals("未设置")) {
            name_et.setText(Name.getText());
            name_et.setSelection(name_et.length());
        }
        OK_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNameChange = true;
                alertDialog.dismiss();
                if (!"".equals(name_et.getText().toString().trim())){
                    Name.setText(name_et.getText());
                } else {
                    Name.setText("未设置");
                }
                editor = preferences.edit();
                editor.putString("name",Name.getText().toString());
                editor.apply();
            }
        });
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //设置性别
    private void SexChange() {
        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_sex,null);
        final RadioButton boy_btn = (RadioButton) layout.findViewById(R.id.radio_boy);
        final RadioButton girl_btn = (RadioButton) layout.findViewById(R.id.radio_gile);
        TextView OK_dialog = (TextView) layout.findViewById(R.id.OK_tv);
        TextView cancel_dialog = (TextView) layout.findViewById(R.id.cancel_tv);
        AlertDialog.Builder dialog = new AlertDialog.Builder(SelfInformationActivity.this);
        dialog.setView(layout);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.show();
        if (!Sex.getText().toString().equals("未设置")) {
            if (Sex.getText().toString().equals(boy_btn.getText())){
                boy_btn.setChecked(true);
            } else {
                girl_btn.setChecked(true);
            }
        }

        OK_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (boy_btn.isChecked()) {
                    Sex.setText(boy_btn.getText());
                } else if (girl_btn.isChecked()) {
                    Sex.setText(girl_btn.getText());
                }
                editor = preferences.edit();
                editor.putString("sex",Sex.getText().toString());
                editor.apply();
            }
        });
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //设置身份证号
    private void IDCardChange() {
        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_idcard,null);
        final EditText idcard_et = (EditText) layout.findViewById(R.id.IDCard_et);
        TextView cancel_tv = (TextView) layout.findViewById(R.id.cancel_tv);
        TextView OK_tv = (TextView) layout.findViewById(R.id.OK_tv);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(layout);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.show();
        if (!IDCard.getText().toString().equals("未设置")) {
            idcard_et.setText(IDCard.getText());
            idcard_et.setSelection(idcard_et.length());
        }
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        OK_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!"".equals(idcard_et.getText().toString().trim())){
                    if (JudgeStr(idcard_et.getText().toString())) {
                        IDCard.setText(idcard_et.getText());
                    } else {
                        LinearLayout layout1 = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.user_content_error,null);
                        TextView Ok_TV = (TextView) layout1.findViewById(R.id.OK_tv);
                        AlertDialog.Builder dialog = new AlertDialog
                                .Builder(SelfInformationActivity.this);
                        dialog.setView(layout1);
                        final AlertDialog dialog1 = dialog.show();
                        Ok_TV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                                return;
                            }
                        });
                    }

                } else {
                    IDCard.setText("未设置");
                }
                editor = preferences.edit();
                editor.putString("IDCard",IDCard.getText().toString());
                editor.apply();
            }
        });

    }

    //判断输入的身份证号格式是否正确
    private boolean JudgeStr(String IDCard) {
        if (IDCard.length() != 18)
            return false;
        String str = IDCard.substring(0,17);
        //判断输入的是否为数字（11.1是数字，但是不符合身份证的形式，所以要判断是否包含“.”）
        if (!str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") || str.contains(".")) {
            return false;
        }
        str = IDCard.substring(17,18);
        //身份证的最后一位可以是X，所以需要再次判断
        if (!str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") && !str.contains("x")
                && !str.contains("X")) {
            return false;
        }
        return true;
    }

    //设置个人签名
    private void IntroduceChange() {
        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_introduce,null);
        final EditText introduce_et = (EditText) layout.findViewById(R.id.introduce_et);
        TextView cancel_tv = (TextView) layout.findViewById(R.id.cancel_tv);
        TextView OK_tv = (TextView) layout.findViewById(R.id.OK_tv);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(layout);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.show();
        if (!Introduce.getText().toString().equals("未设置")) {
            introduce_et.setText(Introduce.getText());
            introduce_et.setSelection(introduce_et.length());
        }
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        OK_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (!"".equals(introduce_et.getText().toString())){
                    Introduce.setText(introduce_et.getText());
                } else {
                    Introduce.setText("未设置");
                }
                editor = preferences.edit();
                editor.putString("introduce",Introduce.getText().toString());
                editor.apply();
            }
        });

    }

    //退出登录
    private void ExitLogin() {
        final Activity activity = (Activity)MainActivity.CONTEXT;

        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_exit,null);
        TextView cancel_tv = (TextView) layout.findViewById(R.id.cancel_tv);
        TextView exit_tv = (TextView) layout.findViewById(R.id.exit_tv);
        TextView exit_information = (TextView) layout.findViewById(R.id.exit_information);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(layout);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.show();
        if (!isTask) {
            exit_information.setText("确认退出账号吗？");
        }

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        exit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SelfInformationActivity.this).edit();
                editor.putBoolean("firstStart",true);
                editor.apply();
                Intent intent = new Intent(SelfInformationActivity.this,LoginActivity.class);
                startActivity(intent);
                activity.finish();
                finish();
            }
        });

    }

    //选择设置头像的方式
    private void ImageChange(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.image_change_way,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        if (ContextCompat.checkSelfPermission(SelfInformationActivity.this,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SelfInformationActivity.this, new
                            String[]{Manifest.permission.CAMERA}, 0);
                        } else {
                            GetCamera();
                        }
                        break;
                    case R.id.photo:
                        if (ContextCompat.checkSelfPermission(SelfInformationActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                                .PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SelfInformationActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        } else {
                            GetPhotos();
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    //调用相机
    private void GetCamera() {
        //创建File对象，用户存储拍照后的图片

        imageFile = new File(getExternalCacheDir(),
                java.util.UUID.randomUUID().toString() + ".jpg");
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SelfInformationActivity.this,
                    "com.example.acer.bus1.Activity.fileprovider",imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
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
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    isImageChange = true;
                    crop(imageFile.getAbsolutePath());
//                    ImageURI = MediaStore.Images.Media
//                            .insertImage(getContentResolver(),bitmap,null,null);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    isImageChange = true;
                    imagePath = handleImageKitKat(data);
                    crop(imagePath);
                }
                break;
            case CIRCLE_PHOTO:
                try {
                    imageBitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(Uri.fromFile(imageFile)));
                    user_bgimage.setImageBitmap(fastblur(imageBitmap,25));
                    user_image.setImageBitmap(imageBitmap);
                    Toast.makeText(SelfInformationActivity.this,"设置成功",
                            Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(SelfInformationActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageBitmap != null) {
                            String base = SaveUserPicture.getBitmapBase64(imageBitmap);
                            SaveUserPicture.putBase64(SelfInformationActivity.this,base);
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    //裁剪原始的图片
    private void crop(String imagePath) {
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

    //获取裁剪的图片保存地址
    private File getmCropImageFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        }
        return null;
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

    //对图片进行毛玻璃模糊效果处理
    private Bitmap fastblur(Bitmap sentBitmap, int radius) {

        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            final RenderScript rs = RenderScript.create(SelfInformationActivity.this);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }


        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    GetCamera();
                } else {
                    Toast.makeText(SelfInformationActivity.this,"只有同意了权限才能打开相机",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    GetPhotos();
                } else {
                    Toast.makeText(SelfInformationActivity.this,"只有同意了权限才能打开相册",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //menu选项点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }



}
