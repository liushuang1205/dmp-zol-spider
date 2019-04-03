package com.sndo.dmp.job;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

    private static AtomicInteger total = new AtomicInteger(0);

    public static void increment() {
        total.incrementAndGet();
    }

    public static int getValue() {
        return total.get();
    }

}
