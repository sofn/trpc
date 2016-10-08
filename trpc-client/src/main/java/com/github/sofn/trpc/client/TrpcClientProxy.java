package com.github.sofn.trpc.client;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.factory.ClientCluster;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import com.github.sofn.trpc.core.exception.TRpcException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.async.AsyncMethodCallback;
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
    public <T> T client() {
        Class clazz;
        String className = clientArgs.getServiceInterface() + (clientArgs.isAsync() ? "$AsyncClient" : "$Client");
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("class not found: " + className);
            throw new TRpcException(e);
        }

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);

        try {
            T t;
            if (clientArgs.isAsync()) {
                t = (T) factory.create(new Class[]{TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class}, new Object[]{null, null, null});
            } else {
                t = (T) factory.create(new Class[]{TProtocol.class}, new Object[]{null});
            }
            if (this.clientArgs.isAsync()) {
                ((Proxy) t).setHandler(asyncHandler(clazz));
            } else {
                ((Proxy) t).setHandler(blockHandler(clazz));
            }
            return t;
        } catch (Exception e) {
            log.error("proxy error", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private MethodHandler blockHandler(final Class clazz) {
        return (o, method, method1, args) -> {
            Pair<ThriftServerInfo, AbstractTrpcClient> borrowClient = ClientCluster.getBlockClient(clientArgs);
            Object client = borrowClient.getValue().getClient(clazz);
            boolean success = false;
            long startTime = System.currentTimeMillis();
            try {
                Object result = method.invoke(client, args);
                success = true;
                //将连接归还对象池
                clientArgs.getPoolProvider().returnConnection(borrowClient.getKey(), borrowClient.getValue());
                return result;
            } catch (Exception e) {
                log.error("call rpc error", e);
                clientArgs.getPoolProvider().returnBrokenConnection(borrowClient.getKey(), borrowClient.getValue());
                throw e;
            } finally {
                log.info("call " + method.getName() + " " + success + " time: " + (System.currentTimeMillis() - startTime));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private MethodHandler asyncHandler(final Class clazz) {
        return (o, method, method1, args) -> {
            Pair<ThriftServerInfo, AbstractTrpcClient> borrowClient = ClientCluster.getBlockClient(clientArgs);
            Object client = borrowClient.getValue().getClient(clazz);
            try {
                if (args.length > 0 && args[args.length - 1] instanceof AsyncMethodCallback) {
                    //代理AsyncMethodCallback用于统计时间
                    args[args.length - 1] = java.lang.reflect.Proxy.newProxyInstance(AsyncMethodCallback.class.getClassLoader(),
                            new Class[]{AsyncMethodCallback.class},
                            new AsyncMethodCallbackProxy(args[args.length - 1], clientArgs, borrowClient));
                }
                return method.invoke(client, args);
            } catch (Exception e) {
                log.error("call rpc error", e);
                throw e;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private class AsyncMethodCallbackProxy implements InvocationHandler {
        private Object proxied;
        private long startTime = System.currentTimeMillis();
        private ClientArgs clientArgs;
        private Pair<ThriftServerInfo, AbstractTrpcClient> borrowClient;

        private AsyncMethodCallbackProxy(Object proxied, ClientArgs clientArgs, Pair<ThriftServerInfo, AbstractTrpcClient> borrowClient) {
            this.proxied = proxied;
            this.clientArgs = clientArgs;
            this.borrowClient = borrowClient;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            try {
                result = method.invoke(proxied, args);
                this.clientArgs.getPoolProvider().returnConnection(this.borrowClient.getKey(), this.borrowClient.getValue());
            } catch (Exception e) {
                this.clientArgs.getPoolProvider().returnBrokenConnection(this.borrowClient.getKey(), this.borrowClient.getValue());
                log.error("async rpc error", e);
                throw e;
            }
            log.info("call " + method.getName() + " time: " + (System.currentTimeMillis() - startTime));
            return result;
        }
    }
}
