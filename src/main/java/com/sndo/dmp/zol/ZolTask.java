package com.sndo.dmp.zol;

import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.mongo.MongoServer;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liushuang
 * @date 2019/3/7
 */
public class ZolTask {

    public static void main(String[] args) throws Exception {
        Request request = new Request();
        request.setUrl("http://sj.zol.com.cn/android_game/");
        request.putExtra(ZolConstants.PAGE_INDEX, 0);

        List<SpiderListener> spiderListeners = new ArrayList<>();
        spiderListeners.add(new ZolSpiderListener());

        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.start();

        Spider.create(new ZolProcessor())
                .setScheduler(new ZolScheduler())
                .addPipeline(new ZolPipeline())
                .addRequest(request)
                .setSpiderListeners(spiderListeners)
                .thread(5)
                .run();

        imageDownloader.close();
        MongoServer.close();
    }
}
