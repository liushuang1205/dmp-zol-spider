package com.sndo.dmp.zol;

import com.sndo.dmp.ImageDownloader;
import com.sndo.dmp.util.CharUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZolParser {

    public org.bson.Document pageGame(String url, String referer, Document doc){
        org.bson.Document resultDoc = new org.bson.Document();
        org.bson.Document android = new org.bson.Document();
        resultDoc.put("url",url);
        resultDoc.put("referer",referer);

        String gameName = parseGameName(doc);
        if(StringUtils.isNotBlank(gameName)){
            resultDoc.put("name",gameName);
        }

        String categories = parseCategory(doc);
        if(StringUtils.isNotBlank(categories)){
            //resultDoc.put("categories",categories);
        }

        String logoUrl = parseLogoUrl(doc);
        if(StringUtils.isNotBlank(logoUrl)){
            android.put("logo_url",logoUrl);
            //resultDoc.put("logo_url",logoUrl);
            String localLogoImagePath = add2ImageDownloadQueue(url,logoUrl,false);
            if(StringUtils.isNotBlank(localLogoImagePath)){
                resultDoc.put("localLogoImagePath", localLogoImagePath);
            }
        }

        String essay = parseEssay(doc);
        if (essay != null) {
            resultDoc.put("essay", essay);
        }

        String desc = parseDesc(doc);
        if (desc != null) {
            resultDoc.put("desc", desc);
        }

        String size = parseSize(doc);
        if (size != null) {
            //resultDoc.put("size", size);
            android.put("size", size);
        }

        String version = parseVersion(doc);
        if (version != null) {
            //resultDoc.put("version", version);
            android.put("version", version);
        }

        String versionDate = parseVersionDate(doc);
        if (versionDate != null) {
            //resultDoc.put("version_date", versionDate);
            android.put("version_date", versionDate);
        }

        String downloads = parseDownloads(doc);
        if (downloads != null) {
            //resultDoc.put("download_count", downloads);
            android.put("download_count", downloads);
        }

        String score = parseScore(doc);
        if (score != null) {
            resultDoc.put("web_score", score);
        }

        String require = parseRequire(doc);
        if (require != null) {
            //resultDoc.put("require", require);
            android.put("require", require);
        }

        String updateLog = parseUpdateLog(doc);
        if (updateLog != null) {
            resultDoc.put("updateLog", updateLog);
        }

        String downloadUrl = parseDownloadUrl(doc);
        if (downloadUrl != null) {
            //resultDoc.put("download_url", downloadUrl);
            android.put("download_url", downloadUrl);
        }

        boolean isFree = parseIsFree(doc);
        //resultDoc.put("is_free",isFree);
        android.put("is_free",isFree);

        String provider = parseProvider(doc);
        if(provider != null){
            resultDoc.put("provider",provider);
        }

        String language = parseLanguage(doc);
        if(language != null){
            //resultDoc.put("language",language);
        }

        List<String> screenshots = parseScreenshot(doc);
        if (screenshots != null) {
            //resultDoc.put("screenshotUrl", screenshots);
            List<String> saveFilePathList = new ArrayList<String>();
            for (String cur : screenshots) {
                String saveFilePath = add2ImageDownloadQueue(url, cur, true);
                saveFilePathList.add(saveFilePath);
            }
            //resultDoc.put("game_capture", saveFilePathList);
            android.put("game_capture", saveFilePathList);
        }

        int commentNum = parseCommentNum(doc);
        if (commentNum > 0) {
            resultDoc.put("commentNum", commentNum);
        }

        if(android != null){
            resultDoc.put("android",android);
        }

        return resultDoc;
    }

    private String parseLanguage(Document doc) {
        Elements elements = doc.select("ul.soft-text > li > span");
        if(!elements.isEmpty()) {
            for (Element element : elements) {
                if (element.text().contains("语言：")) {
                    Elements provider = doc.select("ul.soft-text > li > em[title*=简体中文]");
                    if(!provider.isEmpty()){
                        return provider.text();
                    }
                }
            }
        }
        return null;
    }

    private String parseProvider(Document doc) {
        Elements elements = doc.select("ul.soft-text > li > span");
        if(!elements.isEmpty()) {
            for (Element element : elements) {
                if (element.text().contains("厂商：")) {
                    Elements provider = doc.select("ul.soft-text > li > em[title*=腾讯]");
                    if(!provider.isEmpty()){
                        return provider.text();
                    }
                }
            }
        }
        return null;
    }

    private boolean parseIsFree(Document doc) {
        Elements elements = doc.select("ul.soft-text > li > span");
        if(!elements.isEmpty()){
            for (Element element : elements){
                if(element.text().contains("授权：")){
                    if(doc.select("ul.soft-text > li > em[title*=软件]").text().contains("免费")){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String parseEssay(Document doc) {
        return null;
    }

    private int parseCommentNum(Document doc) {
        Elements elements = doc.select("div.section.comment-section > div.discuss-tip > em:nth-child(1)");
        if (!elements.isEmpty()) {
            return Integer.valueOf(elements.text());
        } else {
            return 0;
        }
    }

    private List<String> parseScreenshot(Document doc) {
        Elements elements = doc.select("ul.screenshot-items.clearfix > li.item > a.pic > img");
        if (!elements.isEmpty()) {
            List<String> urls = new ArrayList<String>();
            for (Element element : elements) {
                String url = element.attr("src");
                urls.add(url);
            }
            return urls;
        } else {
            return null;
        }
    }

    private String parseDownloadUrl(Document doc) {
        Elements elements = doc.select("a.downLoad-button.androidDown-button");
        if (!elements.isEmpty()) {
            return elements.first().absUrl("href");
//            return elements.first().attr("href");
        } else {
            return null;
        }
    }

    //没有更新日志
    private String parseUpdateLog(Document doc) {
        return null;
    }

    //版本要求
    private String parseRequire(Document doc) {
        Elements elements = doc.select("li.item > span:nth-child(2)");
        if(!elements.isEmpty()){
            return elements.first().text();
        }else{
            return null;
        }
    }

    private String parseScore(Document doc) {
        Elements elements = doc.select("div.rate-box > em");
        if (!elements.isEmpty()) {
            return elements.text();
        } else {
            return null;
        }
    }

    private String parseDownloads(Document doc) {
        Elements elements = doc.select("ul.summary-text.clearfix li:nth-child(3) span:nth-child(2)");
        if (!elements.isEmpty()) {
            String text = elements.text();
            return text;
        }
        return null;
    }

    private String parseVersionDate(Document doc) {
        Elements elements = doc.select("ul.soft-text li:nth-child(4) > em");
        if (!elements.isEmpty()) {
            String versionDate = elements.text();
            if(versionDate.contains("简体中文")){
                return doc.select("ul.soft-text li:nth-child(5) > em").text();
            }
            return versionDate;
        } else {
            return null;
        }
    }

    private String parseVersion(Document doc) {
        Elements elements = doc.select("div.soft-detail > h3.soft-title");
        if (!elements.isEmpty()) {
            for (Element element : elements) {
                String key = element.text();
                String[] versions = key.split(" ");
                if(versions.length == 2) {
                    return versions[1];
                }
            }
        } else {
            return null;
        }
        return null;
    }

    private String parseSize(Document doc) {
        Elements elements = doc.select("ul.soft-infor > li:nth-child(1)");
        if (!elements.isEmpty()) {
            return elements.first().text().split("：")[1];
        } else {
            return null;
        }
    }

    private String parseDesc(Document doc) {
        Elements elements = doc.select("div.text > p");
        if (!elements.isEmpty()) {
            return elements.text();
        } else {
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

    private static final String baseDir = "G:\\";

    // type: false ==> zol_logo, true ==> zol_screenshot
    private String getSaveFilePath(boolean type, String url) {
        StringBuilder saveFilePathBuilder = new StringBuilder(baseDir);
        if (type == false) {
            saveFilePathBuilder.append("logo/");
        } else {
            saveFilePathBuilder.append("screenshot/");
        }

        saveFilePathBuilder.append(DigestUtils.md5Hex(url)).append("/");
        File dirFile = new File(saveFilePathBuilder.toString());
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }

        saveFilePathBuilder.append(UUID.randomUUID().toString()).append(".jpg");
        return saveFilePathBuilder.toString();
    }

    private String parseLogoUrl(Document doc) {
        Elements elements = doc.select("div.soft-div > span.pic > img");
        if (!elements.isEmpty()) {
            return elements.first().attr("src");
        } else {
            return null;
        }
    }


    private String parseCategory(Document doc) {
        Elements categoryElements = doc.select("ul.soft-text > li > a");
        if(categoryElements.isEmpty()){
            return null;
        }

        return categoryElements.text();
    }

    private String parseGameName(Document doc) {
        Elements nameElements = doc.select("h1.soft-title");
        if(nameElements.isEmpty()){
            return null;
        }

        String name = nameElements.text();
        return CharUtil.fullToHalf(name);
    }
}
