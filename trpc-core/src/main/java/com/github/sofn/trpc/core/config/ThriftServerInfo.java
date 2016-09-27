package com.github.sofn.trpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 23:02.
 */
@Data
@AllArgsConstructor
public class ThriftServerInfo {
    private String host;
    private int port;

    @Override
    public String toString() {
        return host + ":" + port;
    }

    public static ThriftServerInfo parse(String info) {
        String[] splits = StringUtils.split(info, ":");
        if (splits == null || splits.length != 2) {
            return null;
        }
        return new ThriftServerInfo(splits[0], NumberUtils.toInt(splits[1]));
    }
}
