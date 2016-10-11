package com.github.sofn.trpc.client.client;

import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.exception.TRpcException;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

import java.nio.channels.UnresolvedAddressException;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-24 19:32
 */
@Slf4j
public class AsyncTrpcClient extends AbstractTrpcClient<TAsyncClient> {
    private TNonblockingTransport transport;
    private TAsyncClientManager clientManager;
    private volatile boolean isOpen = true;

    public AsyncTrpcClient(ThriftServerInfo serverInfo) {
        super(serverInfo);
    }

    @Override
    public void open() {
        try {
            //异步调用管理器
            this.clientManager = new TAsyncClientManager();
            //设置传输通道，调用非阻塞IO。
            this.transport = new TNonblockingSocket(this.serverInfo.getIp(), this.serverInfo.getPort(), 1000);
        } catch (Exception e) {
            log.error("create AsyncTrpcClient:" + this.serverInfo + " error", e);
            throw new TRpcException("create AsyncTrpcClient:" + this.serverInfo + " error", e);
        }
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void close() {
        if (this.transport != null && this.transport.isOpen()) {
            log.info("unRegistry client: " + serverInfo);
            this.transport.close();
        } else {
            log.warn("try to unRegistry not open client: " + serverInfo);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends TAsyncClient> X getClient(final Class<X> clazz) {
        return (X) super.clients.computeIfAbsent(ClassNameUtils.getOuterClassName(clazz), (className) -> {
            TProtocolFactory protocolFactory = (TProtocolFactory) tTransport -> {
                TProtocol protocol = new TCompactProtocol(tTransport);
                return new TMultiplexedProtocol(protocol, className);
            };
            try {
                return clazz.getConstructor(TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class)
                        .newInstance(protocolFactory, this.clientManager, this.transport);
            } catch (Throwable e) {
                if (e instanceof UnresolvedAddressException) {
                    this.isOpen = false;
                }
                return null;
            }
        });
    }
}
