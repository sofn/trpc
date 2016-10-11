package com.github.sofn.trpc.core.test.config;

import com.github.sofn.trpc.core.config.ThriftServerInfo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-27 23:59.
 */
public class ThriftServerInfoTest {

    @Test
    public void test() {
        ThriftServerInfo info = new ThriftServerInfo("127.0.0.1", 8888);
        assertThat(info.toString()).isEqualTo("127.0.0.1:8888");
        ThriftServerInfo info2 = ThriftServerInfo.parse(info.toString());
        assertThat(info2).isNotNull();
        assertThat(info2.getIp()).isEqualTo("127.0.0.1");
        assertThat(info2.getPort()).isEqualTo(8888);
    }

}
