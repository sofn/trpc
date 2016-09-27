package com.github.sofn.trpc.registry.zk;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.core.config.RegistryConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 22:37.
 */
@Slf4j
@Setter
public class ZkRegistry implements IRegistry {
    public static final String registry = "zookeeper";
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
            String nodeValue = registryConfig.toJsonString();
            PersistentNode node = new PersistentNode(client, CreateMode.EPHEMERAL, true, ZkConstant.SERVICES_DIR + registryConfig.getAppKey() + "/" + nodeName, nodeValue.getBytes());
            node.start();
            node.waitForInitialCreate(3, TimeUnit.SECONDS);
            String actualPath = node.getActualPath();
            log.info("registry to zookeeper, node: " + actualPath + " value: " + nodeValue);
        } catch (Exception e) {
            log.error("registry error", e);
        }
    }

    @Override
    public boolean destory() {
        return false;
    }

}
