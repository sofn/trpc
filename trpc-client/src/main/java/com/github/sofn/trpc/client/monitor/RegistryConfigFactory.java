package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:15
 */
public class RegistryConfigFactory {
    private static Map<String, Map<ThriftServerInfo, RegistryConfig>> configs = new ConcurrentHashMap<>(); //某个appKey下的所有配置

    public Collection<RegistryConfig> getNodes(final String remoteKey, AbstractMonitor monitor) {
        return getNodes(remoteKey, Lists.newArrayList(monitor));
    }

    /**
     * 获取节点同时监控
     */
    public Collection<RegistryConfig> getNodes(final String remoteKey, List<AbstractMonitor> monitors) {
        if (configs.get(remoteKey) == null) {
            synchronized (this) {
                if (configs.get(remoteKey) == null) {
                    Map<ThriftServerInfo, RegistryConfig> result = new ConcurrentHashMap<>();
                    monitors.forEach(monitor -> {
                        List<RegistryConfig> registryConfigs = monitor.monitorRemoteKey(null);
                        registryConfigs.forEach(config -> result.put(config.getServerInfo(), config));
                    });
                    configs.putIfAbsent(remoteKey, result);
                    return result.values();
                }
            }
        }
        return Collections.emptyList();
    }

    private static Map<ThriftServerInfo, RegistryConfig> getRegistryConfigsMap(String remoteKey) {
        return RegistryConfigFactory.configs.computeIfAbsent(remoteKey, key -> new ConcurrentHashMap<>());
    }

    private static Collection<RegistryConfig> getRegistryConfigs(String remoteKey) {
        return getRegistryConfigsMap(remoteKey).values();
    }

}
