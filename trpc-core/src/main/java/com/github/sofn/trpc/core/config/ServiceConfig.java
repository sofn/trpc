package com.github.sofn.trpc.core.config;

import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 16:34.
 */
@Getter
public class ServiceConfig extends AbstractConfig {
    @NotBlank
    private String service;
    @Min(0)
    @Max(100)
    private int weight = -1;
    @Min(0)
    private int timeout = (int) TimeUnit.SECONDS.toMillis(3);

    private ServiceConfig() {
        super("service");
    }

    public ServiceConfig(String service, int weight) {
        this();
        this.service = service;
        this.weight = weight;
    }

    public ServiceConfig(String service, int weight, int timeout) {
        this();
        this.service = service;
        this.weight = weight;
        this.timeout = timeout;
    }
}
