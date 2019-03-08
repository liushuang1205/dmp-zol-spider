package com.sndo.dmp.zol;

import com.mongodb.client.MongoCollection;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

public class ZolPipeline implements Pipeline {

    private final MongoCollection<Document> htmlElementCollection;
    private final MongoCollection<Document> htmlContentCollection;

    public ZolPipeline() {
        htmlElementCollection = MongoServer.getCollection("zolTest", "gameInfo");
        htmlContentCollection = MongoServer.getCollection("zolTest", "htmlContent");
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> resultItemMap = resultItems.getAll();
        if (resultItemMap.containsKey(ZolConstants.TARGET_PAGE_HTML_ELEMENT)) {
            Document htmlElementDoc = resultItems.get(ZolConstants.TARGET_PAGE_HTML_ELEMENT);
            htmlElementCollection.insertOne(htmlElementDoc);
        }

        if (resultItemMap.containsKey(ZolConstants.TARGET_PAGE_HTML_CONTENT)) {
            Document htmlContentDoc = resultItems.get(ZolConstants.TARGET_PAGE_HTML_CONTENT);
            htmlContentCollection.insertOne(htmlContentDoc);
        }
    }
}
