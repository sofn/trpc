package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.client.loadbalance.AbstractLoadBalance;
import com.github.sofn.trpc.client.loadbalance.RandomLoadBalance;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import com.github.sofn.trpc.core.AbstractMonitor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-28 16:55
 */
@Data
@Builder
public class ClientArgs {
    private TrpcClientPoolProvider<AbstractTrpcClient> poolProvider;
    private List<AbstractMonitor> monitors;
    private AbstractLoadBalance loadBalance = new RandomLoadBalance();
    private String serviceInterface;
    private String localAppKey;
    private String remoteAppKey;
    private int timeout;
}
