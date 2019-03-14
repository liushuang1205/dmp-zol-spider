package com.sndo.dmp.ugame;

import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.mongo.MongoServer;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * @author liushuang
 * @date 2019/3/13
 */
public class UgameTask {

    public static void main(String[] args) throws Exception {

        UgameParser parser = new UgameParser();
        List<String> urls = parser.getUrl();

        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.start();

        Spider.create(new UgameProcessor())
                .startUrls(urls)
                .addPipeline(new UgamePipeline())
                .thread(5)
                .run();

        imageDownloader.close();
        MongoServer.close();
    }
}
