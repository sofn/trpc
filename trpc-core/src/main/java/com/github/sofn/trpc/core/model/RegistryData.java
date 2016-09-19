package com.github.sofn.trpc.core.model;

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
public class RegistryData {
    private String protocol;
    private String host;
    private int port;
    private int weight = 100; //权重

    public String getStringData() {
        return protocol + "://" + host + ":" + port + "?weight=" + weight;
    }

    public static RegistryData parse(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        RegistryData result = new RegistryData();
        result.setProtocol(data.substring(0, data.indexOf("://")));

        int hostIpEndIndex = data.indexOf("?") > 0 ? data.indexOf("?") : data.length();
        String hostIp = data.substring(data.indexOf("://") + 3, hostIpEndIndex);
        result.setHost(hostIp.substring(0, hostIp.indexOf(":")));
        result.setPort(NumberUtils.toInt(hostIp.substring(hostIp.indexOf(":") + 1)));

        if (data.indexOf("?") > 0) {
            Map<String, String> params = Splitter.on("&").withKeyValueSeparator("=").split(data.substring(data.indexOf("?") + 1));
            result.setWeight(NumberUtils.toInt(params.get("weight"), 100));
        }

        return result;
    }

}
