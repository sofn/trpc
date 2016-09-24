package com.github.sofn.trpc.client.test.pool;

import com.github.sofn.trpc.client.client.AysncTrpcClient;
import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.pool.impl.AsyncTrpcClientPoolImpl;
import com.github.sofn.trpc.client.pool.impl.BlockTrpcClientPoolImpl;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.DemoServer;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-23 23:12
 */
public class TrpcClientPoolTest {
    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;

    private int port = 8888;

    @Before
    public void init() {
        this.port = RandomUtils.nextInt(10000, 20000);
        new DemoServer().startDaemon(this.port);
    }

    @Test
    public void testBlockPool() throws TException {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotal(MAX_CONN);
        config.setMaxTotalPerKey(MAX_CONN);
        config.setMaxIdlePerKey(MAX_CONN);
        config.setMinIdlePerKey(MIN_CONN);
        config.setTestOnBorrow(true);
        config.setMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        config.setSoftMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
        config.setJmxEnabled(false);

        BlockTrpcClientPoolImpl clientPool = new BlockTrpcClientPoolImpl(config, BlockTrpcClient::new);

        ThriftServerInfo serverInfo = new ThriftServerInfo("localhost", this.port);
        BlockTrpcClient trpcClient = clientPool.getConnection(serverInfo);
        assertThat(trpcClient.isOpen()).isTrue();

        Hello.Client client = trpcClient.getClient(Hello.Client.class);
        assertThat(client.hi("world")).isEqualTo("hello world");
    }

    @Test
    public void testAysncPool() throws TException, InterruptedException {
        AsyncTrpcClientPoolImpl clientPool = new AsyncTrpcClientPoolImpl();
        ThriftServerInfo serverInfo = new ThriftServerInfo("localhost", this.port);
        AysncTrpcClient trpcClient = clientPool.getConnection(serverInfo);
        Hello.AsyncClient client = trpcClient.getClient(Hello.AsyncClient.class);

        CountDownLatch downLatch = new CountDownLatch(1);
        client.hi("world", new AsyncMethodCallback<Hello.AsyncClient.hi_call>() {
            @Override
            public void onComplete(Hello.AsyncClient.hi_call hi_call) {
                try {
                    System.out.println(hi_call.getResult());
                    assertThat(hi_call.getResult()).isEqualTo("hello world");
                    downLatch.countDown();
                } catch (TException e) {
                    fail();
                }
            }

            @Override
            public void onError(Exception e) {
                fail();
            }
        });
        downLatch.await();
    }

}
