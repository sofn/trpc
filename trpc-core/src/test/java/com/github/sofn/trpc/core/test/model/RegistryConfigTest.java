package com.github.sofn.trpc.core.test.model;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 17:07
 */
public class RegistryConfigTest {
    @Test
    public void test() {
        RegistryConfig data = new RegistryConfig();
        data.setRegistry("zookeeper");
        data.setServerInfo(new ThriftServerInfo("127.0.0.1", 8080));
        data.setWeight(80);
        data.setServers(ImmutableSet.of("aaa", "bbb"));
        data.setId(1);

        assertThat(data.getStringData()).isEqualTo("zookeeper://127.0.0.1:8080?weight=80&id=1&servers=aaa,bbb");

        RegistryConfig data2 = RegistryConfig.parse(data.getStringData());
        assertThat(data2).isNotNull();
        assertThat(data2.getRegistry()).isEqualTo(data.getRegistry());
        assertThat(data2.getServerInfo().getHost()).isEqualTo(data.getServerInfo().getHost());
        assertThat(data2.getServerInfo().getPort()).isEqualTo(data.getServerInfo().getPort());
        assertThat(data2.getWeight()).isEqualTo(data.getWeight());
        assertThat(data2.getId()).isEqualTo(1);
        assertThat(data2.getServers().size()).isEqualTo(2);
    }
}
