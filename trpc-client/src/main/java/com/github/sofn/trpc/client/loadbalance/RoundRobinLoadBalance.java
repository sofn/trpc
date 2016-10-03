package com.github.sofn.trpc.client.loadbalance;

import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author sofn
 * @version 1.0 Created at: 2016-10-03 10:20
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private final Map<ServiceKey, AtomicInteger> callCounts = new ConcurrentHashMap<>();

    @Override
    public TrpcServiceNode getNode(ServiceKey serviceKey, ClientArgs args) {
        List<TrpcServiceNode> allNodes = getAllNodes(serviceKey, args);
        int length = allNodes.size(); // 总个数
        int maxWeight = 0;            // 最大权重
        int minWeight = Integer.MAX_VALUE; // 最小权重
        int weightSum = 0;
        Map<TrpcServiceNode, AtomicInteger> weightMap = new LinkedHashMap<>();
        for (int i = 0; i < length; i++) {
            TrpcServiceNode node = allNodes.get(i);
            weightMap.put(node, new AtomicInteger(node.getWeight()));
            int weight = node.getWeight();
            maxWeight = Math.max(maxWeight, weight); // 累计最大权重
            minWeight = Math.min(minWeight, weight); // 累计最小权重
            if (weight > 0) {
                weightSum += weight;
            }
        }
        AtomicInteger count = callCounts.computeIfAbsent(serviceKey, key -> new AtomicInteger());
        int currentSequence = count.getAndIncrement();
        if (maxWeight > 0 && minWeight < maxWeight) { // 权重不一样
            int mod = currentSequence % weightSum;
            for (int i = 0; i < maxWeight; i++) {

                for (Map.Entry<TrpcServiceNode, AtomicInteger> entry : weightMap.entrySet()) {
                    if (mod == 0 && entry.getValue().get() > 0) {
                        return entry.getKey();
                    }
                    if (entry.getValue().get() > 0) {
                        entry.getValue().decrementAndGet();
                        mod--;
                    }
                }
            }
        }
        // 取模轮循
        return allNodes.get(currentSequence % length);
    }

}
