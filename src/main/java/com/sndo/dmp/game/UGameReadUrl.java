package com.sndo.dmp.game;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UGameReadUrl {

    public List<String> readUrl(){
        List<String> urls = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\urls.txt")));
            String line;
            while((line = reader.readLine()) != null){
                urls.add(formatUrl(line));
            }
            return urls;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String formatUrl(String url) {
        String[] arr = url.split("-");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://goldpage.9game.cn/api/ugm/game/info?gameId=")
                .append(arr[2].split("\\.")[0])
                .append("&cmId=")
                .append(arr[1]);

        return urlBuilder.toString();
    }
}
