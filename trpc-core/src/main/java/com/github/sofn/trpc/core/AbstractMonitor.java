package com.github.sofn.trpc.core;

import com.github.sofn.trpc.core.config.RegistryConfig;

import java.util.List;
import java.util.Observable;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-20 22:40.
 */
public abstract class AbstractMonitor extends Observable {

    public abstract List<RegistryConfig> getAllServers();

    public abstract String removeServer();

    public abstract RegistryConfig addServer();

}
