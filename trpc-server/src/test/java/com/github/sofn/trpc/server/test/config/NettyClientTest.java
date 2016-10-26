package com.github.sofn.trpc.server.test.config;

import com.github.sofn.trpc.direct.DemoClient;
import com.github.sofn.trpc.utils.NumUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-10-26 22:17.
 */
@Slf4j
public class NettyClientTest {
    private DemoClient client = new DemoClient();

    @Before
    public void init() throws InterruptedException {
        int port = NumUtil.nextPort();
        this.client.setPort(port);
        new NettyServerTest().startDaemon(port);
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void nioCall() {
        this.client.nioCall();
    }

    @Test
    public void bioCall() {
        this.client.bioCall();
    }
}
