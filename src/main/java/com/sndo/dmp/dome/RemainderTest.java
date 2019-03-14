package com.sndo.dmp.dome;

import java.util.ArrayList;
import java.util.List;

public class RemainderTest {

    public static void remainder(List list){
        if(list.size() % 100 == 0 && list.size() > 0){
            System.out.println("true --->" + list.size());
        }
    }

    public static void main(String[] args){
        List list = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            list.add(i);
            remainder(list);
        }
    }
}
