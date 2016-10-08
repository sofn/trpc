package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.client.loadbalance.AbstractLoadBalance;
import com.github.sofn.trpc.client.loadbalance.RandomLoadBalance;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.client.pool.impl.TrpcClientPoolImpl;
import com.github.sofn.trpc.core.AbstractMonitor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.List;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-28 16:55
 */
@Getter
public class ClientArgs {
    private volatile TrpcClientPoolProvider poolProvider;
    private volatile AbstractLoadBalance loadBalance;
    private GenericKeyedObjectPoolConfig poolConfig;
    private List<AbstractMonitor> monitors;
    private String serviceInterface;
    private String localAppKey;
    private String remoteAppKey;
    private int timeout;
    private boolean async;

    @Builder
    public ClientArgs(GenericKeyedObjectPoolConfig poolConfig, List<AbstractMonitor> monitors, String serviceInterface, String localAppKey, String remoteAppKey, int timeout, boolean async) {
        this.poolConfig = poolConfig;
        this.monitors = monitors;
        this.serviceInterface = serviceInterface;
        this.localAppKey = localAppKey;
        this.remoteAppKey = remoteAppKey;
        this.timeout = timeout;
        this.async = async;
    }

    public AbstractLoadBalance getLoadBalance() {
        if (this.loadBalance == null) {
            synchronized (this) {
                if (this.loadBalance == null) {
                    this.loadBalance = new RandomLoadBalance();
                }
            }
        }
        return this.loadBalance;
    }

    public TrpcClientPoolProvider getPoolProvider() {
        if (this.poolProvider == null) {
            synchronized (this) {
                if (this.poolProvider == null) {
                    this.poolProvider = new TrpcClientPoolImpl(this.poolConfig, this.async);
                }
            }
        }
        return this.poolProvider;
    }

}
