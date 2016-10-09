package com.github.sofn.trpc.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-10-09 23:40.
 */
public class NumUtil {
    private static final AtomicLong NUM = new AtomicLong();
    private static final AtomicInteger PORT = new AtomicInteger(10000);

    public static long nextNum() {
        return NUM.incrementAndGet();
    }

    public static int nextPort() {
        return PORT.incrementAndGet();
    }

}
