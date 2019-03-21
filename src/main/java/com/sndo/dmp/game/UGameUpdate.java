package com.sndo.dmp.game;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mongodb 中game表中数据的修改
 */
public class UGameUpdate {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> collection1;

    public UGameUpdate(){
        collection = MongoServer.getCollection("game_app","game");
        collection1 = MongoServer.getCollection("game_app","gameTest");
    }

    public List<Map<String,Object>> find(){
        List<Map<String,Object>> data = new ArrayList<>();
        FindIterable<Document> list = collection1.find();
        for (Document doc : list){
            Map<String,Object> map = new HashMap<>();
            map.put("id",doc.get("id"));
            map.put("category_id",doc.get("category_id"));
            map.put("word_segment",doc.get("word_segment"));
            data.add(map);
        }
        return data;
    }

    public void update(List<Map<String,Object>> date){
        for(Map<String,Object> map : date){
            Document oldDoc = new Document();
            Document newDoc = new Document();
            for (Map.Entry<String, Object> entry : map.entrySet()){
                if("id".equals(entry.getKey())){
                    oldDoc.put("id",entry.getValue());
                }else if("category_id".equals(entry.getKey())){
                    newDoc.put("category_id",entry.getValue());
                }else if("word_segment".equals(entry.getKey())){
                    newDoc.put("word_segment",entry.getValue());
                }
            }
            collection.updateMany(Filters.eq("id", oldDoc.getInteger("id")), new Document("$set",newDoc));
        }
        System.out.println("修改完毕！");
    }

    public void findName(){
        List<String> names = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\liushuang\\Desktop\\游戏名.txt"),"GBK"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String line;
        while(true){
            try {
                if (!((line = reader.readLine()) != null)) break;
                names.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        int i = 0;
        for(String name : names){
            Document filter = new Document();
            filter.put("name", name);
            Document doc = collection.find(filter).first();

            System.out.println("id :"+ doc.getInteger("id") +" == " + i +"   --->" + doc.getString("name"));
            i++;
        }

    }

    public static void main(String[] args){
        UGameUpdate update = new UGameUpdate();
        /*List<Map<String,Object>> data = update.find();
        update.update(data);*/
        update.findName();
    }
}
