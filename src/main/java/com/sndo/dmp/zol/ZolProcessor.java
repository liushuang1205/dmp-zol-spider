package com.sndo.dmp.zol;

import com.sndo.dmp.util.Zlib;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @date 2019/3/7
 */
public class ZolProcessor implements PageProcessor {

    private ZolParser pageParser = new ZolParser();

    private Site site = Site.me().setSleepTime(1000);

    @Override
    public void process(Page page) {
        int pageIndex = (Integer) page.getRequest().getExtra(ZolConstants.PAGE_INDEX);

        List<Request> requests = new ArrayList<>();
        Document document = page.getHtml().getDocument();
        if(pageIndex < 2){
            if(pageIndex == 0){
                List<String> targetUrls = extractFirstPageTargetUrl(document, page.getRequest().getUrl());
                addUrlToRequest(page, pageIndex, requests, targetUrls);
            }else if(pageIndex == 1){
                List<String> targetUrls = extractSecondPageTargetUrl(document);
                addUrlToRequest(page, pageIndex, requests, targetUrls);
            }
        }

        if (CollectionUtils.isNotEmpty(requests)) {
            for (Request request : requests) {
                page.addTargetRequest(request);
            }
        }

        String referer = (String) page.getRequest().getExtra(ZolConstants.PAGE_REFERER);
       // List<String> gameUrls = (List<String>) page.getRequest().getExtra("gameUrls");
        ResultItems resultItems = getResultItems(pageIndex, page.getRequest().getUrl(), referer, page.getBytes());

        if (resultItems != null) {
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                page.putField(entry.getKey(), entry.getValue());
            }
        } else {
            page.setSkip(true);
        }
    }

    private ResultItems getResultItems(int pageIndex, String url, String referer, byte[] bytes) {
        ResultItems resultItems = new ResultItems();

        if(pageIndex == 2){
            org.bson.Document pageElement = null;
            try {
                pageElement = parseGame(url, referer, Jsoup.parse(new String(bytes, "gbk"), url));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (pageElement != null) {
                resultItems.put(ZolConstants.TARGET_PAGE_HTML_ELEMENT, pageElement);
            }

            org.bson.Document htmlContent = toTargetHtmlDoc(pageIndex, url, referer, bytes);
            if (htmlContent != null) {
                resultItems.put(ZolConstants.TARGET_PAGE_HTML_CONTENT, htmlContent);
            }

        }
        return resultItems;
    }

    private org.bson.Document toTargetHtmlDoc(int pageIndex, String url, String referer, byte[] bytes) {
        org.bson.Document targetDoc = new org.bson.Document();
        targetDoc.put("url", url);
        targetDoc.put("referer", referer);
        targetDoc.put("index", pageIndex);
        targetDoc.put("zlib", Zlib.compress(bytes));

        return targetDoc;
    }

    private org.bson.Document parseGame(String url, String referer, Document parse) {
        return pageParser.pageGame(url,referer,parse);
    }

    private void addUrlToRequest(Page page, int pageIndex, List<Request> requests, List<String> targetUrls) {
        if (CollectionUtils.isNotEmpty(targetUrls)) {
            for (String targetUrl : targetUrls) {
                Request targetRequest = new Request();
                targetRequest.setUrl(targetUrl);
                targetRequest.putExtra(ZolConstants.PAGE_REFERER, page.getRequest().getUrl());
                targetRequest.putExtra(ZolConstants.PAGE_INDEX, pageIndex + 1);
                requests.add(targetRequest);
            }
        }
    }

    private List<String> extractFirstPageTargetUrl(Document document, String url) {
        Elements elements = document.select("div.page > a:nth-child(2)");
        int maxPage = Integer.valueOf(elements.last().text());
        List<String> targetUrls = new ArrayList<String>();
        targetUrls.add(url);
        for(int i = 2; i < maxPage + 1; i++){
            StringBuilder builder = new StringBuilder();
            builder.append(url).append("page_").append(i).append(".html");
            targetUrls.add(builder.toString());
        }
        return targetUrls;
    }

    public List<String>  extractSecondPageTargetUrl(Document document){
        Elements elements = document.select("ul.soft-list.clearfix > li > a");
        List<String> urls = new ArrayList<>();
        if(!elements.isEmpty()){
            for(Element elemenet : elements){
                String url = elemenet.absUrl("href");
                if(StringUtils.isNotBlank(url)){
                    urls.add(url);
                }
            }
        }
        return urls;
    }


    @Override
    public Site getSite() {
        return this.site;
    }
}
