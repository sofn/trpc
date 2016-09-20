package com.github.sofn.trpc.core.config;

import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 23:02.
 */
@Data
@ToString
@AllArgsConstructor
public class ThriftServerInfo {
    private static Splitter splitter = Splitter.on(':');
    private final String host;
    private final int port;

    public ThriftServerInfo(String hostPort) {
        List<String> split = splitter.splitToList(hostPort);
        assert split.size() == 2;
        this.host = split.get(0);
        this.port = NumberUtils.toInt(split.get(1));
    }
}
