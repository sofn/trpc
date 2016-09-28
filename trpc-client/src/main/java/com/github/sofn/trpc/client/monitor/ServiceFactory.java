package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.RegistryConfigListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thrift服务配置工厂
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-28 23:02.
 */
public class ServiceFactory {
    private static Map<ServiceKey, Set<TrpcServiceNode>> servicesMap = new ConcurrentHashMap<>();

    public static List<ServiceKey> getServiceKeys(ServiceKey serviceKey, List<AbstractMonitor> monitors) {
        if (servicesMap.get(serviceKey) == null) {
            synchronized (ServiceFactory.class) {
                if (servicesMap.get(serviceKey) == null) {
                    startMonitor(serviceKey.getRemoteKey(), monitors);
                    return null;
                }
            }
        }
        return Collections.emptyList();
    }

    //TODO
    private static List<RegistryConfig> startMonitor(String remoteKey, List<AbstractMonitor> monitors) {
        Map<ThriftServerInfo, TrpcServiceNode> result = new ConcurrentHashMap<>();
        monitors.forEach(monitor -> {
            List<RegistryConfig> registryConfigs = monitor.monitorRemoteKey(new RegistryConfigListener(remoteKey) {
                @Override
                public void addServer(ThriftServerInfo serverInfo, RegistryConfig config) {

                }

                @Override
                public void removeServer(ThriftServerInfo serverInfo) {

                }

                @Override
                public void updateServer(ThriftServerInfo serverInfo, RegistryConfig config) {

                }
            });
        });
        return null;
    }
}
