package com.github.sofn.trpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 23:02.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ThriftServerInfo {
    private String ip;
    private int port;

    public ThriftServerInfo(String ipPort) {
        checkNotNull(ipPort);
        String[] splits = ipPort.split(":");
        checkArgument(splits.length == 2, "ipPort format error");
        this.ip = splits[0];
        this.port = NumberUtils.toInt(splits[1]);
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public static ThriftServerInfo parse(String info) {
        String[] splits = StringUtils.split(info, ":");
        if (splits == null || splits.length != 2) {
            return null;
        }
        return new ThriftServerInfo(splits[0], NumberUtils.toInt(splits[1]));
    }
}
