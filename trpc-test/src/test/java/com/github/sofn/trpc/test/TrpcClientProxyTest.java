package com.github.sofn.trpc.test;

import com.github.sofn.trpc.client.TrpcClientProxy;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.registry.zk.ZkMonitor;
import com.github.sofn.trpc.registry.zk.ZkRegistry;
import com.github.sofn.trpc.server.ThriftServerPublisher;
import com.github.sofn.trpc.server.config.ServerArgs;
import com.github.sofn.trpc.test.monitor.ServiceFactoryTest;
import com.github.sofn.trpc.utils.NumUtil;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.*;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-10-03 22:59
 */
@Slf4j
public class TrpcClientProxyTest {
    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;
    private static final String localAppKey = "clientkey";
    private String appKey;

    private GenericKeyedObjectPoolConfig poolConfig;

    @Before
    public void init() throws InterruptedException {
        this.appKey = "proxytest" + NumUtil.nextNum();
        ServiceFactoryTest serviceFactoryTest = new ServiceFactoryTest();
        ServerArgs serverArgs = serviceFactoryTest.getServerArgs(this.appKey, "127.0.0.1", NumUtil.nextPort());
        ZkRegistry zkRegistry = serviceFactoryTest.getZkRegistry();
        serverArgs.setRegistrys(ImmutableList.of(zkRegistry));

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
    public void testBlock() {
        ZkMonitor zkMonitor = new ZkMonitor();
        zkMonitor.setConnectString("localhost:2181");
        ClientArgs args = ClientArgs.builder()
                .poolConfig(poolConfig)
                .localAppKey(localAppKey)
                .remoteAppKey(appKey)
                .monitors(ImmutableList.of(zkMonitor))
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
            log.error("call error", e);
        }
    }


    @Test
    public void testAysnc() throws InterruptedException {
        ZkMonitor zkMonitor = new ZkMonitor();
        zkMonitor.setConnectString("localhost:2181");
        ClientArgs args = ClientArgs.builder()
                .poolConfig(poolConfig)
                .localAppKey(localAppKey)
                .remoteAppKey(appKey)
                .monitors(ImmutableList.of(zkMonitor))
                .serviceInterface(ClassNameUtils.getClassName(Hello.class))
                .timeout(100)
                .async(true)
                .build();

        TrpcClientProxy proxy = new TrpcClientProxy();
        proxy.setClientArgs(args);
        Hello.AsyncClient client = proxy.client();
        CountDownLatch latch = new CountDownLatch(3);
        try {
            client.hi("world", new ProxyTestCallBack(latch));
            client.hi("world2", new ProxyTestCallBack(latch));
            client.hi("world3", new ProxyTestCallBack(latch));
        } catch (TException e) {
            fail();
        }
        latch.await();
    }

    private class ProxyTestCallBack implements AsyncMethodCallback<Hello.AsyncClient.hi_call> {
        private CountDownLatch latch;

        ProxyTestCallBack(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onComplete(Hello.AsyncClient.hi_call hi) {
            try {
                System.out.println("async response: " + hi.getResult());
                latch.countDown();
            } catch (TException e) {
                fail();
            }
        }

        @Override
        public void onError(Exception e) {
            fail();
        }
    }
}
