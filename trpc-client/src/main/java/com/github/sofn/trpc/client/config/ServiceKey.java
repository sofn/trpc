package com.github.sofn.trpc.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-26 13:16
 */
@Data
@AllArgsConstructor
public class ServiceKey {
    private String remoteKey;
    private String service;
}
