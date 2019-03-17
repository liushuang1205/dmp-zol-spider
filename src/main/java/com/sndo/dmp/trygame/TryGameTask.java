package com.sndo.dmp.trygame;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.sndo.dmp.mongo.MongoServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liushuang
 * @date 2019/3/16
 */
public class TryGameTask {

	private MongoCollection<Document> collection;

	public TryGameTask(){
		collection = MongoServer.getCollection("try_game", "game");
	}

	private List<String> readUrl() throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream("D:\\urls.txt")));

		List<String> urls = new ArrayList<>();
		String line ;
		while((line = reader.readLine()) != null){
			urls.add(format(line));
		}
		reader.close();
		return urls;
	}
	/*http://ugame.9game.cn/game/share-193941-574245.html
	https://goldpage.9game.cn/api/ugm/game/info?gameId=574245&cmId=193941*/
	private String format(String url){
		String[] arr = url.split("-");
		StringBuilder builder = new StringBuilder("https://goldpage.9game.cn/api/ugm/game/info?gameId=")
				.append(arr[2].split("\\.")[0])
				.append("&cmId=")
				.append(arr[1]);
		return builder.toString();
	}
	private byte[] download(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		httpGet.setHeader("Accept", "application/json");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpGet.setHeader("Accept-Encoding", "gzip,deflate,br");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Origin", "https://render-ant.9game.cn");
		httpGet.setHeader("Host", "goldpage.9game.cn");
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");

		CloseableHttpResponse response = httpClient.execute(httpGet);
		return EntityUtils.toByteArray(response.getEntity());
	}


	private Document parse(byte[] bytes , String url) throws UnsupportedEncodingException {
		String content = new String(bytes, "utf-8");
		JSONObject jsonObject = JSONObject.parseObject(content);

		Document doc = new Document();

		String name = parseName(jsonObject);
		if(StringUtils.isNotBlank(name)){
			doc.put("name", name);
		}

		List<String> images = parseImages(jsonObject);
		if(images != null) {
			doc.put("images", images);
		}
		System.out.println(name);
		return doc;
	}

	private List<String> parseImages(JSONObject jsonObject) {
		List<String> images = new ArrayList<>();
		JSONArray array = jsonObject.getJSONObject("data").getJSONArray("images");
		for (int i = 0; i < array.size(); i++){
			images.add(array.getJSONObject(i).getString("url"));
		}
		return images;
	}

	private String parseName(JSONObject jsonObject) {
		String name = jsonObject.getJSONObject("data").getJSONObject("game").getString("gameName");
		return name;
	}

	private void save(Document document){
		collection.insertOne(document);
	}

	private void close(){
		MongoServer.close();
	}

	public static void main(String[] args) throws IOException {
		TryGameTask task = new TryGameTask();
		List<String> urls = task.readUrl();
		for(String url : urls){
			Document document = task.parse(task.download(url),url);
			task.save(document);
			System.out.println(document.toString());
		}
		task.close();
	}
}
