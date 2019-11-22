package com.example.acer.bus1.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.acer.bus1.Activity.MainActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyService extends Service {

    private GetTaskBinder mBinder = new GetTaskBinder();

    class GetTaskBinder extends Binder {

        String result;

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        MainActivity.isTask = true;
                        break;
                }
            }
        };

        public void StartSearch() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!MainActivity.isTask) {
                        //放慢速度
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //查询数据
                        HttpUtil.getOKHttpRequest(UrlAPI.SearchLine, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                result = response.body().string();
                                Message message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        });

                        try {
                            Thread.sleep(17000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
