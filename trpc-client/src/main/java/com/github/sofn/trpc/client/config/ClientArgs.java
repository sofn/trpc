package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.client.loadbalance.AbstractLoadBalance;
import com.github.sofn.trpc.client.loadbalance.RandomLoadBalance;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.client.pool.impl.TrpcClientPoolImpl;
import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.StaticMonitor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> serviceInterfaces;
    private String serviceInterface;
    private String localAppKey;
    private String remoteAppKey;
    private String ipPorts;
    private int timeout;
    private boolean async;

    @Builder
    public ClientArgs(GenericKeyedObjectPoolConfig poolConfig, List<AbstractMonitor> monitors, List<String> serviceInterfaces, String serviceInterface, String localAppKey, String remoteAppKey, String ipPorts, int timeout, boolean async) {
        this.poolConfig = poolConfig;
        this.monitors = monitors;
        this.serviceInterfaces = serviceInterfaces;
        this.serviceInterface = serviceInterface;
        this.localAppKey = localAppKey;
        this.remoteAppKey = remoteAppKey;
        this.ipPorts = ipPorts;
        this.timeout = timeout;
        this.async = async;
        afterPropertiesSet();
    }

    private void afterPropertiesSet() {
        if (StringUtils.isNoneEmpty(serviceInterface)) {
            if (serviceInterfaces == null) {
                serviceInterfaces = new ArrayList<>();
            }
            serviceInterfaces.add(serviceInterface);
        }
        if (StringUtils.isNotEmpty(ipPorts)) {
            List<ThriftServerInfo> serverInfos = Arrays.stream(ipPorts.split(",")).map(ThriftServerInfo::new).collect(Collectors.toList());
            if (monitors == null) {
                monitors = new ArrayList<>();
            }
            monitors.add(new StaticMonitor(serverInfos, serviceInterfaces, remoteAppKey));
        }
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
