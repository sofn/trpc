package com.github.sofn.trpc.client.pool.impl;

import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-20 11:55
 */
@Slf4j
public class BioConnectionFactory implements KeyedPooledObjectFactory<ThriftServerInfo, TTransport> {

    private final Function<ThriftServerInfo, TTransport> transportProvider;

    public BioConnectionFactory(Function<ThriftServerInfo, TTransport> transportProvider) {
        this.transportProvider = transportProvider;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool2.PooledObjectFactory#makeObject()
     */
    @Override
    public PooledObject<TTransport> makeObject(ThriftServerInfo info) throws Exception {
        TTransport transport = transportProvider.apply(info);
        transport.open();
        DefaultPooledObject<TTransport> result = new DefaultPooledObject<>(transport);
        log.trace("make new thrift connection:{}", info);
        return result;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool2.PooledObjectFactory#destroyObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void destroyObject(ThriftServerInfo info, PooledObject<TTransport> p)
            throws Exception {
        TTransport transport = p.getObject();
        if (transport != null && transport.isOpen()) {
            transport.close();
            log.trace("close thrift connection:{}", info);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool2.PooledObjectFactory#validateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public boolean validateObject(ThriftServerInfo info, PooledObject<TTransport> p) {
        try {
            return p.getObject().isOpen();
        } catch (Throwable e) {
            log.error("fail to validate tsocket:{}", info, e);
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool2.PooledObjectFactory#activateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void activateObject(ThriftServerInfo info, PooledObject<TTransport> p)
            throws Exception {
        // do nothing
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool2.PooledObjectFactory#passivateObject(org.apache.commons.pool2.PooledObject)
     */
    @Override
    public void passivateObject(ThriftServerInfo info, PooledObject<TTransport> p)
            throws Exception {
        // do nothing
    }
}
