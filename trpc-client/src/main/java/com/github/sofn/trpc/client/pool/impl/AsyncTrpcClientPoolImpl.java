package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.client.AysncTrpcClient;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.core.config.ThriftServerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-24 19:26
 */
public class AsyncTrpcClientPoolImpl implements TrpcClientPoolProvider<AysncTrpcClient> {

    private Map<ThriftServerInfo, AysncTrpcClient> cache = new ConcurrentHashMap<>();

    private static class AsyncTrpcClientPoolImplHolder {
        private static final AsyncTrpcClientPoolImpl INSTANCE = new AsyncTrpcClientPoolImpl();
    }

    public static AsyncTrpcClientPoolImpl getInstance() {
        return AsyncTrpcClientPoolImplHolder.INSTANCE;
    }


    @Override
    public AysncTrpcClient getConnection(ThriftServerInfo thriftServerInfo) {
        return cache.computeIfAbsent(thriftServerInfo, info -> {
            AysncTrpcClient client = new AysncTrpcClient(info);
            client.open();
            return client;
        });
    }

    @Override
    public void returnConnection(ThriftServerInfo thriftServerInfo, AysncTrpcClient transport) {
        //do nothing
    }

    @Override
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, AysncTrpcClient transport) {
        //do nothing
    }
}
