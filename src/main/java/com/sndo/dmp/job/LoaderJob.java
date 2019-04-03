package com.sndo.dmp.job;

import com.sndo.dmp.dome.LoaderConfig;
import com.sndo.dmp.load.LoaderTask;
import com.sndo.dmp.mongo.MongoServer;
import com.sndo.dmp.util.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

public class LoaderJob {

    private LoaderTask zolTask ;
    private LoaderTask pphelperTask;
    private LoaderTask pc6Task;
    private LoaderTask appstroeTask;

    private List<LoaderTask> tasks = new ArrayList<>();

    public LoaderJob(){
        zolTask = new LoaderTask(getZolConfig());
        pphelperTask = new LoaderTask(getPPHelperConfig());
//        pc6Task = new LoaderTask(getPc6Config());
        appstroeTask = new LoaderTask(getAppStoreConfig());

        tasks.add(zolTask);
        tasks.add(pphelperTask);
//        tasks.add(pc6Task);
        tasks.add(appstroeTask);
    }

    private LoaderConfig getOnlineConfig() {
        LoaderConfig config = new LoaderConfig();
        config.setOnlineDB(ConfigUtil.getString("mongo.onlineDB"));
        config.setOnlineGameCol(ConfigUtil.getString("mongo.onlineGameCol"));
        config.setOnlineInrcCol(ConfigUtil.getString("mongo.onlineInrcCol"));
        return config;
    }

    public LoaderConfig getZolConfig(){
        LoaderConfig config = getOnlineConfig();

        config.setSrcDB(ConfigUtil.getString("mongo.zol.srcDB"));
        config.setSrcGameCol(ConfigUtil.getString("mongo.srcGameCol"));

        config.setOnlinePicDir(ConfigUtil.getString("mongo.onlinePicDir"));
        config.setSrcPicDir(ConfigUtil.getString("mongo.zol.srcPicDir"));
        return config;
    }

    public LoaderConfig getPPHelperConfig(){
        LoaderConfig config = getOnlineConfig();

        config.setSrcDB(ConfigUtil.getString("mongo.pphelper.srcDB"));
        config.setSrcGameCol(ConfigUtil.getString("mongo.srcGameCol"));

        config.setOnlinePicDir(ConfigUtil.getString("mongo.onlinePicDir"));
        config.setSrcPicDir(ConfigUtil.getString("mongo.pphelper.srcPicDir"));
        return config;
    }


    public LoaderConfig getAppStoreConfig(){
        LoaderConfig config = getOnlineConfig();

        config.setSrcDB(ConfigUtil.getString("mongo.appstore.srcDB"));
        config.setSrcGameCol(ConfigUtil.getString("mongo.srcGameCol"));

        config.setOnlinePicDir(ConfigUtil.getString("mongo.onlinePicDir"));
        config.setSrcPicDir(ConfigUtil.getString("mongo.appstore.srcPicDir"));
        return config;
    }

    public LoaderConfig getPc6Config(){
        LoaderConfig config = getOnlineConfig();

        config.setSrcDB(ConfigUtil.getString("mongo.pc6.srcDB"));
        config.setSrcGameCol(ConfigUtil.getString("mongo.srcGameCol"));

        config.setOnlinePicDir(ConfigUtil.getString("mongo.onlinePicDir"));
        config.setSrcPicDir(ConfigUtil.getString("mongo.pc6.srcPicDir"));
        return config;
    }

    public void start() throws InterruptedException {
        for (LoaderTask task : tasks) {
            task.start();

            if (Counter.getValue() > 9) {
                break;
            }
        }

        close();
    }

    private void close() {
        MongoServer.close();
    }

    public static void main(String[] args) throws InterruptedException {
        LoaderJob job = new LoaderJob();
        job.start();
    }
}
