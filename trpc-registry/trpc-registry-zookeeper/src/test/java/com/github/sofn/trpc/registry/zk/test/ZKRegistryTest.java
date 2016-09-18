package com.github.sofn.trpc.registry.zk.test;

import com.github.sofn.trpc.registry.zk.ZKRegistry;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 23:14.
 */
public class ZKRegistryTest {

    @Test
    public void test() throws UnknownHostException, InterruptedException {
        ZKRegistry registry = new ZKRegistry();
        registry.setConnectString("localhost:2181");
        registry.setSessionTimeout(100);
        registry.setConnectionTimeout(1000);

        registry.initConnect();

        registry.registry("testkey", InetAddress.getLocalHost(), 8080);
        TimeUnit.SECONDS.sleep(20);

    }

}
