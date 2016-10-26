package com.github.sofn.trpc.server.test.config;

import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.server.netty.NettyServerArgs;
import com.github.sofn.trpc.server.netty.TNettyServer;
import org.apache.thrift.TMultiplexedProcessor;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-10-26 22:03.
 */
public class NettyServerTest {

    public void startNettyServer(int port) throws InterruptedException {
        TMultiplexedProcessor processor = new TMultiplexedProcessor();
        processor.registerProcessor(ClassNameUtils.getClassName(Hello.class), new Hello.Processor<>(new HelloServer()));

        NettyServerArgs serverArgs = new NettyServerArgs()
                .port(port)
                .processor(processor);

        TNettyServer server = new TNettyServer(serverArgs);
        server.serve();
        server.waitForClose();
    }

    public void startDaemon(int port) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startNettyServer(port);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
