package com.example.acer.bus1.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.acer.bus1.Activity.LoginActivity;
import com.example.acer.bus1.Activity.MainActivity;
import com.example.acer.bus1.Activity.StartActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 获取路线信息
 */

public class TaskLine {

    private String result;
    private Boolean isTask = MainActivity.isTask;
    private ArrayList<String> stationlist;
    private Context context;
    private String select_train;

    public TaskLine(String select_train,Context context) {
        stationlist = new ArrayList<>();
        this.context = context;
        this.select_train = select_train;
    }

//    public TaskLine(Context context) {
//        this.context = context;
//    }
//
//    public void start_() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent("com.example.acer.bus1.Activity.TaskLine");
//                context.sendBroadcast(intent);
//            }
//        } , 9000);
//    }

    public void setSelect_train(String select_train) {
        this.select_train = select_train;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isTask = true;

                    //TODO 将获取的路线信息result转换为ArrayList数组stationList

                    Intent intent = new Intent("com.example.acer.bus1.Activity.TaskLine");
                    intent.putExtra("stations",stationlist);
                    context.sendBroadcast(intent);

                    //SaveStationLine();
                    break;
            }
        }
    };

    //开始查询是否有新路线任务
    public void StartSearch() {

        stationlist.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isTask) {
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

    /**
     * 保存路线信息
     * 依次保存任务状态标识、车次号、时间和具体站点信息
     */
    public void SaveStationLine(ArrayList<String> stationlist) {
        StringBuilder builder = new StringBuilder();
        FileOutputStream outputStream;
        BufferedWriter writer = null;
        try {
            outputStream = context.openFileOutput("station", Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            Calendar calendar = Calendar.getInstance();
            builder.append(String.valueOf(isTask)).append(",")
                    .append(select_train).append(",");
            builder.append(calendar.get(Calendar.MONTH) + 1).append("月")
                    .append(calendar.get(Calendar.DAY_OF_MONTH)).append("日").append(",");
            for (String station : stationlist) {
                builder.append(station).append(",");
            }
            builder.append("\n");
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //当一条路线行驶完成后更新历史路线信息
    public void UpdateHistory() {

        ArrayList<ArrayList<String>> stationList = new ArrayList<>();
        ArrayList<String> isTask = new ArrayList<>();
        ArrayList<String> trainNum = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        FileInputStream inputStream ;
        FileOutputStream outputStream;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        ArrayList<String> strings;
        try {
            inputStream = context.openFileInput("station");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = reader.readLine()) != null) {
                strings = new ArrayList<>();
                Collections.addAll(strings,str.split(","));
                isTask.add(strings.get(0));
                trainNum.add(strings.get(1));
                time.add(strings.get(2));
                strings.remove(2);
                strings.remove(1);
                strings.remove(0);
                stationList.add(strings);
            }

            try {
                reader.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

            isTask.remove(isTask.size()-1);
            isTask.add("false");

            outputStream = context.openFileOutput("station",Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (int i = 0; i < isTask.size(); i++) {
                builder.append(isTask.get(i)).append(",")
                        .append(trainNum.get(i)).append(",")
                        .append(time.get(i)).append(",");

                for (String station : stationList.get(i)) {
                    builder.append(station).append(",");
                }
                builder.append("\n");
            }
            writer.write(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
