package com.sndo.dmp.ugame;

import com.mongodb.client.MongoCollection;
import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.mongo.MongoServer;
import com.sndo.dmp.util.ReadExcelUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class UgameParser {

    private static final String baseDir = "G:\\";

    private static final String downloadGamePackPath = "C:\\Users\\liushuang\\Desktop\\游戏包下载.xlsx";
    private static final String gamePageLinksPath = "C:\\Users\\liushuang\\Desktop\\游戏投放页面链接.xlsx";

    public Document parseGame(String url, org.jsoup.nodes.Document doc) {
        Document resultDoc = new Document();
        Document android = new Document();

        Integer id = getId();
        if(id > 0){
            resultDoc.put(UgameField.ID.getValue(),id);
        }

        String name = parseName(doc);
        if(StringUtils.isNotBlank(name)){
            resultDoc.put(UgameField.NAME.getValue(),name);
        }

        List<Integer> category = parseCategory(doc);
        if(category != null && category.size() > 0){
            resultDoc.put(UgameField.CATEGORY_ID.getValue(),category);
        }

        String desc = parseDesc(doc);
        if(StringUtils.isNotBlank(desc)){
            resultDoc.put(UgameField.DESC.getValue(),desc);
        }

        double webScore = getWebScore();
        resultDoc.put(UgameField.WEB_SCORE.getValue(), webScore);

        int hotScore = getHotScore();
        resultDoc.put(UgameField.HOT_SCORE.getValue(), hotScore);

        String logoUrl = parseLogoUrl(doc);
        if(StringUtils.isNotBlank(logoUrl)){
            String saveFilePath = add2ImageDownloadQueue(url, logoUrl, false);
            android.put(UgameField.GAME_ANDROID_LOGO_URL.getValue(), saveFilePath);
        }

        String size = parseSize(doc);
        if(StringUtils.isNotBlank(size)){
            android.put(UgameField.GAME_ANDROID_SIZE.getValue(), size);
        }

        String downloadUrl = getData2Excel(name,0,4,downloadGamePackPath);
        if(StringUtils.isNotBlank(downloadUrl)){
            android.put(UgameField.GAME_ANDROID_DOWNLOAD_URL.getValue(), downloadUrl);
        }

        android.put(UgameField.GAME_ANDROID_DOWNLOAD_COUNT.getValue(), 0);//没有下载次数,默认给个0

        android.put(UgameField.GAME_ANDROID_IS_FREE.getValue(), true); //没有数据，默认为true

        String version = getData2Excel(name,0,1,downloadGamePackPath);
        if(StringUtils.isNotBlank(version)){
            android.put(UgameField.GAME_ANDROID_VERSION.getValue(), version);
        }

        String versionDate = parseVersionDate(doc);
        if(StringUtils.isNotBlank(versionDate)){
            android.put(UgameField.GAME_ANDROID_VERSION_DATE.getValue(), versionDate);
        }

        List<String> gameCapturelist = parseGameCapture(doc);
        if(gameCapturelist != null){
            List<String> saveFilePathList = new ArrayList<>();
            List<String> imageHorizontalVertical = new ArrayList<>();
            for(String gameCapture : gameCapturelist){
                String saveFilePath = add2ImageDownloadQueue(url, gameCapture, true);
                saveFilePathList.add(saveFilePath);

                String ihv = getVerticalImage(saveFilePath);
                imageHorizontalVertical.add(ihv);
            }
            android.put(UgameField.GAME_ANDROID_GAME_CAPTURE.getValue(), saveFilePathList);
            android.put(UgameField.GAME_ANDROID_GAME_CAPTURE_FLAG.getValue(), imageHorizontalVertical);
        }

        String require = parseRequire(doc);
        if(StringUtils.isNotBlank(require)){
            android.put(UgameField.GAME_ANDROID_REQUIRE.getValue(),require);
        }

        String access = parseAccess(doc);
        if(StringUtils.isNotBlank(access)){
            android.put(UgameField.GAME_ANDROID_ACCESS.getValue(), access);
        }

        resultDoc.put(UgameField.ANDROID.getValue(), android);

        String provider = parseProvider(doc);
        if(StringUtils.isNotBlank(provider)){
            resultDoc.put(UgameField.PROVIDER.getValue(), provider);
        }

        String upTime = parseVersionDate(doc);
        if(StringUtils.isNotBlank(upTime)){
            resultDoc.put(UgameField.UPTIME.getValue(), upTime);
        }

        resultDoc.put(UgameField.UPDATE_TIME.getValue(), new Date());

        resultDoc.put(UgameField.FILTER_FLAG.getValue(), 0); //状态标识 0 正常 1 暂停 默认正常

        resultDoc.put(UgameField.IS_VALID.getValue(), 1); //android游戏apk是否可以正常下载 0 不可下载 1 可下载 默认为 1

        resultDoc.put(UgameField.IS_AD.getValue(), 2); //游戏广告类型 0 不是广告 1 后台上传的广告 2 第三方广告
        return resultDoc;
    }

    private String parseProvider(org.jsoup.nodes.Document doc) {
        return null;
    }


    //权限声明（没有数据）
    private String parseAccess(org.jsoup.nodes.Document doc) {
        return null;
    }

    //系统要求（没有数据）
    private String parseRequire(org.jsoup.nodes.Document doc) {
        return null;
    }

    private String getVerticalImage(String saveFilePath) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(saveFilePath));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            if (height > width) {
                // 竖版
                return "h";
            } else {
                // 横版
                return "w";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> parseGameCapture(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("ul.game__fivepic__list > li > img");
        if(!elements.isEmpty()){
            List<String> urls = new ArrayList<String>();
            for (Element element : elements) {
                String url = element.attr("src");
                urls.add(url);
            }
            return urls;
        }else{
            return null;
        }
    }

    private String parseVersionDate(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("p.game__updatetime");
        if(!elements.isEmpty()){
            return elements.text().substring(5);
        }else{
            return null;
        }
    }


    private String getData2Excel(String name,Integer nameIndex,Integer index,String excelPath) {
        try {
            List<List<String>> excelData = ReadExcelUtils.readXlsx(excelPath);
            for (int i = 0; i < excelData.size(); i++) {
                List<String> model = excelData.get(i);
                if(model.get(nameIndex) != null && model.get(nameIndex).equals(name)){
                    return model.get(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseSize(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("span#apk-size");
        if(!elements.isEmpty()){
            return elements.text();
        }else{
            return null;
        }
    }

    private String add2ImageDownloadQueue(String websiteUrl, String imageUrl, boolean type) {
        String saveFilePath = getSaveFilePath(type, websiteUrl);
        try {
            ImageDownloader.add(imageUrl, saveFilePath);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return saveFilePath.replace(baseDir, "");
    }

    // type: false ==> logo, true ==> screenshot
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

    private String parseLogoUrl(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("div.game__icon");
        if(!elements.isEmpty()){
            return elements.attr("src");
        }else {
            return null;
        }
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

    private String parseDesc(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("p.desc.j-desc-content.ellipsis");
        if(!elements.isEmpty()){
            return elements.text();
        } else {
            return null;
        }
    }

    private String parseName(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("p.game__name");
        if(!elements.isEmpty()){
            return elements.text();
        }else {
            return null;
        }
    }

    private Integer getId() {
        MongoCollection<Document> collection = MongoServer.getCollection("game", "inrc");
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

    private List<Integer> parseCategory(org.jsoup.nodes.Document doc) {
        Elements elements = doc.select("ul.game__tags > li:nth-child(1)");
        if(!elements.isEmpty()){
            List<Integer> categories = new ArrayList<>();
            String category = elements.text().trim();
            int categoryId = getCategoryId(category);
            categories.add(categoryId);

            String name = parseName(doc);
            if(StringUtils.isNotBlank(name)){
                categoryId = getCategoryIdByName(name);
                categories.add(categoryId);
            }
            return categories;
        }else {
            return null;
        }
    }

    private int getCategoryIdByName(String name) {
        if (name.contains("休闲益智")) {
            return 1;
        } else if (name.contains("扑克棋牌")) {
            return 2;
        } else if (name.contains("飞行射击")) {
            return 3;
        } else if (name.contains("网络游戏")) {
            return 4;
        } else if (name.contains("跑酷竞速")) {
            return 5;
        } else if (name.contains("动作冒险")) {
            return 6;
        } else if (name.contains("经营策略")) {
            return 7;
        } else if (name.contains("体育竞技")) {
            return 8;
        } else if (name.contains("角色扮演")) {
            return 9;
        } else if (name.contains("辅助工具")) {
            return 10;
        } else if (name.contains("斗地主")) {
            return 11;
        } else if (name.contains("麻将")) {
            return 12;
        } else if (name.contains("捕鱼")) {
            return 13;
        } else if (name.contains("炸金花")) {
            return 14;
        } else if (name.contains("电玩城")) {
            return 15;
        } else if (name.contains("娱乐城")) {
            return 16;
        } else if (name.contains("牛牛")) {
            return 17;
        } else if (name.contains("百家乐")) {
            return 18;
        } else if (name.contains("水果机")) {
            return 19;
        } else if (name.contains("街机")) {
            return 20;
        } else {
            return -1;
        }
    }

    private int getCategoryId(String value) {
        if ("休闲益智".contains(value)) {
            return 1;
        } else if ("扑克棋牌".contains(value)) {
            return 2;
        } else if ("飞行射击".contains(value)) {
            return 3;
        } else if ("网络游戏".contains(value)) {
            return 4;
        } else if ("跑酷竞速".contains(value)) {
            return 5;
        } else if ("动作冒险".contains(value)) {
            return 6;
        } else if ("经营策略".contains(value)) {
            return 7;
        } else if ("体育竞技".contains(value)) {
            return 8;
        } else if ("角色扮演".contains(value)) {
            return 9;
        } else if ("辅助工具".contains(value)) {
            return 10;
        } else if ("斗地主".contains(value)) {
            return 11;
        } else if ("麻将".contains(value)) {
            return 12;
        } else if ("捕鱼".contains(value)) {
            return 13;
        } else if ("炸金花".contains(value)) {
            return 14;
        } else if ("电玩城".contains(value)) {
            return 15;
        } else if ("娱乐城".contains(value)) {
            return 16;
        } else if ("牛牛".contains(value)) {
            return 17;
        } else if ("百家乐".contains(value)) {
            return 18;
        } else if ("水果机".contains(value)) {
            return 19;
        } else if ("街机".contains(value)) {
            return 20;
        } else {
            return -1;
        }

    }

    public List<String> getUrl() {
        List<String> url = new ArrayList<>();
        try {
            List<List<String>> excelData = ReadExcelUtils.readXlsx(gamePageLinksPath);
            for (int i = 0; i < 1; i++) {
                List<String> model = excelData.get(i);
                if(model.get(2) != null){
                    StringBuilder builder = new StringBuilder();
                    String[] gameNum = model.get(2).substring(33).split("\\.")[0].split("-");
                    String cmId = gameNum[0];
                    String gameId = gameNum[1];
                    builder.append("https://goldpage.9game.cn/api/ugm/game/info?gameId=")
                            .append(gameId)
                            .append("&cmId=")
                            .append(cmId);
                    url.add(builder.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
