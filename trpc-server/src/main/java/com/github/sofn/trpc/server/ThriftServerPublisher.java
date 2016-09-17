package com.github.sofn.trpc.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-17 23:44.
 */
@Data
@Slf4j
public class ThriftServerPublisher {
    private IRegistry registry;
    private String appkey;
    private int port;
    private List<TBaseProcessor> services;

    public void init() {
        try {
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            for (TBaseProcessor service : services) {
                String className = service.getClass().getName();
                if (className.endsWith("$Processor")) {
                    className = className.substring(0, className.indexOf("$Processor"));
                }
                processor.registerProcessor(className, service);
            }
            //serverSocket
            InetAddress inetAddress = InetAddress.getLocalHost();
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(new InetSocketAddress(inetAddress, port));
            //异步IO，需要使用TFramedTransport，它将分块缓存读取。
            TTransportFactory transportFactory = new TFramedTransport.Factory();
            //使用高密度二进制协议
            TProtocolFactory proFactory = new TCompactProtocol.Factory();
            // Use this for a multithreaded server
            TServer server = new TThreadedSelectorServer(new
                    TThreadedSelectorServer.Args(serverTransport)
                    .transportFactory(transportFactory)
                    .protocolFactory(proFactory)
                    .processor(processor)
            );

            log.info("Starting the Thrift server...");
            server.serve();
            registry.registry(appkey, inetAddress, port);
        } catch (Exception e) {
            log.error("publish thrift server error", e);
        }
    }
}
