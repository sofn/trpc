package com.github.sofn.trpc.registry.zk.test;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.MonitorAble;
import com.github.sofn.trpc.registry.zk.ZkMonitor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-27 22:15.
 */
@Slf4j
public class ZkMonitorTest {
    private String zkconnStr = "localhost:2181";
    private String appKey = "test";

    @Test
    public void test01() throws InterruptedException {
        ZkRegistryTest registry = new ZkRegistryTest();
        registry.startZkRegistry(zkconnStr, appKey, "127.0.0.1", 8000);

        TimeUnit.MILLISECONDS.sleep(20);

        ZkMonitor monitor = new ZkMonitor();
        monitor.setConnectString(zkconnStr);
        monitor.setSessionTimeout(100);
        monitor.setConnectionTimeout(1000);
        List<RegistryConfig> registryConfigs = monitor.monitorRemoteKey(appKey, new MonitorAble() {
            @Override
            public void removeServer(ThriftServerInfo serverInfo) {
                log.info("monitorRemoteKey removeServer receive: " + serverInfo);
            }

            @Override
            public void addServer(ThriftServerInfo serverInfo) {
                log.info("monitorRemoteKey addServer receive: " + serverInfo);
            }

            @Override
            public void updateServer(ThriftServerInfo serverInfo, RegistryConfig config) {
                log.info("monitorRemoteKey updateServer receive: " + serverInfo + " config: " + config.toJsonString());
            }
        });
        TimeUnit.MILLISECONDS.sleep(10);
        registry.startZkRegistry(zkconnStr, appKey, "127.0.0.1", 8001);
        System.out.println(registryConfigs.size());
        registryConfigs.stream().map(RegistryConfig::toJsonString).forEach(System.out::println);

        TimeUnit.MILLISECONDS.sleep(20);
    }
}
