package com.sndo.dmp.zol;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

public class ZolScheduler implements Scheduler {

    private final MongoCollection<Document> collection;
    UpdateOptions updateOptions = new UpdateOptions();

    public ZolScheduler(){
        collection = MongoServer.getCollection("zolTest", "urls");
        updateOptions.upsert(true);
    }

    @Override
    public void push(Request request, Task task) {
        int pageIndex = (Integer) request.getExtra(ZolConstants.PAGE_INDEX);
        String referer = (String) request.getExtra(ZolConstants.PAGE_REFERER);
        String url = request.getUrl();

        if (!isExist(url, pageIndex)) {
            doInsert(url, referer, pageIndex);
        }
    }

    @Override
    public Request poll(Task task) {
        Document filter = new Document();
        filter.put("status", 0);
        filter.put("num", new Document("$lte", 1));

        Document update = new Document();
        update.put("$set", new Document("status", 2));
        update.put("$inc", new Document("num", 1));

        Document result = collection.findOneAndUpdate(filter, update);
        if (result != null) {
            Request request = new Request();
            request.setUrl(result.getString("url"));

            request.putExtra(ZolConstants.PAGE_REFERER,
                    result.getString("referer"));

            Integer index = result.getInteger("index");
            request.putExtra(ZolConstants.PAGE_INDEX, index);
            return request;
        } else {
            return null;
        }
    }

    private void doInsert(String url, String referer, int pageIndex) {
        Document doc = new Document();
        doc.put("url", url);
        doc.put("referer", referer);
        doc.put("index", pageIndex);
        doc.put("status", 0);
        doc.put("num", 0);

        if (pageIndex < 3)  {
            collection.insertOne(doc);
        }
    }

    private boolean isExist(String url, int pageIndex) {
        Document doc = new Document();
        doc.put("url", url);
        doc.put("index", pageIndex);

        Document result = collection.find(doc).first();
        return result != null;
    }
}
