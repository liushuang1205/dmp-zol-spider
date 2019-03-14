package com.sndo.dmp.game;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UGameDownloader {

    public byte[] download(String url) throws URISyntaxException, IOException {
        CloseableHttpClient httpCilent = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder(url);
        URI uri = builder.build();

        HttpGet httpGet = new HttpGet(uri);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Accept-Encoding", "gzip,deflate,br");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Origin", "https://render-ant.9game.cn");
        httpGet.setHeader("Host", "goldpage.9game.cn");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");

        CloseableHttpResponse execute = httpCilent.execute(httpGet);
        return EntityUtils.toByteArray(execute.getEntity());
    }
}
