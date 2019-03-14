package com.sndo.dmp.ugame;

import org.jsoup.Jsoup;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author liushuang
 * @date 2019/3/13
 */
public class UgameProcessor implements PageProcessor {

    private Site site = Site.me()
            .addHeader("Origin", "https://render-ant.9game.cn")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
            .setSleepTime(1000);

    private UgameParser parser = new UgameParser();



    @Override
    public void process(Page page) {
        String data = page.getRawText();

        String name = new JsonPathSelector("$.data.game.gameName").select(page.getRawText());

        page.addTargetRequests(page.getHtml().all());
        ResultItems resultItems = getResultItems(page.getRequest().getUrl(),page.getBytes());
        if (resultItems != null) {
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                page.putField(entry.getKey(), entry.getValue());
            }
        } else {
            page.setSkip(true);
        }
    }

    private ResultItems getResultItems(String url, byte[] bytes) {
        ResultItems resultItems = new ResultItems();

        org.bson.Document pageElement = null;
        try {
            pageElement = parseGame(url, Jsoup.parse(new String(bytes, "utf-8"), url));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(pageElement != null){
            resultItems.put(UgameConstants.TARGET_PAGE_HTML_ELEMENT, pageElement);
        }

        return resultItems;
    }


    private org.bson.Document parseGame(String url, org.jsoup.nodes.Document parse) {
        return parser.parseGame(url,parse);
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
