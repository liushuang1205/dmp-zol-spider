package com.sndo.dmp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class OnlinePicHW {

    private MongoClient client = new MongoClient("192.168.120.128",27017);
    private MongoCollection<Document> pphelperCollection = null;
    private BlockingQueue<Document> queue = new ArrayBlockingQueue<>(1000);

    public OnlinePicHW(){
        //pphelperCollection = MongoServer.getSrcCollection("pc6_game","gameInfo");
        pphelperCollection = client.getDatabase("pphelper_game").getCollection("gameInfo");
    }


    public String getVerticalImage(String cur) throws IOException {
        File file = new File(cur);
        BufferedImage sourceImg;
        if(file.exists()) {
            if(file.isFile()) {
                sourceImg = ImageIO.read(file);
                int height = sourceImg.getHeight();
                int width = sourceImg.getWidth();
                if (height > width) {

                    return "h";// 竖版
                } else {
                    return "w"; // 横版
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void qury() throws InterruptedException, IOException {
        Document doc = new Document();
        doc.put("status",0);


        MongoCursor<Document> srcDocs = pphelperCollection.find(doc).iterator();
        while (srcDocs.hasNext()){
            long start = System.currentTimeMillis();
            List<String> gameCaptureFlag = new ArrayList<>();
            Document srcDoc = srcDocs.next();
            Document android = (Document) srcDoc.get("android");
            if(android != null){
                List<String> gameCapture = (List<String>)android.get("game_capture");

                if(gameCapture != null && gameCapture.size() > 0){

                    for (String url : gameCapture){
                        long b = System.currentTimeMillis();
                        StringBuilder builder = new StringBuilder("G:\\pphelper\\");///home/staff/dmp_workspace/dmp-changgame-spider/data/pc6/
                        builder.append(url);
                        String hw = getVerticalImage(builder.toString());

                        if (hw != null) {
                            gameCaptureFlag.add(hw);
                        }
                        System.out.println("pic : end-time ------->" + (System.currentTimeMillis() - b));
                    }

                }
            }

            if(gameCaptureFlag != null && gameCaptureFlag.size() > 0){
                android.put("game_capture_flag", gameCaptureFlag);
                queue.put(srcDoc);
            }
            long end = System.currentTimeMillis();
            System.out.println("hw : end-time ------->" + (end - start));
        }

        queue.put(new Document());
    }

    private int i = 0;

    private class Update implements Runnable {

        @Override
        public void run() {
            List<Document> docs = new ArrayList<>();
            try {
                while (true) {
                    if (docs.size() % 10 == 0 && docs.size() > 0) {
                        updateCatp(docs);
                        docs.clear();
                    }

                    Document doc = queue.take();
                    if(doc.isEmpty()){
                        queue.put(doc);
                        break;
                    } else {
                        docs.add(doc);
                    }
                }
                if(docs.size() > 0){
                    updateCatp(docs);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCatp(List<Document> docs) {
        long start = System.currentTimeMillis();
        List<WriteModel<Document>> models = new ArrayList<>();
        for (Document doc : docs) {
            Document filterDoc = new Document();
            filterDoc.put("name", doc.getString("name"));

            doc.remove("_id");

            models.add(new UpdateOneModel<Document>(filterDoc, new Document("$set", doc)));
        }
        i++;
        pphelperCollection.bulkWrite(models);
        long end = System.currentTimeMillis();
        System.out.println("update : end-time ------->" + (end - start));
    }



    public void start() throws InterruptedException, IOException {
        Thread[] thread = new Thread[5];
        for (int i = 0; i < thread.length; i++){
            thread[i] = new Thread(new Update());
            thread[i].start();
        }

        qury();

        for (int i = 0; i < thread.length; i++) {
            thread[i].join();
        }

        System.out.println("执行完毕！ i---> " + i);
    }


    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        OnlinePicHW pic = new OnlinePicHW();
        try {
            pic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("time ------->" + (end - start));

    }
}
