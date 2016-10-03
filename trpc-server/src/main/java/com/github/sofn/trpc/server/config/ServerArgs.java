package com.github.sofn.trpc.server.config;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.utils.NetUtils;
import com.github.sofn.trpc.core.utils.ValidationUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封装所有server端的配置信息
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 16:28.
 */
@Data
@Builder
public class ServerArgs {
    @Singular
    private List<IRegistry> registrys = Collections.emptyList();
    @Size(min = 1, max = 100)
    @Singular
    private List<ServiceArgs> services = Collections.emptyList();
    @NotBlank
    private String appkey;  //localAppKey
    @NotBlank
    private String host;
    @NotBlank
    private String hostName;
    @Min(1)
    private int port;
    @Min(0)
    @Max(100)
    private int weight = 100;

    public void setWeight(int weight) {
        services.stream().filter(s -> s.getWeight() == this.weight).forEach(s -> s.setWeight(weight));
        this.weight = weight;
    }

    //对象创建完成后执行此方法
    public void afterPropertiesSet() {
        InetAddress localAddress = NetUtils.getLocalAddress();
        if (StringUtils.isBlank(host)) {
            this.host = localAddress.getHostAddress();
        }
        services.stream().filter(s -> s.getWeight() < 0).forEach(s -> s.setWeight(weight));
        this.hostName = localAddress.getHostName();
        ValidationUtils.validateWithException(this);
    }

    public RegistryConfig getRegistryConfig() {
        return RegistryConfig.builder()
                .serverInfo(new ThriftServerInfo(this.host, this.port))
                .servers(this.services.stream().map(ServiceArgs::getServiceConfig).collect(Collectors.toList()))
                .appKey(this.appkey)
                .hostName(this.hostName)
                .weight(this.weight)
                .build();
    }

}
