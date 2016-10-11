package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.RegistryConfigListener;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 根据服务信息，获取节点信息，有监控机制保证数据实时同步
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-28 23:02.
 */
@Slf4j
public class ServiceFactory {
    private static Map<ServiceKey, Set<TrpcServiceNode>> servicesMap = new ConcurrentHashMap<>();

    /**
     * 获取节点信息
     */
    public static Set<TrpcServiceNode> getServiceKeys(ServiceKey serviceKey, AbstractMonitor monitor) {
        return getServiceKeys(serviceKey, ImmutableList.of(monitor));
    }

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
        final Set<RegistryConfig> result = new HashSet<>();
        monitors.forEach(monitor -> {
            List<RegistryConfig> registryConfigs = monitor.monitorRemoteKey(new RegistryConfigListenerImpl(remoteKey));
            result.addAll(new HashSet<>(registryConfigs));
        });
        registry2ServiceMap(result);
    }

    private static void registry2ServiceMap(RegistryConfig config) {
        log.info("registry2ServiceMap" + config);
        config.getServers().forEach(service -> {
            //取出现有元素
            Set<TrpcServiceNode> nodes = servicesMap.computeIfAbsent(
                    new ServiceKey(config.getAppKey(), service.getService()),
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
            Map<ServiceKey, Set<TrpcServiceNode>> deleteNodes = new HashMap<>();
            servicesMap.forEach((k, set) -> set.forEach(v -> {
                if (StringUtils.equals(v.getIp(), serverInfo.getIp()) && v.getPort() == serverInfo.getPort()) {
                    deleteNodes.computeIfAbsent(k, key -> new HashSet<>()).add(v);
                }
            }));

            deleteNodes.forEach((k, set) -> servicesMap.get(k).removeAll(set));
        }

        @Override
        public void updateServer(RegistryConfig config) {
            removeServer(config.getServerInfo());
            addServer(config);
        }
    }
}
