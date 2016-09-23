package com.github.sofn.trpc.core.test.utils;

import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-23 16:08
 */
public class ClassNameUtilsTest {

    @Test
    public void testClassName() {
        assertThat(ClassNameUtils.getClassName(Hello.class)).isEqualTo("com.github.sofn.trpc.demo.Hello");
        assertThat(ClassNameUtils.getOuterClassName(Hello.class)).isEqualTo("com.github.sofn.trpc.demo.Hello");
        assertThat(ClassNameUtils.getOuterClassName(Hello.Client.class)).isEqualTo("com.github.sofn.trpc.demo.Hello");
    }
}
