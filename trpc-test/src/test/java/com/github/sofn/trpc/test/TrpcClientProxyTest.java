package com.github.sofn.trpc.test;

import com.github.sofn.trpc.client.TrpcClientProxy;
import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.pool.impl.AsyncTrpcClientPoolImpl;
import com.github.sofn.trpc.client.pool.impl.BlockTrpcClientPoolImpl;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.registry.zk.ZkMonitor;
import com.github.sofn.trpc.registry.zk.ZkRegistry;
import com.github.sofn.trpc.server.ThriftServerPublisher;
import com.github.sofn.trpc.server.config.ServerArgs;
import com.github.sofn.trpc.test.monitor.ServiceFactoryTest;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.fail;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-10-03 22:59
 */
@Slf4j
public class TrpcClientProxyTest {
    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;
    private static final String appKey = "proxytest";
    private static final String localAppKey = "clientkey";

    @Before
    public void init() throws InterruptedException {
        ServiceFactoryTest serviceFactoryTest = new ServiceFactoryTest();
        ServerArgs serverArgs = serviceFactoryTest.getServerArgs(appKey, "127.0.0.1", 8010);
        ZkRegistry zkRegistry = serviceFactoryTest.getZkRegistry();
        serverArgs.setRegistrys(ImmutableList.of(zkRegistry));

        ThriftServerPublisher publisher = new ThriftServerPublisher(serverArgs);
        Thread thread = new Thread(publisher::init);
        thread.setDaemon(true);
        thread.start();
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void testBlock() {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotal(MAX_CONN);
        config.setMaxTotalPerKey(MAX_CONN);
        config.setMaxIdlePerKey(MAX_CONN);
        config.setMinIdlePerKey(MIN_CONN);
        config.setTestOnBorrow(true);
        config.setMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        config.setSoftMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        config.setJmxEnabled(false);

        ZkMonitor zkMonitor = new ZkMonitor();
        zkMonitor.setConnectString("localhost:2181");
        ClientArgs args = ClientArgs.builder()
                .localAppKey(localAppKey)
                .remoteAppKey(appKey)
                .monitors(ImmutableList.of(zkMonitor))
                .serviceInterface(ClassNameUtils.getClassName(Hello.class))
                .poolProvider(new BlockTrpcClientPoolImpl(config, BlockTrpcClient::new))
                .timeout(100)
                .build();

        TrpcClientProxy proxy = new TrpcClientProxy();
        proxy.setClientArgs(args);
        Hello.Client client = (Hello.Client) proxy.client();
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
                .localAppKey(localAppKey)
                .remoteAppKey(appKey)
                .monitors(ImmutableList.of(zkMonitor))
                .serviceInterface(ClassNameUtils.getClassName(Hello.class))
                .poolProvider(AsyncTrpcClientPoolImpl.getInstance())
                .timeout(100)
                .build();

        TrpcClientProxy proxy = new TrpcClientProxy();
        proxy.setClientArgs(args);
        Hello.AsyncClient client = (Hello.AsyncClient) proxy.asyncClient();
        CountDownLatch latch = new CountDownLatch(1);
        try {
            client.hi("world", new AsyncMethodCallback<Hello.AsyncClient.hi_call>() {
                @Override
                public void onComplete(Hello.AsyncClient.hi_call hi) {
                    try {
                        System.out.println("async response: " + hi.getResult());
                    } catch (TException e) {
                        fail();
                    }
                    latch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (TException e) {
            log.error("call error", e);
        }
        latch.await();
    }
}
