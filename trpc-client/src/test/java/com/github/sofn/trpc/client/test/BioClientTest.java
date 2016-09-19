package com.github.sofn.trpc.client.test;

import com.github.phantomthief.thrift.client.ThriftClient;
import com.github.phantomthief.thrift.client.impl.FailoverThriftClientImpl;
import com.github.phantomthief.thrift.client.pool.ThriftServerInfo;
import com.github.phantomthief.thrift.client.pool.impl.DefaultThriftConnectionPoolImpl;
import com.github.phantomthief.thrift.client.utils.FailoverCheckingStrategy;
import com.github.sofn.trpc.demo.Hello;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-19 17:30
 */
public class BioClientTest {
    @Test
    public void test01() {
        // a customize failover client, if the call fail 10 times in 30 seconds, the backend server will be marked as fail for 1 minutes.
        FailoverCheckingStrategy<ThriftServerInfo> failoverCheckingStrategy = new FailoverCheckingStrategy<>(
                10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1));
        ThriftClient customizedFailoverThriftClient = new FailoverThriftClientImpl(
                failoverCheckingStrategy, () -> Arrays.asList(//
                ThriftServerInfo.of("127.0.0.1", 9090), //
                ThriftServerInfo.of("127.0.0.1", 9091) //
        ), DefaultThriftConnectionPoolImpl.getInstance());

        Hello.Client iface = customizedFailoverThriftClient.iface(Hello.Client.class);
        System.out.println(iface);
    }
}
