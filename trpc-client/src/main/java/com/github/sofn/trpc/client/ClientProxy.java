package com.github.sofn.trpc.client;

import com.github.sofn.trpc.core.AbstractMonitor;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.Data;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-19 16:05
 */
@Data
public class ClientProxy {
    private String localAppKey;
    private String remoteAppKey;
    private String hostPorts;
    private List<AbstractMonitor> monitors;

    @SuppressWarnings("unchecked")
    public static <T extends TServiceClient> TServiceClient client(Class<T> clazz) {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);

        try {
            T t = (T) factory.create(new Class[]{TProtocol.class}, new Object[]{null});
            ((Proxy) t).setHandler((self, thisMethod, proceed, args) -> {
                //TODO
                boolean success = false;
                try {
                    Object result = proceed.invoke(self, args);
                    success = true;
                    return result;
                } finally {
                    System.out.println("finally");
                }
            });
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
