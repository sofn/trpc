package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.client.pool.TrpcClientPoolProvider;
import lombok.Builder;
import lombok.Data;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-28 16:55
 */
@Data
@Builder
public class ClientArgs {
    private TrpcClientPoolProvider<AbstractTrpcClient> poolProvider;
    private String serviceInterface;
    private String localAppKey;
    private String remoteAppKey;
    private int timeout;
}
