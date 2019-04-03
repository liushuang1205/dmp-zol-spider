package com.sndo.dmp.job;

import com.sndo.dmp.dome.LoaderConfig;
import com.sndo.dmp.util.ConfigUtil;

public class Pc6Job {

    public static LoaderConfig getPc6Config(){
        LoaderConfig config = new LoaderConfig();
        config.setOnlineDB(ConfigUtil.getString("mongo.onlineDB"));
        config.setOnlineGameCol(ConfigUtil.getString("mongo.onlineGameCol"));
        config.setOnlineInrcCol(ConfigUtil.getString("mongo.onlineInrcCol"));

        config.setSrcDB(ConfigUtil.getString("mongo.pc6.srcDB"));
        config.setSrcGameCol(ConfigUtil.getString("mongo.srcGameCol"));

        config.setOnlinePicDir(ConfigUtil.getString("mongo.onlinePicDir"));
        config.setSrcPicDir(ConfigUtil.getString("mongo.pc6.srcPicDir"));
        return config;
    }
}
