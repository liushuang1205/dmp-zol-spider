package com.sndo.dmp.dome;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoUpdateTest {

    private static MongoCollection<Document> collection;
    private static FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
    private static FindOneAndDeleteOptions deleteOptions = new FindOneAndDeleteOptions();
    private static UpdateOptions options = new UpdateOptions();


    public MongoUpdateTest(){
        collection = MongoServer.getCollection("updateTest","test");
        updateOptions.upsert(true);
        options.upsert(true);

    }



    public static void updateAndAdd(){
        Document qury = new Document();
        qury.put("name","中国象棋");

        Document ios = new Document();
        ios.put("logo","logo/561321s54d62116s5d1f32/sdfasdf12654df5141f5s1.jpg");
        ios.put("size","12G");

        Document update = new Document();
        update.put("desc","测试实施还是会死是是是是是红寺湖似乎四是十四师");
        update.put("android.size","测试123");
        update.put("ios",ios);

        collection.findOneAndUpdate(qury, new Document("$set", update), updateOptions);
    }

    public static void updateAndDel(){
        Document qury = new Document();
        qury.put("name","中国象棋");

        Document ios = new Document();
        ios.put("logo","logo/561321s54d62116s5d1f32/sdfasdf12654df5141f5s1.jpg");
        ios.put("size","12G");

        Document update = new Document();
        update.put("desc","测试实施还是会死是是是是是红寺湖似乎四是十四师");

        collection.updateOne(qury ,new Document("$unset", update));
    }


    public static void main(String[] args){
        updateAndDel();
    }
}
