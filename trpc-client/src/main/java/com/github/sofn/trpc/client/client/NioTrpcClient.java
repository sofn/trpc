package com.github.sofn.trpc.client.client;

import com.github.sofn.trpc.core.exception.TRpcException;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TCompactProtocol;
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
@Data
@Slf4j
public class NioTrpcClient extends AbstractTrpcClient<TServiceClient> {
    private TProtocol protocol;

    @Override
    public void open() {
        TTransport transport;

        transport = new TSocket(serverInfo.getHost(), serverInfo.getPort());
        transport = new TFramedTransport(transport);
        try {
            transport.open();
        } catch (TTransportException e) {
            log.error("connect thrift server error", e);
            throw new TRpcException(e);
        }

        this.protocol = new TCompactProtocol(transport);
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
    public <M extends TServiceClient> M getClient(final Class<M> clazz) {
        return (M) super.clients.computeIfAbsent(ClassNameUtils.getOuterClassName(clazz), className -> {
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
