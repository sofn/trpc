package com.github.sofn.trpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 23:02.
 */
@Data
@ToString
@AllArgsConstructor
public class ThriftServerInfo {
    private String host;
    private int port;
}
