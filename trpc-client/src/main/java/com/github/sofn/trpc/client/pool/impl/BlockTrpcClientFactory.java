package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.BlockTrpcClient;
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
public class BlockTrpcClientFactory implements KeyedPooledObjectFactory<ThriftServerInfo, BlockTrpcClient> {

    private final Function<ThriftServerInfo, BlockTrpcClient> transportProvider;

    public BlockTrpcClientFactory(Function<ThriftServerInfo, BlockTrpcClient> transportProvider) {
        this.transportProvider = transportProvider;
    }

    @Override
    public PooledObject<BlockTrpcClient> makeObject(ThriftServerInfo info) throws Exception {
        BlockTrpcClient client = transportProvider.apply(info);
        client.open();
        DefaultPooledObject<BlockTrpcClient> result = new DefaultPooledObject<>(client);
        log.debug("make new ThriftClient:{}", info);
        return result;
    }

    @Override
    public void destroyObject(ThriftServerInfo info, PooledObject<BlockTrpcClient> p) throws Exception {
        BlockTrpcClient client = p.getObject();
        if (client != null && client.isOpen()) {
            client.close();
            log.trace("unRegistry thrift connection:{}", info);
        }
    }

    @Override
    public boolean validateObject(ThriftServerInfo info, PooledObject<BlockTrpcClient> p) {
        try {
            return p.getObject().isOpen();
        } catch (Throwable e) {
            log.error("fail to validate tsocket:{}", info, e);
            return false;
        }
    }

    @Override
    public void activateObject(ThriftServerInfo info, PooledObject<BlockTrpcClient> p)
            throws Exception {
        // do nothing
    }

    @Override
    public void passivateObject(ThriftServerInfo info, PooledObject<BlockTrpcClient> p)
            throws Exception {
        // do nothing
    }
}
