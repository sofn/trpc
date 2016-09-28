package com.github.sofn.trpc.core;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.monitor.RegistryConfigListener;

import java.util.List;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-20 22:40.
 */
public abstract class AbstractMonitor {

    /**
     * 返回remoteKey下所有节点数据，并监控变化
     *
     * @param monitor 消息listener
     * @return 返回现在的数据
     */
    public abstract List<RegistryConfig> monitorRemoteKey(RegistryConfigListener monitor);

}
