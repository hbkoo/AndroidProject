package com.example.acer.mynettest.Class;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by acer on 2017/7/26.
 */

public class FileDownLoader {

    private static final String TAG = "FileDownLoader";
    private static final int RESPONSEOK = 200;  //响应码200，即访问成功
    private Context context;
    private FileService fileService;
    private boolean exited;     //停止下载标志
    private int downloadSize = 0;   //已下载文件长度
    private int fileSize = 0;       //原始文件长度
    private DownloadThread[] threads;  //根据线程数设置下载线程池
    private File saveFile;  //数据保存到的本地文件
    private Map<Integer, Integer> data = new ConcurrentHashMap<>();
    private int block;   //每条线程下载的长度
    private String downloadUrl;  //下载路径

    //获取线程数
    public int getThreadSize() {
        return threads.length;
    }

    //退出下载
    public void exit() {
        this.exited = true;
    }
    public boolean getExited(){
        return this.exited;
    }


    //获取文件大小
    public int getFileSize() {
        return this.fileSize;
    }

    //累计已下载大小
    //使用同步关键字synchronized解决并发访问问题
    public synchronized void append(int size) {
        downloadSize += size;//把实时下载的长度加入到总下载长度中
    }

    //更新指定线程最后下载的位置
    public synchronized void update(int threadId, int pos) {
        this.data.put(threadId, pos);
        fileService.update(downloadUrl, threadId, pos);
    }

