package com.github.sofn.trpc.core.test.model;

import com.github.sofn.trpc.core.config.RegistryConfig;
import com.github.sofn.trpc.core.config.ServiceConfig;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 17:07
 */
public class RegistryConfigTest {
    private RegistryConfig data;

    @Before
    public void init() {
        data = new RegistryConfig();
        data.setRegistry("zookeeper");
        data.setServerInfo(new ThriftServerInfo("127.0.0.1", 8080));
        data.setAppKey("appKey");
        data.setHostName("localhost");
        data.setWeight(80);
        data.setServers(ImmutableList.of(new ServiceConfig("aaa", 100, 100), new ServiceConfig("bbb", 100, 100)));
    }

    @Test
    public void test() {
        assertThat(data.toJsonString()).isEqualTo("{\"key\":\"registry\",\"id\":0,\"servers\":[{\"key\":\"service\",\"id\":0,\"service\":\"aaa\",\"weight\":100,\"timeout\":100},{\"key\":\"service\",\"id\":1,\"service\":\"bbb\",\"weight\":100,\"timeout\":100}],\"serverInfo\":{\"host\":\"127.0.0.1\",\"port\":8080},\"appKey\":\"appKey\",\"registry\":\"zookeeper\",\"hostName\":\"localhost\",\"weight\":80}");

        RegistryConfig data2 = RegistryConfig.parse(data.toJsonString());
        assertThat(data2).isNotNull();
        assertThat(data2.getRegistry()).isEqualTo(data.getRegistry());
        assertThat(data2.getServerInfo().getHost()).isEqualTo(data.getServerInfo().getHost());
        assertThat(data2.getServerInfo().getPort()).isEqualTo(data.getServerInfo().getPort());
        assertThat(data2.getWeight()).isEqualTo(data.getWeight());
        assertThat(data2.getHostName()).isEqualTo(data.getHostName());
        assertThat(data2.getId()).isEqualTo(0);
        assertThat(data2.getServers().size()).isEqualTo(2);
        assertThat(data2.getServers().get(0).getId()).isEqualTo(0L);
        assertThat(data2.getServers().get(1).getId()).isEqualTo(1L);
    }
}
