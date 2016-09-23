package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.function.Function;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-20 11:55
 */
@Slf4j
public class TrpcClientFactory implements KeyedPooledObjectFactory<ThriftServerInfo, AbstractTrpcClient> {

    private final Function<ThriftServerInfo, AbstractTrpcClient> transportProvider;

    public TrpcClientFactory(Function<ThriftServerInfo, AbstractTrpcClient> transportProvider) {
        this.transportProvider = transportProvider;
    }

    @Override
    public PooledObject<AbstractTrpcClient> makeObject(ThriftServerInfo info) throws Exception {
        AbstractTrpcClient client = transportProvider.apply(info);
        DefaultPooledObject<AbstractTrpcClient> result = new DefaultPooledObject<>(client);
        log.trace("make new ThriftClient:{}", info);
        return result;
    }

    @Override
    public void destroyObject(ThriftServerInfo info, PooledObject<AbstractTrpcClient> p) throws Exception {
        AbstractTrpcClient client = p.getObject();
        if (client != null && client.isOpen()) {
            client.close();
            log.trace("close thrift connection:{}", info);
        }
    }

    @Override
    public boolean validateObject(ThriftServerInfo info, PooledObject<AbstractTrpcClient> p) {
        try {
            return p.getObject().isOpen();
        } catch (Throwable e) {
            log.error("fail to validate tsocket:{}", info, e);
            return false;
        }
    }

    @Override
    public void activateObject(ThriftServerInfo info, PooledObject<AbstractTrpcClient> p)
            throws Exception {
        // do nothing
    }

    @Override
    public void passivateObject(ThriftServerInfo info, PooledObject<AbstractTrpcClient> p)
            throws Exception {
        // do nothing
    }
}
