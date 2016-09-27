package com.github.sofn.trpc.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:27
 */
@Data
@AllArgsConstructor
public class TrpcServiceNode {
    private String host;
    private int port;
    private int weight;
    private int timeout;
}
