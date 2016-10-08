package com.github.sofn.trpc.core.test.config;

import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.core.utils.ValidationUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 16:51.
 */
@Slf4j
public class ServiceArgsTest {

    @Test
    public void testNormal() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", 100, 1);
        ValidationUtils.validateWithException(service);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNull() {
        ServiceArgs service = new ServiceArgs(null, "service", 100, 1);
        ValidationUtils.validateWithException(service);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmpty() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "", 100, 1);
        ValidationUtils.validateWithException(service);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMinMax1() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", 101, 0);
        ValidationUtils.validateWithException(service);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMinMax2() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", -1, 0);
        ValidationUtils.validateWithException(service);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMinMax3() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", 100, -1);
        ValidationUtils.validateWithException(service);
    }

    @Test
    public void testValidateInfo() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", 100, -1);
        try {
            ValidationUtils.validateWithException(service);
        } catch (ConstraintViolationException e) {
            List<String> message = ValidationUtils.extractMessage(e);
            assertThat(message.size()).isEqualTo(1);
            log.info("ServiceArgs.timeout 最小不能小于0 invalidValue: -1");
        }
    }

    @Test
    public void testValidateInfo2() {
        ServiceArgs service = new ServiceArgs(new Hello.Processor<>(new HelloServer()), "service", 100, -1);
        try {
            ValidationUtils.validateWithException(service);
        } catch (ConstraintViolationException e) {
            String message = ValidationUtils.extractMessageAsString(e);
            assertThat(message).isNotEmpty();
            log.info("ServiceArgs.timeout 最小不能小于0 invalidValue: -1");
        }
    }

}
