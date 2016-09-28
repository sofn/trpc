package com.github.sofn.trpc.client.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
}
