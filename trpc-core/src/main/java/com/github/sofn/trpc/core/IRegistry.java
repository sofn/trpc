package com.github.sofn.trpc.core;

import com.github.sofn.trpc.core.config.RegistryConfig;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-17 23:56.
 */
public interface IRegistry {

    /**
     * 注册
     */
    void registry(RegistryConfig registryConfig);

    /**
     * 取消注册
     */
    boolean destory();

}
