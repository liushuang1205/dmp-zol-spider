package com.sndo.dmp.game;

import org.bson.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class UGameTask {

    private UGameDownloader downloader;
    private UGameParser parser;
    private UGameReadUrl readUrl;
    private UGamePipeline pipeline;

    public UGameTask(){
        downloader = new UGameDownloader();
        parser = new UGameParser();
        readUrl = new UGameReadUrl();
        pipeline = new UGamePipeline();
    }

    public void start() throws IOException, URISyntaxException {
        List<String> urls = readUrl.readUrl();
        for (String url : urls) {
            byte[] bytes = downloader.download(url);
            Document doc = parser.parse(url, bytes);
            pipeline.doSave(doc);
        }
    }

    public static void main(String[] args) {
        UGameTask task = new UGameTask();
        try {
            task.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                task.pipeline.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
