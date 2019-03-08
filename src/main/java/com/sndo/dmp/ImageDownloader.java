package com.sndo.dmp;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @date 2019/3/7
 */
public class ImageDownloader {

    private static BlockingQueue<Image> queue = new ArrayBlockingQueue<>(2000);

    public static void add(String imageUrl, String saveFilePath) throws InterruptedException {
        Image image = new Image();
        image.setUrl(imageUrl);
        image.setPath(saveFilePath);

        queue.put(image);
    }

    private class Downloader implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Image image  = queue.take();
                    if (image.isEmpty()) {
                        queue.put(image);
                        break;
                    } else {
                        downloadImage(image.getUrl(), image.getPath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void downloadImage(String imageUrl, String saveFilePath) {
        URL url;
        try {
            url = new URL(imageUrl);
            FileUtils.copyURLToFile(url, new File(saveFilePath));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Thread[] downloaders = new Thread[15];
        for (int i = 0; i < downloaders.length; i++) {
            downloaders[i] = new Thread(new Downloader());
            downloaders[i].start();
        }
    }

    public void close() throws InterruptedException {
        Image image = new Image();
        image.setEmpty(true);
        queue.put(image);
    }

    static class Image {
        private String url;
        private String path;
        private boolean isEmpty = false;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }
    }
}
