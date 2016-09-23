package com.github.sofn.trpc.client.pool;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.core.config.ThriftServerInfo;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 22:59.
 */
public interface TrpcClientPoolProvider {
    /**
     * <p>
     * getConnection.
     * </p>
     */
    AbstractTrpcClient getConnection(ThriftServerInfo thriftServerInfo);

    /**
     * <p>
     * returnConnection.
     * </p>
     */
    void returnConnection(ThriftServerInfo thriftServerInfo, AbstractTrpcClient transport);

    /**
     * <p>
     * returnBrokenConnection.
     * </p>
     */
    void returnBrokenConnection(ThriftServerInfo thriftServerInfo, AbstractTrpcClient transport);

}
