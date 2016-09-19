package com.github.sofn.trpc.core.test.model;

import com.github.sofn.trpc.core.model.RegistryData;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lishaofeng
 * @version 1.0 Created at: 2016-09-19 17:07
 */
public class RegistryDataTest {
    @Test
    public void test() {
        RegistryData data = new RegistryData();
        data.setProtocol("zookeeper");
        data.setHost("127.0.0.1");
        data.setPort(8080);
        data.setWeight(80);

        assertThat(data.getStringData()).isEqualTo("zookeeper://127.0.0.1:8080?weight=80");

        RegistryData data2 = RegistryData.parse("zookeeper://127.0.0.1:8080");
        assertThat(data2).isNotNull();
        assertThat(data2.getProtocol()).isEqualTo(data.getProtocol());
        assertThat(data2.getHost()).isEqualTo(data.getHost());
        assertThat(data2.getPort()).isEqualTo(data.getPort());
        assertThat(data2.getWeight()).isEqualTo(100);
    }
}
