package com.github.sofn.trpc.registry.zk;

import com.github.sofn.trpc.core.AbstractMonitor;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.monitor.RegistryConfigListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 14:38
 */
@Slf4j
@Setter
public class ZkMonitor extends AbstractMonitor {

    private String connectString;
    private int sessionTimeout = 3000;
    private int connectionTimeout = 100;
    private int retrySleepTime = 100;
    private int maxRetries = 3;

    private CuratorFramework client = null;

    private void initMonitor() {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .canBeReadOnly(false)
                .retryPolicy(new ExponentialBackoffRetry(retrySleepTime, maxRetries))
                .namespace(ZkConstant.NAMESPACE)
                .build();
        client.start();
        client.getConnectionStateListenable().addListener((client1, newState)
                -> log.info("zookeeper client " + connectString + " change status: " + newState.name()));
        log.info("init zookeeper connect: " + connectString);
    }

    public CuratorFramework getClient() {
        if (this.client == null) {
            initMonitor();
        }
        return client;
    }

    @Override
    public List<RegistryConfig> monitorRemoteKey(RegistryConfigListener listener) {
        try {
            final PathChildrenCache childrenCache = new PathChildrenCache(getClient(), ZkConstant.SERVICES_DIR + listener.getRemoteKey(), true);
            childrenCache.getListenable().addListener(
                    (client1, event) -> {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                listener.addServer(RegistryConfig.parse(new String(event.getData().getData())));
                                log.info("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                listener.removeServer(nodeName2ServerInfo(event.getData().getPath()));
                                log.info("CHILD_REMOVED: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                String newData = new String(event.getData().getData());
                                listener.updateServer(RegistryConfig.parse(newData));
                                log.info("CHILD_UPDATED: " + event.getData().getPath());
                                break;
                            default:
                                break;
                        }
                    }, ZkConstant.zkPool);
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            return childrenCache.getCurrentData().stream().map(d -> RegistryConfig.parse(new String(d.getData()))).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("startMonitor error", e);
        }
        return null;
    }

    private ThriftServerInfo nodeName2ServerInfo(String path) {
        if (path.lastIndexOf("-") > 0) {
            return ThriftServerInfo.parse(path.substring(path.lastIndexOf("-") + 1));
        }
        return null;
    }
}
