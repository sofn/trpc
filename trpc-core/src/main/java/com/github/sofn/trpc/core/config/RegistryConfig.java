package com.github.sofn.trpc.core.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-19 16:53
 */
@Data
public class RegistryConfig extends AbstractConfig {
    private String registry;
    private ThriftServerInfo serverInfo;
    private int weight = 100; //权重

    private Map<String, String> options;

    public String getStringData() {
        return registry + "://" + serverInfo.getHost() + ":" + serverInfo.getPort() + "?weight=" + weight + "&id=" + id
                + "&" + Joiner.on("&").withKeyValueSeparator("=").join(options);
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
        }

        return result;
    }

}
