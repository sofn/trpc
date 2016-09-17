package com.github.sofn.trpc.server;

import java.net.InetAddress;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-17 23:56.
 */
public interface IRegistry {
    /**
     * 注册
     *
     *
     * @param appkey
     * @param inetAddress 本地地址
     * @param port        发布端口号
     * @return 注册状态
     */
    boolean registry(String appkey, InetAddress inetAddress, int port);

    /**
     * 取消注册
     *
     * @return 取消注册
     */
    boolean destory();
}
