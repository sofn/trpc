package com.github.sofn.trpc.client.client;

import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.exception.TRpcException;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-23 14:29
 */
@Slf4j
public class BlockTrpcClient extends AbstractTrpcClient<TServiceClient> {
    private TProtocol protocol;

    public BlockTrpcClient(ThriftServerInfo serverInfo) {
        super(serverInfo);
    }

    @Override
    public void open() {
        TTransport transport;

        transport = new TSocket(serverInfo.getIp(), serverInfo.getPort());
        transport = new TFramedTransport(transport);
        try {
            transport.open();
        } catch (TTransportException e) {
            log.error("connect thrift key error", e);
            throw new TRpcException(e);
        }

        this.protocol = new TBinaryProtocol(transport);
    }

    @Override
    public boolean isOpen() {
        return this.protocol != null && this.protocol.getTransport().isOpen();
    }

    @Override
    public void close() {
        if (this.protocol != null) {
            this.protocol.getTransport().close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends TServiceClient> C getClient(final Class<C> clazz) {
        return (C) super.clients.computeIfAbsent(ClassNameUtils.getOuterClassName(clazz), className -> {
            TMultiplexedProtocol tmp = new TMultiplexedProtocol(this.protocol, className);
            try {
                return clazz.getConstructor(TProtocol.class).newInstance(tmp);
            } catch (Exception e) {
                log.error("never execute");
                return null;
            }
        });
    }
}
