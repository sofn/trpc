package com.github.sofn.trpc.server;

import com.github.sofn.trpc.core.IRegistry;
import com.github.sofn.trpc.server.config.ServerArgs;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 21:36.
 */
public class TrpcRegistryEventHandler implements TServerEventHandler {
    private ServerArgs serverArgs;

    public TrpcRegistryEventHandler(ServerArgs serverArgs) {
        this.serverArgs = serverArgs;
    }

    @Override
    public void preServe() {
        for (IRegistry registry : serverArgs.getRegistrys()) {
            registry.registry(serverArgs.getRegistryConfig());
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
