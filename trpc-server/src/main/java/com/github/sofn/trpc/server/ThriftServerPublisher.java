package com.github.sofn.trpc.server;

import com.github.sofn.trpc.core.config.ServiceArg;
import com.github.sofn.trpc.server.config.ServerArg;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportFactory;

import java.net.InetSocketAddress;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-17 23:44.
 */
@Slf4j
public class ThriftServerPublisher {
    private ServerArg serverArg;
    private TServer server;

    public ThriftServerPublisher(ServerArg serverArg) {
        serverArg.afterPropertiesSet();
        this.serverArg = serverArg;
    }

    public void init() {
        try {
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            for (ServiceArg service : serverArg.getServices()) {
                String className = service.getService();
                if (className.endsWith("$Processor")) {
                    className = className.substring(0, className.indexOf("$Processor"));
                }
                processor.registerProcessor(className, service.getProcessor());
            }
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(new InetSocketAddress(serverArg.getHost(), serverArg.getPort()));
            //异步IO，需要使用TFramedTransport，它将分块缓存读取。
            TTransportFactory transportFactory = new TFramedTransport.Factory();
            //使用高密度二进制协议
            TProtocolFactory proFactory = new TCompactProtocol.Factory();
            // Use this for a multithreaded key
            this.server = new TThreadedSelectorServer(new
                    TThreadedSelectorServer.Args(serverTransport)
                    .transportFactory(transportFactory)
                    .protocolFactory(proFactory)
                    .processor(processor)
            );
            log.info("Starting the Thrift key...");
            server.setServerEventHandler(new TrpcRegistryEventHandler(serverArg));
            server.serve();
        } catch (Exception e) {
            log.error("publish thrift key error", e);
        }
    }

    public void stop() {
        if (this.server != null && this.server.isServing()) {
            this.server.stop();
        }
    }
}
