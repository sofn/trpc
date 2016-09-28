package com.github.sofn.trpc.core.monitor;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-27 21:50.
 */
@Getter
@AllArgsConstructor
public abstract class RegistryConfigListener {
    private String remoteKey;

    public abstract void addServer(ThriftServerInfo serverInfo, RegistryConfig config);

    public abstract void removeServer(ThriftServerInfo serverInfo);

    public abstract void updateServer(ThriftServerInfo serverInfo, RegistryConfig config);
}
