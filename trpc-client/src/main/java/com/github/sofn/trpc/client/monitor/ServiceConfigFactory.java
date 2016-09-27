package com.github.sofn.trpc.client.monitor;

import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:15
 */
public class ServiceConfigFactory {
    public static Map<ServiceKey, List<TrpcServiceNode>> configs = new ConcurrentHashMap<>();

    public List<TrpcServiceNode> getNodes(ServiceKey key) {
        return configs.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
    }

    public boolean addNode(ServiceKey key, TrpcServiceNode node) {
        List<TrpcServiceNode> nodes = getNodes(key);
        return nodes.add(node);
    }

    public boolean addAllNodes(ServiceKey key, List<TrpcServiceNode> nodes) {
        List<TrpcServiceNode> currentNodes = getNodes(key);
        return currentNodes.addAll(nodes);
    }

    public boolean removeNode(ServiceKey key, TrpcServiceNode node) {
        List<TrpcServiceNode> currentNodes = getNodes(key);
        return currentNodes.remove(node);
    }

}
