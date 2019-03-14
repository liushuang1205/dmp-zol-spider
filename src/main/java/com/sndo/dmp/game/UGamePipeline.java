package com.sndo.dmp.game;

import com.mongodb.client.MongoCollection;
import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

public class UGamePipeline {

    private MongoCollection<Document> collection;
    private ImageDownloader imageDownloader;

    public UGamePipeline(){
        collection = MongoServer.getCollection("game_app","game");
        imageDownloader = new ImageDownloader();

        imageDownloader.start();
    }

    public void doSave(Document doc) {
        collection.insertOne(doc);
    }

    public void close() throws InterruptedException {
        imageDownloader.close();
        MongoServer.close();
    }
}
