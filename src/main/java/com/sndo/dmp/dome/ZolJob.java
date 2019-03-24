package com.sndo.dmp.dome;


/**
 * @author yangqi
 * @date 2019/3/22 19:53
 **/
public class ZolJob {

    public static void main(String[] args) {
        LoaderConfig config = new LoaderConfig();
        // TODO 设置参数
        config.setOnlineDB("");

        LoaderTask task = new LoaderTask(config);
        try {
            task.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            task.close();
        }
    }
}
