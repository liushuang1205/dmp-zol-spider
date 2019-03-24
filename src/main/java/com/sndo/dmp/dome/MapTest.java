package com.sndo.dmp.dome;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    private static Map<String,Boolean> have = new HashMap<>();

    public static void main(String[] args){
        //have.put("张三", true);

        String name = "张三";
        System.out.println(have.get(name));

        if(have.get(name) == null) {
            have.put(name, true);
        }

        if(have.get(name) == null) {
            have.put(name, true);
        }

    }


}
