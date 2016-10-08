package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.function.Function;

/**
 * AbstractTrpcClient 构建工程类
 *
 * @author sofn
 * @version 1.0 Created at: 2016-09-20 11:55
 */
@Slf4j
public class TrpcClientFactory<T extends AbstractTrpcClient> implements KeyedPooledObjectFactory<ThriftServerInfo, T> {

    private final Function<ThriftServerInfo, T> transportProvider;

    public TrpcClientFactory(Function<ThriftServerInfo, T> transportProvider) {
        this.transportProvider = transportProvider;
    }

    @Override
    public PooledObject<T> makeObject(ThriftServerInfo info) throws Exception {
        T client = transportProvider.apply(info);
        client.open();
        DefaultPooledObject<T> result = new DefaultPooledObject<>(client);
        log.debug("make new ThriftClient:{}", info);
        return result;
    }

    @Override
    public void destroyObject(ThriftServerInfo info, PooledObject<T> p) throws Exception {
        T client = p.getObject();
        if (client != null && client.isOpen()) {
            client.close();
            log.trace("unRegistry thrift connection:{}", info);
        }
    }

    @Override
    public boolean validateObject(ThriftServerInfo info, PooledObject<T> p) {
        try {
            return p.getObject().isOpen();
        } catch (Throwable e) {
            log.error("fail to validate tsocket:{}", info, e);
            return false;
        }
    }

    @Override
    public void activateObject(ThriftServerInfo info, PooledObject<T> p)
            throws Exception {
        // do nothing
    }

    @Override
    public void passivateObject(ThriftServerInfo info, PooledObject<T> p)
            throws Exception {
        // do nothing
    }
}
