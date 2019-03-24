package com.sndo.dmp.dome;

import com.sndo.dmp.util.ReadExcelUtils;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private static String getDownloadUrl(String name) {
        try {
            List<List<String>> excelData = ReadExcelUtils.readXlsx("C:\\Users\\liushuang\\Desktop\\游戏包下载.xlsx");
            for (int i = 0; i < excelData.size(); i++) {
                List<String> model = excelData.get(i);
                if(model.get(0) != null && model.get(0).equals(name)){
                    return model.get(4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
   // http://ugame.9game.cn/game/downloadGame?pack.cooperateModelId=193941&pack.id=28598260
    public static void main(String[] args){
       /* String url = getDownloadUrl("神魔圣域");
        System.out.println(url);*/

        /*String str = "更新时间：2019-03-11 13:50:04";
        System.out.println(str.substring(5));*/

        /*List<String> url = new ArrayList<>();
        String gamePageLinksPath = "C:\\Users\\liushuang\\Desktop\\游戏投放页面链接.xlsx";

        try {
            List<List<String>> excelData = ReadExcelUtils.readXlsx(gamePageLinksPath);
            for (int i = 0; i < 1; i++) {
                List<String> model = excelData.get(i);
                if(model.get(2) != null){
                    String[] gameNum = model.get(2).substring(33).split("\\.")[0].split("-");
                    String cmId = gameNum[0];
                    String gameId = gameNum[1];

                    System.out.println(cmId + " ----" + gameId);
                    //url.add(model.get(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       /* String url = "http://ugame.9game.cn/game/downloadGame?pack.cooperateModelId=193941&pack.id=28598384&from=share";
        int index = url.lastIndexOf("&");
        System.out.println(url.substring(0,index));*/

        int i = 1;
        if(i > 1){
            System.out.println("大于"+i);
        }else{
            System.out.println(i);
        }
    }
}
