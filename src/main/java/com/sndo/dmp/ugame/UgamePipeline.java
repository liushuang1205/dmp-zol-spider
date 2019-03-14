package com.sndo.dmp.ugame;

import com.mongodb.client.MongoCollection;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

public class UgamePipeline implements Pipeline {

    private final MongoCollection<Document> htmlElementCollection;

    public UgamePipeline(){
        htmlElementCollection = MongoServer.getCollection("Ugame","game");
    }


    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String,Object> resultItemMap =  resultItems.getAll();
        if(resultItemMap.containsKey(UgameConstants.TARGET_PAGE_HTML_ELEMENT)){
            Document htmlElementDoc = resultItems.get(UgameConstants.TARGET_PAGE_HTML_ELEMENT);
            htmlElementCollection.insertOne(htmlElementDoc);
        }

    }
}
