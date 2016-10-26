package com.github.sofn.trpc.direct;

import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.utils.NumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-23 19:09
 */
@Slf4j
public class DemoClient {
    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    @Before
    public void init() {
        this.port = NumUtil.nextPort();
        new DemoServer().startDaemon(this.port);
    }

    @Test
    public void nioCall() {
        try {
            //异步调用管理器
            TAsyncClientManager clientManager = new TAsyncClientManager();
            //设置传输通道，调用非阻塞IO。
            final TNonblockingTransport transport = new TNonblockingSocket("127.0.0.1", this.port, 1000);
            //设置协议
            TProtocolFactory protocolFactory = (TProtocolFactory) tTransport -> {
                TProtocol protocol = new TBinaryProtocol(tTransport);
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
                    fail();
                }

                @Override
                public void onComplete(Hello.AsyncClient.hi_call hi_call) {
                    log.info("complete： " + System.currentTimeMillis());
                    try {
                        log.info(hi_call.getResult());
                    } catch (Exception e) {
                        fail();
                    }
                }
            });
            log.info("结束：" + System.currentTimeMillis());
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            log.error("error", e);
            fail();
        }

    }

    @Test
    public void bioCall() {
        try {
            log.info("开始：" + System.currentTimeMillis());
            TTransport transport = new TFramedTransport(new TSocket("localhost", this.port));
            transport.open();

            //使用二进制协议
            TProtocol protocol = new TBinaryProtocol(transport);

            TMultiplexedProtocol mp2 = new TMultiplexedProtocol(protocol, Hello.class.getName());
            Hello.Client helloClient2 = new Hello.Client(mp2);
            log.info(helloClient2.hi("tom"));
            log.info(helloClient2.hi("tom"));
            log.info(helloClient2.hi("tom"));
            log.info("结束：" + System.currentTimeMillis());
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
            fail();
        }
    }

}
