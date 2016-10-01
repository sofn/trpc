package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.RegistryConfigListener;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 根据服务信息，获取节点信息，有监控机制保证数据实时同步
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-28 23:02.
 */
public class ServiceFactory {
    private static Map<ServiceKey, Set<TrpcServiceNode>> servicesMap = new ConcurrentHashMap<>();

    /**
     * 获取节点信息
     */
    public static Set<TrpcServiceNode> getServiceKeys(ServiceKey serviceKey, List<AbstractMonitor> monitors) {
        if (servicesMap.get(serviceKey) == null) {
            synchronized (ServiceFactory.class) {
                if (servicesMap.get(serviceKey) == null) {
                    startMonitor(serviceKey.getRemoteKey(), monitors);
                }
            }
        }
        return servicesMap.computeIfAbsent(serviceKey, key -> new ConcurrentSkipListSet<>());
    }

    /**
     * 开始监控
     */
    private static void startMonitor(final String remoteKey, List<AbstractMonitor> monitors) {
        final Set<RegistryConfig> result = new ConcurrentSkipListSet<>();
        monitors.forEach(monitor -> {
            List<RegistryConfig> registryConfigs = monitor.monitorRemoteKey(new RegistryConfigListenerImpl(remoteKey));
            result.addAll(new HashSet<>(registryConfigs));
        });
        registry2ServiceMap(result);
    }

    private static void registry2ServiceMap(RegistryConfig config) {
        config.getServers().forEach(service -> {
            //取出现有元素
            Set<TrpcServiceNode> nodes = servicesMap.computeIfAbsent(
                    new ServiceKey(config.getKey(), service.getService()),
                    serviceKey -> new ConcurrentSkipListSet<>()
            );
            //拼接
            nodes.addAll(new HashSet<>(TrpcServiceNode.fromRegistryConfig(config)));
        });
    }

    private static void registry2ServiceMap(Set<RegistryConfig> registryConfigs) {
        registryConfigs.forEach(ServiceFactory::registry2ServiceMap);
    }

    private static class RegistryConfigListenerImpl extends RegistryConfigListener {

        RegistryConfigListenerImpl(String remoteKey) {
            super(remoteKey);
        }

        @Override
        public void addServer(RegistryConfig config) {
            registry2ServiceMap(config);
        }

        @Override
        public void removeServer(ThriftServerInfo serverInfo) {
            List<ServiceKey> deleteKeys = Lists.newArrayList();
            servicesMap.forEach((k, set) -> set.forEach(v -> {
                if (StringUtils.equals(v.getHost(), serverInfo.getHost()) && v.getPort() == v.getPort()) {
                    deleteKeys.add(k);
                }
            }));
            deleteKeys.forEach(k -> servicesMap.remove(k));
        }

        @Override
        public void updateServer(RegistryConfig config) {
            removeServer(config.getServerInfo());
            addServer(config);
        }
    }
}
