package com.github.sofn.trpc.direct;

import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-23 19:09
 */
public class DemoServer {
    private Thread thread;

    public void start(CountDownLatch latch, int port) {
        //发布多个服务
        TMultiplexedProcessor processor = new TMultiplexedProcessor();
        processor.registerProcessor(ClassNameUtils.getClassName(Hello.class), new Hello.Processor<>(new HelloServer()));

        try {
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
            //异步IO，需要使用TFramedTransport，它将分块缓存读取。
            TTransportFactory transportFactory = new TFramedTransport.Factory();
            //使用高密度二进制协议
            TProtocolFactory proFactory = new TCompactProtocol.Factory();
            TServer server = new TThreadedSelectorServer(new
                    TThreadedSelectorServer.Args(serverTransport)
                    .transportFactory(transportFactory)
                    .protocolFactory(proFactory)
                    .processor(processor)
            );
            System.out.println("Starting the hello server...");
            latch.countDown();
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDaemon() {
        startDaemon(8888);
    }

    public void startDaemon(int port) {
        CountDownLatch latch = new CountDownLatch(1);
        thread = new Thread(() -> start(latch, port));
        thread.setDaemon(true);
        thread.start();
        try {
            latch.await();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }

}
