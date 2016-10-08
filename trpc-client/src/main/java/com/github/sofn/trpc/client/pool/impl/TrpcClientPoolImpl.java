package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.client.client.AsyncTrpcClient;
import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.function.Function;

/**
 * <p>
 * TrpcClientPoolImpl class.
 * </p>
 *
 * @author sofn
 * @version 1.0 Created at: 2016-09-22 14:13
 */
@Slf4j
public final class TrpcClientPoolImpl<T extends AbstractTrpcClient> implements TrpcClientPoolProvider<T> {

    private final GenericKeyedObjectPool<ThriftServerInfo, T> connections;

    public TrpcClientPoolImpl(GenericKeyedObjectPoolConfig config) {
        this(config, false);
    }

    @SuppressWarnings("unchecked")
    public TrpcClientPoolImpl(GenericKeyedObjectPoolConfig config, boolean async) {
        Function<ThriftServerInfo, AbstractTrpcClient> creater;
        if (async) {
            creater = AsyncTrpcClient::new;
        } else {
            creater = BlockTrpcClient::new;
        }
        connections = new GenericKeyedObjectPool(new TrpcClientFactory<>(creater), config);
    }

    @Override
    public T getConnection(ThriftServerInfo thriftServerInfo) {
        try {
            return connections.borrowObject(thriftServerInfo);
        } catch (Exception e) {
            log.error("fail to get connection for {}", thriftServerInfo, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(ThriftServerInfo thriftServerInfo, T transport) {
        connections.returnObject(thriftServerInfo, transport);
    }

    @Override
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, T transport) {
        try {
            connections.invalidateObject(thriftServerInfo, transport);
        } catch (Exception e) {
            log.error("fail to invalid object:{},{}", thriftServerInfo, transport, e);
        }
    }

}
