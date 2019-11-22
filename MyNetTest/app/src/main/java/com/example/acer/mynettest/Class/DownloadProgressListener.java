package com.example.acer.mynettest.Class;

/**
 * 下载监听
 */

public interface DownloadProgressListener {
    /**
     * 下载进度监听方法，获取和处理下载点数据的大小
     * @param size 数据大小
     */
    public void onDownloadSize(int size);
}
