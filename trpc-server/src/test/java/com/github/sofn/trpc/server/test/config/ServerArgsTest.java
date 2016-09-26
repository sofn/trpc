package com.github.sofn.trpc.server.test.config;

import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.server.config.ServerArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 18:12.
 */
@Slf4j
public class ServerArgsTest {

    @Test
    public void testNormal() {
        ServerArgs arg = ServerArgs.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .hostName("localhost")
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmpty() {
        ServerArgs arg = ServerArgs.builder()
                .appkey("")
                .port(8080)
                .host("")
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyList() {
        ServerArgs arg = ServerArgs.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .hostName("localhost")
                .weight(80)
                .build();
        arg.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSet() {
        ServerArgs arg = ServerArgs.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
        assertThat(arg.getPort()).isEqualTo(8080);
        assertThat(arg.getHost()).isEqualTo("127.0.0.1");

        arg = ServerArgs.builder()
                .appkey("test")
                .port(8080)
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
        assertThat(arg.getPort()).isEqualTo(8080);
        assertThat(arg.getHost()).isNotEqualTo("127.0.0.1");
        log.info("hostName: " + arg.getHostName());
        log.info("hostAddress: " + arg.getHost());
    }

}
