package com.github.sofn.trpc.client.config;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:27
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = {"ip", "port"})
public class TrpcServiceNode implements Comparable<TrpcServiceNode> {
    private String ip;
    private int port;
    private int weight;
    private int timeout;

    public static List<TrpcServiceNode> fromRegistryConfig(RegistryConfig registryConfig) {
        ThriftServerInfo server = registryConfig.getServerInfo();
        return registryConfig.getServers().stream()
                .map(s -> new TrpcServiceNode(server.getIp(), server.getPort(), s.getWeight(), s.getTimeout()))
                .collect(Collectors.toList());
    }

    public ThriftServerInfo toThriftServerInfo() {
        return new ThriftServerInfo(this.ip, this.port);
    }

    @Override
    public int compareTo(TrpcServiceNode other) {
        int result = this.ip.compareTo(other.ip);
        return result != 0 ? result : this.port - other.port;
    }
}
