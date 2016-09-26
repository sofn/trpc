package com.github.sofn.trpc.registry.zk.test;

import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.registry.zk.ZKRegistry;
import com.github.sofn.trpc.server.config.ServerArgs;
import org.junit.Test;

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

        ServerArgs arg = ServerArgs.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .hostName("localhost")
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), ClassNameUtils.getClassName(Hello.class), 80, 100))
                .build();
        arg.afterPropertiesSet();

        registry.registry(arg.getRegistryConfig());
        TimeUnit.SECONDS.sleep(3);
    }

}
