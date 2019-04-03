package com.sndo.dmp.dome;

import com.sndo.dmp.mongo.MongoServer;
import com.sndo.dmp.util.ConfigUtil;

public class CountTest {

    public static Integer qury(Integer count){
        int num = 0;

        for(int i = 0; i < 5; i++){
            num++;
            if(num > count){
                break;
            }
        }

        return num;
    }


  /*  public static void main(String[] args){
        int count = 10;
        int num = 0;


        while(count > num){

            num = qury(count);
            System.out.println(num);
            count = count - num;
            num = 0;

        }
    }*/

    public static void main(String[] args){
        JobTask jobTask = new JobTask();
        try {
            int count = ConfigUtil.getInt("game.update.count");
            int num = 0;
            int i = 0;
            while(count > num){
                num = tasks.get(i).start(count);
                count = count - num;
                num = 0;
                i++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(jobTask.zolTask != null
                    && jobTask.pphelperTask != null
                    && jobTask.appstroeTask != null
                    && jobTask.pc6Task != null){

                MongoServer.close();
            }
        }
    }
}
