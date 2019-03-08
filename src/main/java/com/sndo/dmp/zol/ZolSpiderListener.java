package com.sndo.dmp.zol;

import com.mongodb.client.MongoCollection;
import com.sndo.dmp.mongo.MongoServer;
import org.assertj.core.util.Strings;
import org.bson.Document;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

/**
 * 监听
 * @date 2019/3/7
 */
public class ZolSpiderListener implements SpiderListener {

    private final MongoCollection<Document> collection;

    public  ZolSpiderListener() {
        collection = MongoServer.getCollection("zolTest", "urls");
    }

    @Override
    public void onSuccess(Request request) {
        int pageIndex = (Integer)request.getExtra(ZolConstants.PAGE_INDEX);
        String url = request.getUrl();
        String referer = (String) request.getExtra(ZolConstants.PAGE_REFERER);

        Document filter = new Document();
        filter.put("url", url);
        filter.put("index", pageIndex);
        if (!Strings.isNullOrEmpty(referer)) {
            filter.put("referer", referer);
        }

        Document update = new Document("$set", new Document("status", 1));
        collection.findOneAndUpdate(filter, update);
    }

    @Override
    public void onError(Request request) {

    }
}
