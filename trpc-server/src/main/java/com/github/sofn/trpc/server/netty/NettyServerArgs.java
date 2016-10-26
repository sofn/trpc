package com.github.sofn.trpc.server.netty;

import org.apache.thrift.server.TServer.AbstractServerArgs;

/**
 * Arguments for Netty Server
 */
public class NettyServerArgs extends AbstractServerArgs<NettyServerArgs> {

    public String ip;
    public int port = -1;

    public int bossThreads = 2;
    public int workerThreads = 0;
    public int userThreads = Runtime.getRuntime().availableProcessors() * 2;

    public int socketTimeoutMills = -1;
    public int shutdownTimeoutMills = 10000;

    public int sendBuff = -1;
    public int recvBuff = -1;

    public NettyServerArgs() {
        super(null);
    }


    public NettyServerArgs ip(String ip) {
        this.ip = ip;
        return this;
    }

    public NettyServerArgs port(int port) {
        this.port = port;
        return this;
    }

    public NettyServerArgs bossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
        return this;
    }

    public NettyServerArgs workerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }

    public NettyServerArgs userThreads(int userThreads) {
        this.userThreads = userThreads;
        return this;
    }

    public NettyServerArgs socketTimeoutMills(int socketTimeoutMills) {
        this.socketTimeoutMills = socketTimeoutMills;
        return this;
    }

    public NettyServerArgs shutdownTimeoutMills(int shutdownTimeoutMills) {
        this.shutdownTimeoutMills = shutdownTimeoutMills;
        return this;
    }

    public NettyServerArgs sendBuff(int sendBuff) {
        this.sendBuff = sendBuff;
        return this;
    }

    public NettyServerArgs recvBuff(int recvBuff) {
        this.recvBuff = recvBuff;
        return this;
    }

    public void validate() {
        if (port < 0) {
            throw new IllegalArgumentException("port " + port + " is wrong.");
        }
    }
}
