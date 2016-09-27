package com.github.sofn.trpc.core.monitor;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;

import java.util.Observable;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-27 21:50.
 */
public abstract class MonitorAble extends Observable {

    public abstract void removeServer(ThriftServerInfo serverInfo);

    public abstract void addServer(ThriftServerInfo serverInfo);

    public abstract void updateServer(ThriftServerInfo serverInfo, RegistryConfig string);
}
