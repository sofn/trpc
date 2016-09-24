package com.github.sofn.trpc.direct;

import com.github.sofn.trpc.demo.Hello;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-23 19:09
 */
@Slf4j
public class DemoClient {
    private int port = 8888;

    @Before
    public void init() {
        this.port = RandomUtils.nextInt(10000, 20000);
        new DemoServer().startDaemon(this.port);
    }

    @Test
    public void nioCall() {
        try {
            //异步调用管理器
            TAsyncClientManager clientManager = new TAsyncClientManager();
            //设置传输通道，调用非阻塞IO。
            final TNonblockingTransport transport = new TNonblockingSocket("localhost", this.port, 1000);
            //设置协议
            TProtocolFactory protocolFactory = (TProtocolFactory) tTransport -> {
                TProtocol protocol = new TCompactProtocol(tTransport);
                return new TMultiplexedProtocol(protocol, Hello.class.getName());
            };

            //创建Client
            final Hello.AsyncClient client = new Hello.AsyncClient(protocolFactory, clientManager, transport);
            //调用服务
            log.info("开始：" + System.currentTimeMillis());
            client.hi("world", new AsyncMethodCallback<Hello.AsyncClient.hi_call>() {
                @Override
                public void onError(Exception exception) {
                    log.info("error： " + System.currentTimeMillis());
                    exception.printStackTrace();
                }

                @Override
                public void onComplete(Hello.AsyncClient.hi_call hi_call) {
                    log.info("complete： " + System.currentTimeMillis());
                    try {
                        log.info(hi_call.getResult());
                    } catch (Exception e) {
                        log.error("error", e);
                    }
                }
            });
            log.info("结束：" + System.currentTimeMillis());
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            log.error("error", e);
        }

    }

    @Test
    public void bioCall() {
        try {
            log.info("开始：" + System.currentTimeMillis());
            TTransport transport = new TFramedTransport(new TSocket("localhost", this.port));
            transport.open();

            //使用高密度二进制协议
            TProtocol protocol = new TCompactProtocol(transport);

            TMultiplexedProtocol mp2 = new TMultiplexedProtocol(protocol, Hello.class.getName());
            Hello.Client helloClient2 = new Hello.Client(mp2);
            log.info(helloClient2.hi("tom"));
            log.info("结束：" + System.currentTimeMillis());
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

}
