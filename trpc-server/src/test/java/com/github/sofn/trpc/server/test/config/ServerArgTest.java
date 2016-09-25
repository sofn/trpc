package com.github.sofn.trpc.server.test.config;

import com.github.sofn.trpc.core.config.ServiceArg;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.server.config.ServerArg;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 18:12.
 */
@Slf4j
public class ServerArgTest {

    @Test
    public void testNormal() {
        ServerArg arg = ServerArg.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .hostName("localhost")
                .weight(80)
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmpty() {
        ServerArg arg = ServerArg.builder()
                .appkey("")
                .port(8080)
                .host("")
                .weight(80)
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyList() {
        ServerArg arg = ServerArg.builder()
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
        ServerArg arg = ServerArg.builder()
                .appkey("test")
                .port(8080)
                .host("127.0.0.1")
                .weight(80)
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
        assertThat(arg.getPort()).isEqualTo(8080);
        assertThat(arg.getHost()).isEqualTo("127.0.0.1");

        arg = ServerArg.builder()
                .appkey("test")
                .port(8080)
                .weight(80)
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService", 80, 100))
                .service(new ServiceArg(new Hello.Processor<>(new HelloServer()), "helloService2", 80, 100))
                .build();
        arg.afterPropertiesSet();
        assertThat(arg.getPort()).isEqualTo(8080);
        assertThat(arg.getHost()).isNotEqualTo("127.0.0.1");
        log.info("hostName: " + arg.getHostName());
        log.info("hostAddress: " + arg.getHost());
    }

}
