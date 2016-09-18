package com.github.sofn.trpc.core.test.utils;

import com.github.sofn.trpc.core.utils.NetUtils;
import org.junit.Test;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-19 00:13.
 */
public class NetUtilsTest {
    @Test
    public void testGetLocalHost() {
        System.out.println(NetUtils.getLocalAddress().getHostName());
    }
}
