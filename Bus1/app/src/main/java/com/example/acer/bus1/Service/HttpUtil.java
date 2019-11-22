package com.example.acer.bus1.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 向服务器发送、请求数据
 */

public class HttpUtil {

    //向服务器发送请求并获取数据
    public static void getOKHttpRequest(String address,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //向服务器发送数据
    public static void postOKHttpRequest(String address, RequestBody requestBody,
                                         okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
