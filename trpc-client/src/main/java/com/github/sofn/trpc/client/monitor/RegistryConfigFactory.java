package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.MonitorAble;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:15
 */
public class RegistryConfigFactory {
    public static Map<String, List<RegistryConfig>> configs = new ConcurrentHashMap<>();

    public List<RegistryConfig> getNodes(final String remoteKey, AbstractMonitor monitor) {
        return getNodes(remoteKey, Lists.newArrayList(monitor));
    }

    /**
     * 获取节点同时监控
     */
    public List<RegistryConfig> getNodes(final String remoteKey, List<AbstractMonitor> monitors) {
        if (configs.get(remoteKey) == null) {
            synchronized (this) {
                if (configs.get(remoteKey) == null) {
                    List<RegistryConfig> result = Lists.newArrayList();
                    monitors.forEach(monitor -> {
                        result.addAll(monitor.monitorRemoteKey(remoteKey, new ConfigMonitor()));
                    });
                    configs.putIfAbsent(remoteKey, result);
                    return result;
                }
            }
        }
        return null;
    }

    private class ConfigMonitor extends MonitorAble {
        @Override
        public void addServer(ThriftServerInfo serverInfo, RegistryConfig config) {

        }

        @Override
        public void removeServer(ThriftServerInfo serverInfo) {

        }

        @Override
        public void updateServer(ThriftServerInfo serverInfo, RegistryConfig string) {

        }
    }

}
