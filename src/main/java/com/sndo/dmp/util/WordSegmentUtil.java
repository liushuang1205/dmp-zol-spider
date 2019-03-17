package com.sndo.dmp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldxPC on 2019/1/9.
 */
public class WordSegmentUtil {

    private static HttpClient httpClient = new DefaultHttpClient();
    public static List<String> getWordSegment(String word){
        try {
            if(StringUtils.isBlank(word)){
                return null;
            }
            HttpPost post = new HttpPost("http://servicenode06/wordseg/lactag");
            post.setHeader("Content-type", "application/json; charset=utf-8");
            JSONObject param = new JSONObject();
            param.put("content", word);
            StringEntity entity = new StringEntity(param.toJSONString(), Charset.forName("UTF-8"));
            entity.setContentType("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200){
                HttpEntity resultEntity = response.getEntity();
                String finalStr = EntityUtils.toString(resultEntity,"UTF-8");
                JSONObject jsonObject = JSON.parseObject(finalStr);
                boolean success = jsonObject.getBoolean("success");
                if(!success){
                    return null;
                }
                List<String> userdTags = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("tags");
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject tagObj = jsonArray.getJSONObject(i);
                    if(!"w".equals(tagObj.getString("type")) && tagObj.getString("name").length()>1){
                        userdTags.add(tagObj.getString("name"));
                    }
                }
                return userdTags;
            }else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args){

      List<String> list =  WordSegmentUtil.getWordSegment("炉石传说魔兽8.0主题卡背怎么得 炉石传说争霸");
       System.out.println("the list:"+list);

    }
}
