package com.sndo.dmp.zol;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddUptime {
//
//    MongoClient client = new MongoClient("192.168.1.82",27017);
//
//    MongoCollection<Document> collection = client.getDatabase("zol_game").getCollection("gameInfo");

    MongoCollection<Document> collection = MongoServer.getSrcCollection("zol_game", "gameInfo");

    public void addUptime(){
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        FindIterable<Document> iterable =  collection.find();
        MongoCursor<Document> doc = iterable.iterator();
        while (doc.hasNext()){
            Document document = doc.next();
            String name = document.getString("name");
            Document andr = (Document)document.get("android");
            Date date = andr.getDate("version_date");

            Document filter = new Document();
            filter.put("name",name);

            Document update = new Document();
            update.put("$set",new Document("uptime",date));
            collection.findOneAndUpdate(filter,update);
        }
    }

    public void update(){
        while(true){
            Document filter = new Document();
            filter.put("status", 2);

            Document update = new Document();
            update.put("$set", new Document("status", 0));

            Document result = collection.findOneAndUpdate(filter, update);
            if(result == null){
                System.out.println("数据取完了！");
                break;
            }
            System.out.println(result.toString());
        }
    }

    public void updateCategory(){
        Document document = new Document();
        List<Integer> list = new ArrayList<>();
        document.put("category_id",list);

        FindIterable<Document> result =  collection.find(document);
        MongoCursor<Document> doc = result.iterator();
        List<Integer> cate = new ArrayList<>();
        cate.add(4);
        while (doc.hasNext()){
            Document d =  doc.next();


            Document update = new Document();
            update.put("$set", new Document("category_id", cate));
            collection.findOneAndUpdate(d, update);

            //System.out.println(d.toString());
        }

    }


    public void  test(){
        System.out.println("输出了！");
    }

    public static void main(String[] args){
        AddUptime addUptime = new AddUptime();
        addUptime.update();
//        List<AddUptime> list = new ArrayList<>();
//        list.add(new AddUptime());
//
//        for(AddUptime addUptime : list){
//            addUptime.test();
//        }
    }
}
