package com.example.acer.mynettest.Class;

import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载线程
 */

public class DownloadThread extends Thread {
    private static final String TAG = "DownloadThread";
    private File saveFile;    //下载的数据保存的文件
    private URL downloadUrl;
    private int block;        //每条线程下载的长度
    private int threadId = -1; //初始化线程id设置
    private int downloadLength; //该线程已经下载的数据长度
    private boolean finish = false;  //该线程是否完成下载标志
    private FileDownLoader downLoader;  //文件下载器

    public DownloadThread(FileDownLoader downLoader, URL downloadUrl, File saveFile, int block,
                          int downloadLength, int threadid) {
        this.downLoader = downLoader;
        this.downloadUrl = downloadUrl;
        this.saveFile = saveFile;
        this.block = block;
        this.downloadLength = downloadLength;
        this.threadId = threadid;
    }

    @Override
    public void run() {
        if (downloadLength < block) {
            try {
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.setConnectTimeout(5 * 1000);
                connection.setRequestMethod("GET");
                //设置客户端可接受的返回数据类型
                connection.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg," +
                        "application/x-shockwave-flash,application/xaml+xml,application/vnd.ms-xpsdocument" +
                        ",application/x-ms-xbap,application/x-ms-application,application/vnd.ms-excel," +
                        "application/vnd.ma-powerpoint,application/msword,*/*");
                //设置客户端使用的语言为中文
                connection.setRequestProperty("Accept-Language", "zh-CN");
                //设置请求的来源，便于对访问来源进行统计
                connection.setRequestProperty("Referer", downloadUrl.toString());
                connection.setRequestProperty("Charset", "UTF-8");//设置通信编码为UTF-8
                int startPos = block * (threadId - 1) + downloadLength;//开始位置
                int endPos = block * threadId - 1;//结束位置
                //设置获取实体数据的范围，如果超出了数据范围的大小会自动返回实际的数据大小
                connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                //客户端用户代理
                connection.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 8.0;" +
                        "Window NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR" +
                        "3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729");
                connection.setRequestProperty("Connection", "Keep-Alive");//使用长连接

                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int offset = 0;
                print("Thread" + this.threadId + "starts to download from position" + startPos);
                RandomAccessFile threadFile = new RandomAccessFile(this.saveFile, "rwd");
                threadFile.seek(startPos);//文件指针指向开始下载的位置

                while (!downLoader.getExited() && (offset = inputStream.read(buffer, 0, 1024)) != -1) {    //但用户没有要求停止下载，同时没有到达请求数据的末尾时候会一直循环读取数据
                    threadFile.write(buffer, 0, offset);    //直接把数据写到文件中
                    downloadLength += offset; //把新下载的已经写到文件中的数据加入到下载长度中
                    downLoader.update(this.threadId, downloadLength); //把该线程已经下载的数据长度更新到数据库和内存哈希表中
                    downLoader.append(offset);  //把新下载的数据长度加入到已经下载的数据总长度中
                }//该线程下载数据完毕或者下载被用户停止
                threadFile.close(); //Closes this random access file stream and releases any system resources associated with the stream.
                inputStream.close();   //Concrete implementations of this class should free any resources during close
                if (downLoader.getExited()) {
                    print("Thread " + this.threadId + " has been paused");
                } else {
                    print("Thread " + this.threadId + " download finish");
                }
                this.finish = true;   //设置完成标志为true，无论是下载完成还是用户主动中断下载

            } catch (Exception e) {
                this.downloadLength = -1; //设置该线程已经下载的长度为-1
                print("Thread " + this.threadId + ":" + e);    //打印出异常信息

            }
        }
    }

    //打印信息
    private static void print(String msg) {
        Log.i(TAG, msg);
    }

    //下载是否完成
    public boolean isFinish() {
        return finish;
    }

    //已经下载的内容大小;如果返回-1，则代表下载失败
    public int getDownloadLength() {
        return downloadLength;
    }

}
