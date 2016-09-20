package com.github.sofn.trpc.core.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:53
 */
@Data
public class RegistryConfig extends AbstractConfig {
    private static final Joiner collectionJoiner = Joiner.on(",");
    private static final Splitter collectionSplitter = Splitter.on(",");
    private String registry;
    private ThriftServerInfo serverInfo;
    private int weight = 100; //权重
    private Set<String> servers;

    public String getStringData() {
        return registry + "://" + serverInfo.getHost() + ":" + serverInfo.getPort()
                + "?weight=" + weight
                + "&id=" + id
                + "&servers=" + collectionJoiner.join(servers);
    }

    public static RegistryConfig parse(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        RegistryConfig result = new RegistryConfig();
        result.setRegistry(data.substring(0, data.indexOf("://")));

        int hostIpEndIndex = data.indexOf("?") > 0 ? data.indexOf("?") : data.length();
        String hostAndPort = data.substring(data.indexOf("://") + 3, hostIpEndIndex);
        result.setServerInfo(new ThriftServerInfo(hostAndPort));

        if (data.indexOf("?") > 0) {
            Map<String, String> params = Splitter.on("&").withKeyValueSeparator("=").split(data.substring(data.indexOf("?") + 1));
            result.setWeight(NumberUtils.toInt(params.get("weight"), 100));
            result.setId(NumberUtils.toInt(params.get("id")));
            result.setServers(ImmutableSet.copyOf(collectionSplitter.split(params.get("servers"))));
        }

        return result;
    }

}
