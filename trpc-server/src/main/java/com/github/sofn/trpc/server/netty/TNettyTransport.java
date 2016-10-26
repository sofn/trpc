package com.github.sofn.trpc.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Thrift in memory transport base on Netty's ByteBuf.
 */
public class TNettyTransport extends TTransport {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	private Channel channel;

	public ByteBuf in;
	public ByteBuf out;

	public TNettyTransport(Channel channel, ByteBuf in) {
		this.channel = channel;
		this.in = in;
		out = channel.alloc().buffer(DEFAULT_BUFFER_SIZE);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws TTransportException {
		int _read = Math.min(in.readableBytes(), length);
		in.readBytes(bytes, offset, _read);
		return _read;
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws TTransportException {
		out.writeBytes(bytes, offset, length);
	}

	@Override
	public void open() throws TTransportException {
		// no-op
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public void flush() throws TTransportException {
		// no-op
	}
}
