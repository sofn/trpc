package com.github.sofn.trpc.registry.zk.test;

import com.github.sofn.trpc.core.config.ServiceArg;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.DemoClient;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.registry.zk.ZKRegistry;
import com.github.sofn.trpc.server.ThriftServerPublisher;
import com.github.sofn.trpc.server.config.ServerArg;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 23:14.
 */
public class ThriftServerPublisherTest {

    @Test
    public void test() throws UnknownHostException, InterruptedException {
        ZKRegistry registry = new ZKRegistry();
        registry.setConnectString("localhost:2181");
        registry.setSessionTimeout(100);
        registry.setConnectionTimeout(1000);

        registry.initConnect();

        ServerArg arg = ServerArg.builder()
                .appkey("test")
                .host("127.0.0.1")
                .port(8888)
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), ClassNameUtils.getClassName(Hello.class), 80, 100))
                .registry(registry)
                .build();
        arg.afterPropertiesSet();

        ThriftServerPublisher publisher = new ThriftServerPublisher(arg);
        Thread thread = new Thread(publisher::init);
        thread.setDaemon(true);
        thread.start();

        TimeUnit.MILLISECONDS.sleep(20);
        DemoClient demoClient = new DemoClient();
        demoClient.bioCall();
        demoClient.nioCall();
    }

}
