package com.sndo.dmp.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

/**
 * @author yangqi
 * @date 2018/12/24 14:04
 **/
public class MongoServer {

    private static MongoClient client = null;

    static {
        //"192.168.1.151"
        client = new MongoClient(new ServerAddress("192.168.120.128", 27017),//192.168.157.2
                new MongoClientOptions.Builder().build());
    }

    public static MongoClient getClient() {
        return client;
    }

    public static MongoCollection<Document> getCollection(String db, String col) {
        return client.getDatabase(db).getCollection(col);
    }

    public static final UpdateOptions UPSERT_OPTIONS = new UpdateOptions();
    public static final BulkWriteOptions BULK_WRITE_OPTIONS = new BulkWriteOptions();
    static {
        UPSERT_OPTIONS.upsert(true);
        BULK_WRITE_OPTIONS.ordered(false);
    }

    public static void close() {
        if (client != null) {
            client.close();
        }
    }
}
