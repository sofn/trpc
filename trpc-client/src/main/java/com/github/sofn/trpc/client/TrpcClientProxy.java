package com.github.sofn.trpc.client;

import com.github.sofn.trpc.client.client.AysncTrpcClient;
import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.factory.ClientFactory;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.exception.TRpcException;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:05
 */
@Setter
@Slf4j
public class TrpcClientProxy {
    private ClientArgs clientArgs;

    @SuppressWarnings("unchecked")
    public <T extends TServiceClient> TServiceClient client() {
        Class clazz;
        try {
            clazz = Class.forName(clientArgs.getServiceInterface() + "$Client");
        } catch (ClassNotFoundException e) {
            log.error("class not found: " + clientArgs.getServiceInterface() + "$Client");
            throw new TRpcException(e);
        }

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);

        try {
            T t = (T) factory.create(new Class[]{TProtocol.class}, new Object[]{null});
            ((Proxy) t).setHandler((self, thisMethod, proceed, args) -> {
                Pair<ThriftServerInfo, BlockTrpcClient> borrowClient = ClientFactory.getBlockClient(clientArgs);
                TServiceClient client = borrowClient.getValue().getClient(clazz);
                boolean success = false;
                long startTime = System.currentTimeMillis();
                try {
                    Object result = thisMethod.invoke(client, args);
                    success = true;
                    return result;
                } finally {
                    //将连接归还对象池
                    clientArgs.getPoolProvider().returnConnection(borrowClient.getKey(), borrowClient.getValue());
                    log.info("call " + thisMethod.getName() + " " + success + " time: " + (System.currentTimeMillis() - startTime));
                }
            });
            return t;
        } catch (Exception e) {
            log.error("proxy error", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends TAsyncClient> TAsyncClient asyncClient() {
        Class clazz;
        try {
            clazz = Class.forName(clientArgs.getServiceInterface() + "$AsyncClient");
        } catch (ClassNotFoundException e) {
            log.error("class not found: " + clientArgs.getServiceInterface() + "$AsyncClient");
            throw new TRpcException(e);
        }

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);

        try {
            T t = (T) factory.create(new Class[]{TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class}, new Object[]{null, null, null});
            ((Proxy) t).setHandler((self, thisMethod, proceed, args) -> {
                Pair<ThriftServerInfo, AysncTrpcClient> borrowClient = ClientFactory.getAsyncClient(clientArgs);
                TAsyncClient client = borrowClient.getValue().getClient(clazz);
                try {
                    if (args.length > 0 && args[args.length - 1] instanceof AsyncMethodCallback) {
                        //代理AsyncMethodCallback用于统计时间
                        args[args.length - 1] = java.lang.reflect.Proxy.newProxyInstance(AsyncMethodCallback.class.getClassLoader(),
                                new Class[]{AsyncMethodCallback.class},
                                new AsyncMethodCallbackProxy(args[args.length - 1]));
                    }
                    return thisMethod.invoke(client, args);
                } finally {
                    //将连接归还对象池
                    clientArgs.getPoolProvider().returnConnection(borrowClient.getKey(), borrowClient.getValue());
                }
            });
            return t;
        } catch (Exception e) {
            log.error("proxy error", e);
        }
        return null;
    }

    private class AsyncMethodCallbackProxy implements InvocationHandler {
        private Object proxied;
        private long startTime = System.currentTimeMillis();

        private AsyncMethodCallbackProxy(Object proxied) {
            this.proxied = proxied;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(proxied, args);
            log.info("call " + method.getName() + " time: " + (System.currentTimeMillis() - startTime));
            return result;
        }
    }
}
