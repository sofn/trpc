package com.github.sofn.trpc.registry.zk;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.exception.TRpcRegistryException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 22:37.
 */
@Slf4j
@Setter
public class ZkRegistry implements IRegistry {
    public static final String registry = "zookeeper";
    private static final Map<String, Map<String, PersistentNode>> nodeNames = new ConcurrentHashMap<>();
    private String connectString;
    private int sessionTimeout;
    private int connectionTimeout;
    private int retrySleepTime = 100;
    private int maxRetries = 3;

    private CuratorFramework client;

    public void initConnect() {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(retrySleepTime, maxRetries))
                .namespace(ZkConstant.NAMESPACE)
                .build();
        client.start();
        log.info("init zookeeper connect: " + connectString);
    }

    @Override
    public void registry(RegistryConfig registryConfig) {
        registryConfig.setRegistry(registry);
        try {
            String nodeName = registryConfig.getServerInfo().toString();
            Map<String, PersistentNode> appKeyServers = nodeNames.computeIfAbsent(registryConfig.getAppKey(), (key) -> new ConcurrentHashMap<>());
            if (appKeyServers.get(nodeName) != null) {
                log.error("server registry exists: " + nodeName);
                throw new TRpcRegistryException("server registry exists for key：" + nodeName);
            }
            String nodeValue = registryConfig.toJsonString();
            PersistentNode node = new PersistentNode(client, CreateMode.EPHEMERAL, true, ZkConstant.SERVICES_DIR + registryConfig.getAppKey() + "/" + nodeName, nodeValue.getBytes());
            node.start();
            node.waitForInitialCreate(3, TimeUnit.SECONDS);
            appKeyServers.put(nodeName, node);
            String actualPath = node.getActualPath();
            log.info("registry to zookeeper, node: " + actualPath + " value: " + nodeValue);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                log.error("registry error", e);
            }
        }
    }

    @Override
    public void modify(RegistryConfig registryConfig) {
        registryConfig.setRegistry(registry);
        try {
            String nodeName = registryConfig.getServerInfo().toString();
            Map<String, PersistentNode> appKeyServers = nodeNames.computeIfAbsent(registryConfig.getAppKey(), (key) -> new ConcurrentHashMap<>());
            PersistentNode node = appKeyServers.get(nodeName);
            if (node == null) {
                log.warn("server registry not exists " + nodeName + " try to registry");
                registry(registryConfig);
                return;
            }
            String nodeValue = registryConfig.toJsonString();
            node.setData(nodeValue.getBytes());
            String actualPath = node.getActualPath();
            log.info("zookeeper modify, node: " + actualPath + " value: " + nodeValue);
        } catch (Exception e) {
            log.error("modify error", e);
        }
    }

    @Override
    public boolean unRegistry(String appKey, ThriftServerInfo serverInfo) {
        try {
            Map<String, PersistentNode> appKeyServers = nodeNames.computeIfAbsent(appKey, (key) -> new ConcurrentHashMap<>());
            PersistentNode node = appKeyServers.get(serverInfo.toString());
            if (node != null) {
                log.info("unRegistry " + serverInfo + " actualPath: " + node.getActualPath());
                node.close();
                appKeyServers.remove(serverInfo.toString());
                return true;
            } else {
                log.warn("unRegistry " + serverInfo + " node not found");
            }
        } catch (Exception e) {
            log.error("unRegistry error, serverInfo: " + serverInfo, e);
        }
        return false;
    }

}
