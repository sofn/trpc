package com.github.sofn.trpc.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import java.util.concurrent.Executor;

/**
 * Netty Channel Handler for thrift message.
 */
@Slf4j
@ChannelHandler.Sharable
public class ThriftHandler extends ChannelInboundHandlerAdapter {

    private TProcessorFactory processorFactory;
    private TProtocolFactory inProtocolFactory;
    private TProtocolFactory outProtocolFactory;

    private Executor userExecutor;

    public ThriftHandler(TProcessorFactory processorFactory, TProtocolFactory inProtocolFactory,
                         TProtocolFactory outProtocolFactory, Executor executor) {
        this.processorFactory = processorFactory;
        this.inProtocolFactory = inProtocolFactory;
        this.outProtocolFactory = outProtocolFactory;
        this.userExecutor = executor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        TNettyTransport transport = new TNettyTransport(ctx.channel(), (ByteBuf) msg);
        userExecutor.execute(new ProcessorTask(ctx, transport));
    }

    /**
     * Execute the thrift processor and user code in user threads.
     */
    class ProcessorTask implements Runnable {
        private ChannelHandlerContext ctx;
        private TNettyTransport transport;

        ProcessorTask(ChannelHandlerContext ctx, TNettyTransport transport) {
            this.ctx = ctx;
            this.transport = transport;
        }

        @Override
        public void run() {
            TProtocol inProtocol = inProtocolFactory.getProtocol(transport);
            TProtocol outProtocol = outProtocolFactory.getProtocol(transport);
            try {
                processorFactory.getProcessor(transport).process(inProtocol, outProtocol);
                ctx.writeAndFlush(transport.out);
            } catch (TException e) {
                log.error("Thrift exception happen when call processor", e);
                // TODO: response thrift wrong exception,
            } catch (Exception e) {
                log.error("User exception happen when call processor", e);
                // TODO: response user wrong exception,
            } finally {
                transport.in.release();
            }
        }
    }
}
