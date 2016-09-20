package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.client.pool.ThriftConnectionFactory;
import com.github.sofn.trpc.client.pool.ThriftConnectionPoolProvider;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * <p>
 * DefaultThriftConnectionPoolImpl class.
 * </p>
 *
 * @author w.vela
 * @version $Id: $Id
 */
@Slf4j
public final class DefaultThriftConnectionPoolImpl implements ThriftConnectionPoolProvider {

    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;
    private static final int TIMEOUT = (int) MINUTES.toMillis(5);

    private final GenericKeyedObjectPool<ThriftServerInfo, TTransport> connections;

    public DefaultThriftConnectionPoolImpl(GenericKeyedObjectPoolConfig config,
                                           Function<ThriftServerInfo, TTransport> transportProvider) {
        connections = new GenericKeyedObjectPool<>(new ThriftConnectionFactory(transportProvider), config);
    }

    public DefaultThriftConnectionPoolImpl(GenericKeyedObjectPoolConfig config) {
        this(config, info -> {
            TSocket tsocket = new TSocket(info.getHost(), info.getPort());
            tsocket.setTimeout(TIMEOUT);
            return new TFramedTransport(tsocket);
        });
    }

    /**
     * <p>
     * getInstance.
     */
    public static DefaultThriftConnectionPoolImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TTransport getConnection(ThriftServerInfo thriftServerInfo) {
        try {
            return connections.borrowObject(thriftServerInfo);
        } catch (Exception e) {
            log.error("fail to get connection for {}", thriftServerInfo, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
        connections.returnObject(thriftServerInfo, transport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
        try {
            connections.invalidateObject(thriftServerInfo, transport);
        } catch (Exception e) {
            log.error("fail to invalid object:{},{}", thriftServerInfo, transport, e);
        }
    }

    private static class LazyHolder {

        private static final DefaultThriftConnectionPoolImpl INSTANCE;

        static {
            GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
            config.setMaxTotal(MAX_CONN);
            config.setMaxTotalPerKey(MAX_CONN);
            config.setMaxIdlePerKey(MAX_CONN);
            config.setMinIdlePerKey(MIN_CONN);
            config.setTestOnBorrow(true);
            config.setMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
            config.setSoftMinEvictableIdleTimeMillis(MINUTES.toMillis(1));
            config.setJmxEnabled(false);
            INSTANCE = new DefaultThriftConnectionPoolImpl(config);
        }
    }

}
