package com.github.sofn.trpc.core.config;

import com.github.sofn.trpc.core.utils.JsonUtils;
import lombok.*;

import java.util.Collections;
import java.util.List;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:53
 */
@Setter
@Getter
@Builder
@EqualsAndHashCode(of = {"serverInfo", "appKey"}, callSuper = false)
public class RegistryConfig extends AbstractConfig {
    @Singular
    private List<ServiceConfig> servers = Collections.emptyList();
    private ThriftServerInfo serverInfo;
    private String appKey;
    private String registry;
    private String hostName;
    private int weight; //权重

    public RegistryConfig() {
        super("registry");
    }

    public RegistryConfig(List<ServiceConfig> servers, ThriftServerInfo serverInfo, String appKey, String registry, String hostName, int weight) {
        this();
        this.servers = servers;
        this.serverInfo = serverInfo;
        this.appKey = appKey;
        this.registry = registry;
        this.hostName = hostName;
        this.weight = weight;
    }

    public String toJsonString() {
        this.servers.stream().forEach(AbstractConfig::fillId);
        this.fillId();
        return JsonUtils.toJson(this);
    }

    public static RegistryConfig parse(String json) {
        return JsonUtils.fromJson(json, RegistryConfig.class);
    }

}
