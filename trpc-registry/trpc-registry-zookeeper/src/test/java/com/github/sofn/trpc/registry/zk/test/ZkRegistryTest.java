package com.github.sofn.trpc.registry.zk.test;

import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.core.exception.TRpcRegistryException;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.registry.zk.ZkRegistry;
import com.github.sofn.trpc.server.config.ServerArgs;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 23:14.
 */
public class ZkRegistryTest {

    public ServerArgs getServerArgs(String appKey, String host, int port) {
        ServerArgs arg = ServerArgs.builder()
                .appkey(appKey)
                .port(port)
                .host(host)
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), ClassNameUtils.getClassName(Hello.class), 80, 100))
                .build();
        arg.afterPropertiesSet();
        return arg;
    }

    public ZkRegistry startZkRegistry(String zkConn, String appKey, String host, int port) {
        ZkRegistry registry = new ZkRegistry();
        registry.setConnectString(zkConn);
        registry.setSessionTimeout(100);
        registry.setConnectionTimeout(1000);
        registry.initConnect();
        ServerArgs arg = getServerArgs(appKey, host, port);
        registry.registry(arg.getRegistryConfig());
        return registry;
    }

    @Test
    public void testRegistry() throws UnknownHostException, InterruptedException {
        startZkRegistry("localhost:2181", "test", "127.0.0.1", 8080);
        TimeUnit.MILLISECONDS.sleep(10);
    }

    @Test(expected = TRpcRegistryException.class)
    public void testRepeatRegistry() throws UnknownHostException, InterruptedException {
        startZkRegistry("localhost:2181", "test", "127.0.0.1", 8080);
        startZkRegistry("localhost:2181", "test", "127.0.0.1", 8080);
        TimeUnit.MILLISECONDS.sleep(10);
    }

    @Test
    public void testModify() throws UnknownHostException, InterruptedException {
        ZkRegistry registry = startZkRegistry("localhost:2181", "test", "127.0.0.1", 8081);
        ServerArgs oldArgs = getServerArgs("test", "127.0.0.1", 8081);
        oldArgs.setWeight(60);
        registry.modify(oldArgs.getRegistryConfig());
        TimeUnit.MILLISECONDS.sleep(10);
    }

    @Test
    public void testModifyDirect() throws UnknownHostException, InterruptedException {
        ZkRegistry registry = new ZkRegistry();
        registry.setConnectString("localhost:2181");
        registry.setSessionTimeout(100);
        registry.setConnectionTimeout(1000);
        registry.initConnect();
        ServerArgs args = getServerArgs("test", "127.0.0.1", 8082);
        registry.modify(args.getRegistryConfig());
        TimeUnit.MILLISECONDS.sleep(10);
    }

}
