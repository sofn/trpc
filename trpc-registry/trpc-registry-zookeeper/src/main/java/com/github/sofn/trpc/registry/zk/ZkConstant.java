package com.github.sofn.trpc.registry.zk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-27 21:43.
 */
public class ZkConstant {
    //执行节点变化listener的线程池
    static final ExecutorService zkPool = Executors.newFixedThreadPool(2);
    static final String NAMESPACE = "trpc";
    static final String SERVICES_DIR = "/servers/";
}
