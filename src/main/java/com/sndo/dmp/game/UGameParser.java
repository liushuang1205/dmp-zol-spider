package com.sndo.dmp.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.sndo.dmp.GameField;
import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.mongo.MongoServer;
import com.sndo.dmp.util.WordSegmentUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UGameParser {

    private List<String> imageHorizontalVertical = null;

    public Document parse(String url, byte[] bytes) throws UnsupportedEncodingException {
        String content = new String(bytes, "UTF-8");
        JSONObject jsonObject = JSON.parseObject(content);

        Document doc = new Document();
        Document android = new Document();

        Integer id = getId();
        doc.put(GameField.ID.getValue(),id);

        String name = parseGameName(jsonObject);
        doc.put(GameField.NAME.getValue(), name);

        List<String> wordSegment = WordSegmentUtil.getWordSegment(name);
        doc.put(GameField.WORDSEGMENT.getValue(), wordSegment);

        List<Integer> category = parseCategory(jsonObject);
        doc.put(GameField.CATEGORY_ID.getValue(), category);

        String desc = parseDesc(jsonObject);
        doc.put(GameField.DESC.getValue(), desc);

        double webScore = getWebScore();
        doc.put(GameField.WEB_SCORE.getValue(), webScore);

        int hotScore = getHotScore();
        doc.put(GameField.HOT_SCORE.getValue(), hotScore);

        String rawLogoUrl = parseLogoUrl(jsonObject);
        System.out.println(rawLogoUrl);

        String logoUrl = add2ImageDownloadQueue(url, rawLogoUrl,false);
        android.put(GameField.GAME_ANDROID_LOGO_URL.getValue(), logoUrl);

        String size = parseSize(jsonObject);
        android.put(GameField.GAME_ANDROID_SIZE.getValue(), size);

        String downloadUrl = parseDownloadUrl(jsonObject);
        android.put(GameField.GAME_ANDROID_DOWNLOAD_URL.getValue(), downloadUrl);

        android.put(GameField.GAME_ANDROID_DOWNLOAD_COUNT.getValue(), 0);//没有下载次数,默认给个0

        android.put(GameField.GAME_ANDROID_IS_FREE.getValue(), true); //没有数据，默认为true

        String version = parseVersion(jsonObject);
        android.put(GameField.GAME_ANDROID_VERSION.getValue(), version);

        Date versionDate = null;
        try {
            versionDate = parseVersionDate(jsonObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        android.put(GameField.GAME_ANDROID_VERSION_DATE.getValue(), versionDate);

        List<String> gameCapturelist = parseGameCapture(url,jsonObject);
        android.put(GameField.GAME_ANDROID_GAME_CAPTURE.getValue(), gameCapturelist);

        if(imageHorizontalVertical != null){
            android.put(GameField.GAME_ANDROID_GAME_CAPTURE_FLAG.getValue(), imageHorizontalVertical);
        }

        //String require = parseRequire(jsonObject);

        doc.put(GameField.ANDROID.getValue(), android);

        //游戏更新时间
        doc.put(GameField.UPTIME.getValue(), versionDate);

        doc.put(GameField.UPDATE_TIME.getValue(), new Date());

        doc.put(GameField.FILTER_FLAG.getValue(), 0); //状态标识 0 正常 1 暂停 默认正常

        doc.put(GameField.IS_VALID.getValue(), 1); //android游戏apk是否可以正常下载 0 不可下载 1 可下载 默认为 1

        doc.put(GameField.IS_AD.getValue(), 2); //游戏广告类型 0 不是广告 1 后台上传的广告 2 第三方广告

        return doc;
    }


    private static final String baseDir = "G:\\";

    private String add2ImageDownloadQueue(String websiteUrl, String imageUrl, boolean type) {
        String saveFilePath = getSaveFilePath(type, websiteUrl);
        try {
            ImageDownloader.add(imageUrl, saveFilePath);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return saveFilePath.replace(baseDir, "");
    }

    private String getSaveFilePath(boolean type, String url) {
        StringBuilder saveFilePathBuilder = new StringBuilder(baseDir);
        if (type == false) {
            saveFilePathBuilder.append("logo/");
        } else {
            saveFilePathBuilder.append("screenshot/");
        }

        saveFilePathBuilder.append(DigestUtils.md5Hex(url)).append("/");
        File dirFile = new File(saveFilePathBuilder.toString());
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        saveFilePathBuilder.append(UUID.randomUUID().toString())
                .append(".jpg");

        return saveFilePathBuilder.toString();
    }

    private Integer getId() {
        MongoCollection<Document> collection = MongoServer.getCollection("game_app", "inrc");
        Document filter = new Document();
        filter.put("id","inrcid");

        Document update = new Document();
        update.put("$inc", new Document("game_value", 1));
        Document result = collection.findOneAndUpdate(filter,update);
        if(!result.isEmpty()){
            return (Integer)result.get("game_value");
            // return Long.valueOf(result.getString("game_value"));
        }
        return 0;
    }

    String[] categories = {"休闲益智","扑克棋牌","飞行射击","网络游戏","跑酷竞速","动作冒险","经营策略","体育竞技",
            "角色扮演","辅助工具","斗地主","麻将","捕鱼","炸金花","电玩城","娱乐城","牛牛","百家乐","水果机","街机"};

    private int getCategoryIdByName(String name) {
        for(int i = 0; i < categories.length; i++){
            if(name.contains(categories[i])){
                return i + 1;
            }
        }
        return -1;
    }

    private int getCategoryId(String value) {
        for(int i = 0; i < categories.length; i++){
            if(categories[i].contains(value)){
                return i + 1;
            }else if("卡牌".equals(value)){
                return 2;
            }else if("回合".equals(value)){
                return 4;
            }else if("模拟".equals(value)){
                return 9;
            }else if("音乐".equals(value) || "即时".equals(value)){
                return 4;
            }
        }
        return -1;
    }

    private String parseGameName(JSONObject jsonObject) {
        return jsonObject.getJSONObject("data").getJSONObject("game").getString("gameName");
    }

    private List<Integer> parseCategory(JSONObject jsonObject) {
        String category = jsonObject.getJSONObject("data").getJSONObject("game").getString("categoryName");
        List<Integer> categories = new ArrayList<>();
        int categoryId = getCategoryId(category);
        if(categoryId != -1){
            categories.add(categoryId);
        }

        String name = parseGameName(jsonObject);
        if(StringUtils.isNotBlank(name)){
            categoryId = getCategoryIdByName(name);
            if(categoryId != -1){
                categories.add(categoryId);
            }
        }
        return categories;
    }

    private String parseDesc(JSONObject jsonObject) {
        return jsonObject.getJSONObject("data").getJSONObject("game").getString("description");
    }

    private int getHotScore() {
        int min = 90;
        int max = 100;
        return min + ((int) (new Random().nextFloat() * (max - min)));
    }

    private double getWebScore() {
        double min = 9.0;
        double max = 10.0;
        double d = min + new Random().nextDouble() * (max - min);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
        return d;
    }

    private String parseLogoUrl(JSONObject jsonObject) {
        return jsonObject.getJSONObject("data").getJSONObject("game").getString("iconUrl").replace(";,,WEBP;", "");
    }

    private String parseSize(JSONObject jsonObject) {
        return jsonObject.getJSONObject("data").getJSONObject("download").getString("fileSize");
    }

    private String parseDownloadUrl(JSONObject jsonObject) {
        String url = jsonObject.getJSONObject("data").getJSONObject("download").getString("downloadUrl");
        int index = url.lastIndexOf("&");
        return url.substring(0,index);
    }

    private String parseVersion(JSONObject jsonObject) {
        return jsonObject.getJSONObject("data").getJSONObject("game").getString("version");
    }

    private Date parseVersionDate(JSONObject jsonObject) throws ParseException {
        String date = jsonObject.getJSONObject("data").getJSONObject("game").getString("updateTime");
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sim.parse(date);
    }

    private String getVerticalImage(Integer width, Integer height) {
        if (height > width) {
            return "h";// 竖版
        } else {
            return "w"; // 横版
        }
    }

    private List<String> parseGameCapture(String url,JSONObject jsonObject) {
        List<String> saveFilePathList = new ArrayList<>();
        JSONArray array = jsonObject.getJSONObject("data").getJSONArray("images");
        imageHorizontalVertical = new ArrayList<>();
        for (int i = 0; i < array.size(); i++){
            String imgUrl = array.getJSONObject(i).getString("url");
            Integer width = Integer.valueOf(array.getJSONObject(i).getString("width"));
            Integer height = Integer.valueOf(array.getJSONObject(i).getString("height"));

            String saveFilePath = add2ImageDownloadQueue(url, imgUrl, true);
            saveFilePathList.add(saveFilePath);

            String horizontalVertical = getVerticalImage(width,height);
            imageHorizontalVertical.add(horizontalVertical);
        }
        return saveFilePathList;
    }

    //系统要求（没有数据）
    private String parseRequire(JSONObject jsonObject) {
        return null;
    }
}
