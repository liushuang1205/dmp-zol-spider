package com.sndo.dmp.dome;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Random;

public class ContanisTest {

    private static int getCategoryId(String value) {
        if ("休闲益智".contains(value)) {
            return 1;
        } else if ("扑克棋牌".contains(value)) {
            return 2;
        } else if ("飞行射击".contains(value)) {
            return 3;
        } else if ("网络游戏".contains(value)) {
            return 4;
        } else {
            return -1;
        }
    }

    private static int getId(String value) {
        if (value.contains("斗地主")) {
            return 1;
        } else if (value.contains("麻将")) {
            return 2;
        } else {
            return -1;
        }
    }

    private static double getWebScore() {
        double y= 10.0;
        double x = 9.0;
        double d = (y - Math.random()) % y;
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
        return d;
    }

    private static void getNum(){
        double min = 9.0;
        double max = 10.0;
        double d = min + new Random().nextDouble() * (max - min);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
        //return d;
        System.out.println(d);
    }

    private static int getHotScore() {
        int min = 90;
        int max = 100;
        return min + ((int) (new Random().nextFloat() * (max - min)));
    }
    public static void main(String[] args){
       /* int i = getCategoryId("飞行射击");
        int j = getId("贵阳捉鸡麻将");
        System.out.println(i);
        System.out.println(j);*/
       for(int i = 0 ; i< 100 ; i++) {
           System.out.println(getHotScore());
           //getNum();
       }

    }
}
