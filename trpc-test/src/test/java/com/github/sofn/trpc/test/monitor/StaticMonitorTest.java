package com.github.sofn.trpc.test.monitor;

import com.github.sofn.trpc.client.TrpcClientProxy;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.server.ThriftServerPublisher;
import com.github.sofn.trpc.server.config.ServerArgs;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.*;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-10-09 22:46.
 */
public class StaticMonitorTest {
    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;
    private static final String appKey = "statictest";
    private static final String localAppKey = "clientkey";

    private GenericKeyedObjectPoolConfig poolConfig;

    @Before
    public void init() throws InterruptedException {
        ServiceFactoryTest serviceFactoryTest = new ServiceFactoryTest();
        ServerArgs serverArgs = serviceFactoryTest.getServerArgs(appKey, "127.0.0.1", 8011);

        ThriftServerPublisher publisher = new ThriftServerPublisher(serverArgs);
        Thread thread = new Thread(publisher::init);
        thread.setDaemon(true);
        thread.start();
        TimeUnit.MILLISECONDS.sleep(400);

        poolConfig = new GenericKeyedObjectPoolConfig();
        poolConfig.setMaxTotal(MAX_CONN);
        poolConfig.setMaxTotalPerKey(MAX_CONN);
        poolConfig.setMaxIdlePerKey(MAX_CONN);
        poolConfig.setMinIdlePerKey(MIN_CONN);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        poolConfig.setSoftMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        poolConfig.setJmxEnabled(false);
    }

    @Test
    public void testStaticMonitor() {
        ClientArgs args = ClientArgs.builder()
                .poolConfig(poolConfig)
                .localAppKey(localAppKey)
                .remoteAppKey(appKey)
                .ipPorts("127.0.0.1:8011")
                .serviceInterface(ClassNameUtils.getClassName(Hello.class))
                .timeout(100)
                .build();

        TrpcClientProxy proxy = new TrpcClientProxy();
        proxy.setClientArgs(args);
        Hello.Client client = proxy.client();
        try {
            String response = client.hi("world");
            System.out.println("response: " + response);
        } catch (TException e) {
            fail();
        }
    }

}
