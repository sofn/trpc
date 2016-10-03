package com.github.sofn.trpc.client.loadbalance;

import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.client.monitor.ServiceFactory;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-10-02 00:07
 */
public abstract class AbstractLoadBalance {

    /**
     * 从集群获取一个节点信息用于访问
     */
    public abstract TrpcServiceNode getNode(ServiceKey key, ClientArgs args);

    /**
     * 有序集合
     */
    protected List<TrpcServiceNode> getAllNodes(ServiceKey key, ClientArgs args) {
        return ImmutableList.copyOf(ServiceFactory.getServiceKeys(key, args.getMonitors()));
    }
}
