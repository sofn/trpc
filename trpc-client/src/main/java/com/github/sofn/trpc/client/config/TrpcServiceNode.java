package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:27
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"host", "port"})
public class TrpcServiceNode {
    private String host;
    private int port;
    private int weight;
    private int timeout;

    public static List<TrpcServiceNode> fromRegistryConfig(RegistryConfig registryConfig) {
        ThriftServerInfo server = registryConfig.getServerInfo();
        return registryConfig.getServers().stream()
                .map(s -> new TrpcServiceNode(server.getHost(), server.getPort(), s.getWeight(), s.getTimeout()))
                .collect(Collectors.toList());
    }


}
