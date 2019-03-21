package com.sndo.dmp.load;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.sndo.dmp.mongo.MongoServer;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoaderTask {

    private MongoCollection<Document> idCollection;
    private MongoCollection<Document> appGameCollection;
    private MongoCollection<Document> zolGameCollection;
    private FindOneAndUpdateOptions option = new FindOneAndUpdateOptions();
    private final BlockingQueue<Document> queue = new ArrayBlockingQueue<Document>(200);

    public LoaderTask() {
        idCollection = MongoServer.getCollection("game_app", "inrc");
        appGameCollection = MongoServer.getCollection("game_app", "game");
        zolGameCollection = MongoServer.getCollection("zol_game", "gameInfo");

        option.upsert(true);
    }

    private void query() throws InterruptedException {
        Document filter = new Document();
        filter.put("status", 0);

        MongoCursor<Document> iterator = zolGameCollection.find(filter).iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            doc = format(doc);
            if (doc.isEmpty()) {
                continue;
            }
            if (isExist(doc)) {
                continue;
            }
            doc = setID(doc);
            queue.put(doc);
        }
        queue.put(new Document());
    }

    private Document setID(Document doc) {
        int id = getID();
        doc.put("id", id);

        return doc;
    }

    private boolean isExist(Document doc) {
        Document queryDoc = new Document();
        queryDoc.put("name", doc.getString("name"));

        Document resultDoc = appGameCollection.find(queryDoc).first();
        if (resultDoc == null || resultDoc.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Document format(Document doc) {
        doc.remove("status");
        return doc;
    }

    private int getID() {
        Document result = idCollection.findOneAndUpdate(new Document("id", "inrcid"),
                new Document("$inc", new Document("game_value", 1)), option);

        return result.getInteger("game_value");
    }

    private class Insert implements Runnable {

        @Override
        public void run() {
            List<Document> docs = new ArrayList<Document>();
            try {
                while (true) {
                    if (docs.size() % 100 == 0 && docs.size() > 0) {
                        appGameCollection.insertMany(docs);
                        movePicture(docs);
                        doBatchUpdate(docs);
                        docs.clear();
                    }

                    Document doc = queue.take();
                    if (doc.isEmpty()) {
                        queue.put(doc);
                        break;
                    } else {
                        docs.add(doc);
                    }
                }

                if (docs.size() > 0) {
                    appGameCollection.insertMany(docs);
                    movePicture(docs);
                    doBatchUpdate(docs);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doBatchUpdate(List<Document> docs) {
        List<WriteModel<Document>> models = new ArrayList<>();
        for (Document doc : docs) {
            Document filterDoc = new Document();
            filterDoc.put("name", doc.getString("name"));

            Document upsertDoc = new Document();
            upsertDoc.put("status", 1);

            models.add(new UpdateOneModel<Document>(filterDoc, new Document("$set", upsertDoc)));
        }

        zolGameCollection.bulkWrite(models);
    }

    private String srcDir = "G:\\";
    private String dstDir = "G:\\online\\";

    public void movePicture(List<Document> docs) {
        for (Document doc : docs) {
            Document androidDoc = (Document) doc.get("android");
            moveLogoPicture(androidDoc);
            moveScreenshotPicture(androidDoc);
        }
    }

    public void moveLogoPicture(Document doc) {
        movePicture(getSrcLogoDir(doc), getTargetLogoDir(doc));
    }

    public void moveScreenshotPicture(Document doc) {
        movePicture(getSrcScreenshotDir(doc), getTargetScreenshotDir(doc));
    }

    private String getSrcLogoDir(Document doc) {
        String logoUrl = doc.getString("logo_url");
        StringBuilder sourceUrlPath = new StringBuilder(srcDir).append(logoUrl.substring(0, logoUrl.lastIndexOf("/") + 1));
        return sourceUrlPath.toString();
    }

    private String getTargetLogoDir(Document doc) {
        String logoUrl = doc.getString("logo_url");
        StringBuilder sourceUrlPath = new StringBuilder(dstDir).append(logoUrl.substring(0, logoUrl.lastIndexOf("/") + 1));
        return sourceUrlPath.toString();
    }

    private String getSrcScreenshotDir(Document doc) {
        List<String> screenshots = (List<String>) doc.get("game_capture");
        String screenShotUrl = screenshots.get(0);
        StringBuilder sourceUrlPath = new StringBuilder(srcDir).append(screenShotUrl.substring(0, screenShotUrl.lastIndexOf("/") + 1));
        return sourceUrlPath.toString();
    }

    private String getTargetScreenshotDir(Document doc) {
        List<String> screenshots = (List<String>) doc.get("game_capture");
        String screenShotUrl = screenshots.get(0);
        StringBuilder sourceUrlPath = new StringBuilder(dstDir).append(screenShotUrl.substring(0, screenShotUrl.lastIndexOf("/") + 1));
        return sourceUrlPath.toString();
    }

    public void movePicture(String srcDir, String targetDir) {
        File sourceFile = new File(srcDir);
        File targetFile = new File(targetDir);

        if (!sourceFile.exists()) {
            return;
        } else if (targetFile.exists()) {
            return;
        } else {
            if (sourceFile.isFile()) {
                copyFile(sourceFile, targetFile);
            } else if (sourceFile.isDirectory()) {
                copyDirectory(srcDir, targetDir);
            }
        }
        deleteDirectory(srcDir);
    }

    private void copyFile(File sourceFile, File targetFile) {
        if (!sourceFile.canRead()) {
            return;
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;

            try {
                fis = new FileInputStream(sourceFile);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(targetFile);
                bos = new BufferedOutputStream(fos);
                int len = 0;
                while ((len = bis.read()) != -1) {
                    bos.write(len);
                }
                bos.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyDirectory(String sourcePathStr, String targetPathStr) {
        if (!new File(sourcePathStr).canRead()) {
            return;
        } else {
            (new File(targetPathStr)).mkdirs();

            File[] files = new File(sourcePathStr).listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    copyFile(new File(sourcePathStr + File.separator + files[i].getName()), new File(targetPathStr + File.separator + files[i].getName()));
                } else if (files[i].isDirectory()) {
                    copyDirectory(sourcePathStr + File.separator + files[i].getName(), targetPathStr + File.separator + files[i].getName());
                }
            }
        }
    }

    public void deleteFile(String sourceUrlPath) {
        File file = new File(sourceUrlPath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    public void deleteDirectory(String sourceUrlPath) {
        if (!sourceUrlPath.endsWith(File.separator)) {
            sourceUrlPath = sourceUrlPath + File.separator;
        }

        File dirFile = new File(sourceUrlPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }

        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath());
            }
        }

        dirFile.delete();
    }

    public void start() throws InterruptedException {
        Thread[] inserters = new Thread[1];
        for (int i = 0; i < inserters.length; i++) {
            inserters[i] = new Thread(new Insert());
            inserters[i].start();
        }

        query();

        for (int i = 0; i < inserters.length; i++) {
            inserters[i].join();
        }

    }

    public void close() {
        MongoServer.close();
    }

    public static void main(String[] args) {
        LoaderTask task = new LoaderTask();
        try {
            task.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (task != null) {
                task.close();
            }
        }

    }
}
