package com.github.sofn.trpc.core;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.monitor.MonitorAble;

import java.util.List;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-20 22:40.
 */
public abstract class AbstractMonitor {


    public abstract List<RegistryConfig> monitorRemoteKey(String remoteKey, MonitorAble monitorAble);
}