    public FileDownLoader(Context context, String downloadUrl, File fileSaveDir, int threadNum) {
        try {
            this.context = context;
            this.downloadUrl = downloadUrl;
            fileService = new FileService(context);
            URL url = new URL(downloadUrl);
            //如果指定的文件不存在，则创建目录
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            this.threads = new DownloadThread[threadNum];

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            connection.setRequestProperty("Referer", downloadUrl);
            connection.setRequestProperty("Charset", "UTF-8");//设置通信编码为UTF-8
            //客户端用户代理
            connection.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 8.0;" +
                    "Window NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR" +
                    "3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729");
            connection.connect();
            printResponseHeader(connection);//打印返回的http头字段集合
            if (connection.getResponseCode() == RESPONSEOK) {
                this.fileSize = connection.getContentLength();//根据相应获取文件大小
                //当文件大小小于0时抛出文件运行时异常
                if (fileSize < 0) throw new RuntimeException("Unkown file size ");
                String fileName = getFileName(connection);
                //根据文件保存目录和文件名构建保存文件
                this.saveFile = new File(fileSaveDir, fileName);
                //获取下载记录
                Map<Integer, Integer> loaddata = fileService.getData(downloadUrl);
                if (loaddata.size() > 0) {
                    for (Map.Entry<Integer, Integer> entry : loaddata.entrySet()) {
                        //把各线程已经下载的数据长度放在data中
                        data.put(entry.getKey(), entry.getValue());
                    }
                }
                if (this.data.size() == this.threads.length) {
                    for (int i = 0; i < this.threads.length; i++) {
                        this.downloadSize += this.data.get(i + 1);//计算已经下载的数据之和
                    }
                    print("已经下载的长度" + this.downloadSize + "个字节");
                }

                //计算每条线程下载的数据长度
                this.block = (this.fileSize % this.threads.length) == 0 ?
                        this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
            } else {
                print("服务器响应错误" + connection.getResponseCode() + connection.getResponseMessage());
                throw new RuntimeException("serve response error");
            }
        } catch (Exception e) {
            print(e.toString());
            throw new RuntimeException("Can't connection this url");
        }
    }

    /**
     * 开始下载文件
     *
     * @param listener 监听下载数量的变化，如果不需要了解实时下载的数量，可以设置为null
     * @return 已下载文件大小
     */
    public int download(DownloadProgressListener listener) throws Exception {

        try {
            RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rwd");
            if (this.fileSize > 0) randOut.setLength(this.fileSize);
            randOut.close();//关闭该文件使设置生效
            URL url = new URL(this.downloadUrl);
            if (this.data.size() != this.threads.length) {
                //如果原先未曾下载或者下载线程数目不一样
                this.data.clear();
                for (int i = 0; i < this.threads.length; i++) {
                    this.data.put(i + 1, 0);//初始化每条线程下载的长度为0
                }
                this.downloadSize = 0;//设置已经下载长度为0
            }
            for (int i = 0; i < this.threads.length; i++) {
                //通过特定线程id获取该线程已经下载的数据长度
                int downloadedLength = this.data.get(i + 1);
                //判断线程是否下载完成，否则继续下载
                if (downloadedLength < block && this.downloadSize < this.fileSize) {
                    this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block,
                            this.data.get(i + 1), i + 1);
                    this.threads[i].setPriority(7);//设置线程的优先级
                    this.threads[i].start();
                } else {
                    this.threads[i] = null;//表明该线程已经完成下载任务
                }
            }
            //如果存在下载记录则删除他们重新添加
            fileService.delete(this.downloadUrl);
            //把已经下载的实时数据写进数据库
            fileService.save(this.downloadUrl, this.data);
            boolean notFinish = true; //下载未完成
            while (notFinish) {
                Thread.sleep(900);
                notFinish = false;//假定全部线程下载完成
                for (int i = 0; i < this.threads.length; i++) {
                    //如果发现线程未下载完成
                    if (this.threads[i] != null && !this.threads[i].isFinish()) {
                        notFinish = true;//设置标记为下载未完成
                        //如果下载失败，再重新在已经下载的基础上继续下载
                        if (this.threads[i].getDownloadLength() == -1) {
                            this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block,
                                    this.data.get(i + 1), i + 1);//重新开辟下载线程
                            this.threads[i].setPriority(7);//设置线程的优先级
                            this.threads[i].start();
                        }
                    }
                }
                //通知目前已经完成的下载长度
                if (listener != null) {
                    listener.onDownloadSize(this.downloadSize);
                }
            }

            if (downloadSize == this.fileSize) {
                fileService.delete(this.downloadUrl);
            }
        } catch (Exception e) {
            print(e.toString());
            throw new Exception("File Download Error");
        }
        return this.downloadSize;
    }

    //获取文件名
    private String getFileName(HttpURLConnection connection) {
        //从下载路径的字符串中获取问价名称
        String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf("/") + 1);
        if (filename == null || "".equals(filename.trim())) {
            for (int i = 0; ; i++) {
                //从返回流中获取特定索引的头字段值
                String mine = connection.getHeaderField(i);
                if (mine == null) break;
                if ("content-disposition".equals(connection.getHeaderField(i).toLowerCase())) {
                    //获取content-disposition返回头字段，里面可能会包含文件名
                    //使用正则表达式查询文件名
                    Matcher matcher = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
                    if (matcher.find()) {
                        //如果有符合正则表达式规则的字符串
                        return matcher.group(i);
                    }
                }
            }
            //由网卡上的标识数字（每个网卡都有唯一的标识号）
            // 以及CPU时钟的唯一数字生成的一个16字节的二进制
            filename = UUID.randomUUID() + ".tmp";
        }
        return filename;
    }

    //打印http头字段
    private void printResponseHeader(HttpURLConnection connection) {
        //获取http相应头字段
        Map<String, String> header = getHttpReponseHeader(connection);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey() + ":" : "";
            print(key + entry.getValue());//答应键和值的组合
        }

    }

    //获取http相应头字段
    private Map<String, String> getHttpReponseHeader(HttpURLConnection connection) {
        Map<String, String> header = new LinkedHashMap<>();
        for (int i = 0; ; i++) {
            //getHeaderField(i)用于返回第i个字段的值
            String fieldValue = connection.getHeaderField(i);
            //如果第i个字段没有值，则表明头字段部分已经循环完毕
            if (fieldValue == null)
                break;
            //getHeaderFieldKey(i)用于返回第i个头字段的键
            header.put(connection.getHeaderFieldKey(i), fieldValue);
        }
        return header;
    }

    //打印信息
    private void print(String msg) {
        Log.i(TAG, msg);
    }

}
