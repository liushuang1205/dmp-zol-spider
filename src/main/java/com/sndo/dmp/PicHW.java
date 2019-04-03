package com.sndo.dmp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PicHW {

//    private MongoClient client = null;
//    private MongoCollection<Document> collection = null;

    public PicHW(){
//        client = new MongoClient("192.168.120.128",27017);
//        collection = client.getDatabase("zol_game").getCollection("gameInfo");
    }

    public String getVerticalImage(String cur) {
        File file = new File(cur);

        BufferedImage sourceImg = null;
        if(file.exists()) {
            if(file.isFile()) {
                try {
                    sourceImg = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int height = sourceImg.getHeight();
                int width = sourceImg.getWidth();
                System.out.println("h:" + height);
                System.out.println("w:" + width);
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

//    public String getVerticalPic(String cur) {
//        InputStream murl = null;
//        try {
//            murl = new URL(cur).openStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedImage sourceImg = null;
//        if(murl != null) {
//            try {
//                sourceImg = ImageIO.read(murl);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int height = sourceImg.getHeight();
//            int width = sourceImg.getWidth();
//            System.out.println("h:" + height);
//            System.out.println("w:" + width);
//            if (height > width) {
//                return "h";// 竖版
//            } else {
//                return "w"; // 横版
//            }
//        } else {
//            return null;
//        }
//    }

    MongoCollection<Document> collection = null;

    public void qury(){

        //MongoCursor<Document> srcDocs = collection.find().iterator();
        collection = MongoServer.getOnlineCollection("game_app","app_game");

        Document filter = new Document();
        filter.put("name","龙珠觉醒");

        MongoCursor<Document> srcDocs = collection.find(filter).iterator();
        while (srcDocs.hasNext()){
            List<String> gameCaptureFlag = new ArrayList<>();
            Document srcDoc = srcDocs.next();
            Document andr = (Document) srcDoc.get("android");
            List<String> gameCapture = (List<String>)andr.get("game_capture");

            for (String url : gameCapture){
                StringBuilder builder = new StringBuilder("G:\\123\\");
                builder.append(url.replace("/","\\"));
//                StringBuilder builder = new StringBuilder("/var/rinse_html/changgame/m/");
//                builder.append(url);
                String hw = getVerticalImage(builder.toString());

                if(hw != null){
                    thumbnail(builder.toString(),builder.toString());
                    gameCaptureFlag.add(hw);
                }
            }

            if(gameCaptureFlag != null && gameCaptureFlag.size() > 0){
                updateWH(gameCaptureFlag, srcDoc);
            }
        }
    }

    /**
     * 压缩图片
     *
     * @param srcImagePath 源图片路径
     * @param desImagePath 目标路径
     */
    public static void thumbnail(String srcImagePath, String desImagePath) {

        System.load("D:\\opencv\\opencv\\build\\java\\x64\\opencv_java310.dll");

        //读取图像到矩阵中,取灰度图像
        Mat src = Imgcodecs.imread(srcImagePath);
        int h = src.height();
        int w = src.width();

        //复制矩阵进入dst
        Mat dst = src.clone();
        Imgproc.resize(src, dst, new Size(w * 400 / h, 400));

        Imgcodecs.imwrite(desImagePath, dst);

    }


    private void updateWH(List<String> gameCaptureFlag, Document srcDoc) {
        Document doc = new Document();
        doc.put("name", srcDoc.getString("name"));

        Document update = new Document();
        update.put("$set",new Document("android.game_capture_flag",gameCaptureFlag));
        collection.findOneAndUpdate(doc,update);
    }


    public void start(){
        qury();
    }


    public static void main(String[] args)  {

        PicHW pic = new PicHW();
        pic.start();
 //       thumbnail("G:\\04025530-045b-4a01-a4cc-4d2c8609973d.jpg","G:\\123.jpg");
        //pic.getVerticalImage("G:\\123\\04025530-045b-4a01-a4cc-4d2c8609973d.jpg");
    }
}
