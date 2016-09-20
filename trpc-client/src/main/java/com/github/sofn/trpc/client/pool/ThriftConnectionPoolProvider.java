package com.github.sofn.trpc.client.pool;

import com.github.sofn.trpc.core.config.ThriftServerInfo;
import org.apache.thrift.transport.TTransport;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 22:59.
 */
public interface ThriftConnectionPoolProvider {
    /**
     * <p>
     * getConnection.
     * </p>
     */
    TTransport getConnection(ThriftServerInfo thriftServerInfo);

    /**
     * <p>
     * returnConnection.
     * </p>
     */
    void returnConnection(ThriftServerInfo thriftServerInfo, TTransport transport);

    /**
     * <p>
     * returnBrokenConnection.
     * </p>
     */
    void returnBrokenConnection(ThriftServerInfo thriftServerInfo, TTransport transport);

}
