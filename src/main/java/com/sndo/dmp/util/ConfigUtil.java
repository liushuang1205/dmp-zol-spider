package com.sndo.dmp.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigUtil {

    private static Config conf = null;

    static {
        init();
    }

    public static void init(){
        if(conf == null) {
            conf = ConfigFactory.load("application.conf");
        }
    }

    public static String getString(String key){
        if(conf == null) {
            init();
        }

        return conf.getString(key);
    }

    public static int getInt(String key){
        if(conf == null) {
            init();
        }

        return conf.getInt(key);
    }

}
