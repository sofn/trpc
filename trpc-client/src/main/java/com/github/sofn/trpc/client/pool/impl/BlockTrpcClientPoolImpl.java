package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.function.Function;

/**
 * <p>
 * BlockTrpcClientPoolImpl class.
 * </p>
 *
 * @author sofn
 * @version 1.0 Created at: 2016-09-22 14:13
 */
@Slf4j
public final class BlockTrpcClientPoolImpl implements TrpcClientPoolProvider<BlockTrpcClient> {

    private final GenericKeyedObjectPool<ThriftServerInfo, BlockTrpcClient> connections;

    public BlockTrpcClientPoolImpl(GenericKeyedObjectPoolConfig config,
                                   Function<ThriftServerInfo, BlockTrpcClient> transportProvider) {
        connections = new GenericKeyedObjectPool<>(new BlockTrpcClientFactory(transportProvider), config);
    }

    @Override
    public BlockTrpcClient getConnection(ThriftServerInfo thriftServerInfo) {
        try {
            return connections.borrowObject(thriftServerInfo);
        } catch (Exception e) {
            log.error("fail to get connection for {}", thriftServerInfo, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(ThriftServerInfo thriftServerInfo, BlockTrpcClient transport) {
        connections.returnObject(thriftServerInfo, transport);
    }

    @Override
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, BlockTrpcClient transport) {
        try {
            connections.invalidateObject(thriftServerInfo, transport);
        } catch (Exception e) {
            log.error("fail to invalid object:{},{}", thriftServerInfo, transport, e);
        }
    }

}
