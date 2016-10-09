package com.github.sofn.trpc.core.monitor;

import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ServiceConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-10-09 22:35.
 */
public class StaticMonitor extends AbstractMonitor {

    private List<ThriftServerInfo> servers;
    private List<String> services;
    private String appKey;

    public StaticMonitor(List<ThriftServerInfo> servers, List<String> services, String appKey) {
        checkNotNull(servers);
        checkNotNull(services);
        this.servers = servers;
        this.services = services;
        this.appKey = appKey;
    }

    @Override
    public List<RegistryConfig> monitorRemoteKey(RegistryConfigListener monitor) {
        List<ServiceConfig> serviceConfigs = services.stream().map(s -> new ServiceConfig(s, 100)).collect(Collectors.toList());
        return servers.stream()
                .map(s -> new RegistryConfig(serviceConfigs, s, appKey, "static", s.getHost(), 100))
                .collect(Collectors.toList());
    }

}
