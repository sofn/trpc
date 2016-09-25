package com.github.sofn.trpc.server;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.server.config.ServerArg;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 21:36.
 */
public class TrpcRegistryEventHandler implements TServerEventHandler {
    private ServerArg serverArg;

    public TrpcRegistryEventHandler(ServerArg serverArg) {
        this.serverArg = serverArg;
    }

    @Override
    public void preServe() {
        for (IRegistry registry : serverArg.getRegistrys()) {
            registry.registry(serverArg.getRegistryConfig());
        }
    }

    @Override
    public ServerContext createContext(TProtocol tProtocol, TProtocol tProtocol1) {
        return null;
    }

    @Override
    public void deleteContext(ServerContext serverContext, TProtocol tProtocol, TProtocol tProtocol1) {
    }

    @Override
    public void processContext(ServerContext serverContext, TTransport tTransport, TTransport tTransport1) {
    }
}
