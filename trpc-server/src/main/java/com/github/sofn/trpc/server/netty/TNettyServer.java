package com.github.sofn.trpc.server.netty;

import com.github.sofn.trpc.core.exception.TRpcException;
import com.github.sofn.trpc.core.utils.Threads;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Thrift NIO Server base on Netty.
 */
public class TNettyServer extends TServer {

    private static Logger logger = LoggerFactory.getLogger(TNettyServer.class);

    private NettyServerArgs args;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ExecutorService userThreadPool;
    private ChannelFuture f;

    public TNettyServer(NettyServerArgs args) {
        super(args);
        this.args = args;
    }

    @Override
    public void serve() {
        logger.info("Netty Server is starting");

        args.validate();

        ServerBootstrap b = configServer();

        try {
            // start server
            if (args.ip != null && args.ip.length() > 0) {
                f = b.bind(args.ip, args.port).sync();
            } else {
                f = b.bind(args.port).sync();
            }
            logger.info("Netty Server started and listening on " + args.port);
            setServing(true);

            // register shutown hook
            Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        } catch (Exception e) {
            logger.error("Exception happen when start server", e);
            throw new TRpcException(e);
        }
    }

    /**
     * blocking to wait for close.
     */
    public void waitForClose() throws InterruptedException {
        f.channel().closeFuture().sync();
    }

    @Override
    public void stop() {
        logger.info("Netty server is stopping");

        bossGroup.shutdownGracefully();
        Threads.gracefulShutdown(userThreadPool, args.shutdownTimeoutMills, args.shutdownTimeoutMills, TimeUnit.SECONDS);
        workerGroup.shutdownGracefully();

        logger.info("Netty server stoped");
    }

    private ServerBootstrap configServer() {
        bossGroup = new NioEventLoopGroup(args.bossThreads, new DefaultThreadFactory("NettyBossGroup", true));
        workerGroup = new NioEventLoopGroup(args.workerThreads, new DefaultThreadFactory("NettyWorkerGroup", true));
        userThreadPool = Executors.newFixedThreadPool(args.userThreads, new DefaultThreadFactory("UserThreads", true));

        final ThriftHandler thriftHandler = new ThriftHandler(this.processorFactory_, this.inputProtocolFactory_,
                this.outputProtocolFactory_, userThreadPool);

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        if (args.socketTimeoutMills > 0) {
            b.childOption(ChannelOption.SO_TIMEOUT, args.socketTimeoutMills);
        }

        if (args.recvBuff > 0) {
            b.childOption(ChannelOption.SO_RCVBUF, args.recvBuff);
        }

        if (args.sendBuff > 0) {
            b.childOption(ChannelOption.SO_SNDBUF, args.sendBuff);
        }

        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(createThriftFramedDecoder(), createThriftFramedEncoder(), thriftHandler);
            }
        });

        return b;
    }

    private ChannelHandler createThriftFramedDecoder() {
        return new ThriftFramedDecoder();
    }

    private ChannelHandler createThriftFramedEncoder() {
        return new ThriftFramedEncoder();
    }

    class ShutdownThread extends Thread {
        @Override
        public void run() {
            TNettyServer.this.stop();
        }
    }
}
