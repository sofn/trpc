package com.github.sofn.trpc.registry.zk;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.core.utils.NetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-18 22:37.
 */
@Data
@Slf4j
public class ZKRegistry implements IRegistry {
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
                .namespace("trpc")
                .build();
        client.start();
        log.info("init zookeeper connect: " + connectString);
    }

    @Override
    public boolean registry(String appkey, InetAddress inetAddress, int port) {
        try {
            String host = NetUtils.getLocalAddress().getHostName();
            String value = host + ":" + port;
            byte[] bytes = new byte[1024];
            Runtime.getRuntime().exec("arp -a").getOutputStream().write(bytes);
            System.out.println(new String(bytes));
            PersistentNode node = new PersistentNode(client, CreateMode.EPHEMERAL, true, "/servers/" + appkey + "/" + host, value.getBytes());
            node.start();
            node.waitForInitialCreate(3, TimeUnit.SECONDS);
            String actualPath = node.getActualPath();

            System.out.println(actualPath);
        } catch (Exception e) {
            log.error("registry error", e);
        }
        return false;
    }

    @Override
    public boolean destory() {
        return false;
    }

}
