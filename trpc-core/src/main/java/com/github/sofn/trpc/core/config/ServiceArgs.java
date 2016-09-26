package com.github.sofn.trpc.core.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.TBaseProcessor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 16:34.
 */
@Setter
@Getter
@AllArgsConstructor
public class ServiceArgs {
    @NotNull
    private TBaseProcessor processor;
    @NotBlank
    private String service;
    @Min(0)
    @Max(100)
    private int weight = -1;
    @Min(0)
    private int timeout = (int) TimeUnit.SECONDS.toMillis(3);

    @JsonIgnore
    public TBaseProcessor getProcessor() {
        return this.processor;
    }

    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(this.service, this.weight, this.timeout);
    }
}
