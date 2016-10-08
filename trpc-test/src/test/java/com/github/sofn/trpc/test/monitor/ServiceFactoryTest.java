package com.github.sofn.trpc.test.monitor;

import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.client.monitor.ServiceFactory;
import com.github.sofn.trpc.core.config.ServiceArgs;
import com.github.sofn.trpc.core.utils.ClassNameUtils;
import com.github.sofn.trpc.demo.Hello;
import com.github.sofn.trpc.direct.HelloServer;
import com.github.sofn.trpc.registry.zk.ZkMonitor;
import com.github.sofn.trpc.registry.zk.ZkRegistry;
import com.github.sofn.trpc.server.config.ServerArgs;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-10-02 11:48
 */
public class ServiceFactoryTest {
    private String zkconnStr = "localhost:2181";
    private String appKey = "serviceFactoryTest";

    public ServerArgs getServerArgs(String appKey, String host, int port) {
        ServerArgs arg = ServerArgs.builder()
                .appkey(appKey)
                .port(port)
                .host(host)
                .weight(80)
                .service(new ServiceArgs(new Hello.Processor<>(new HelloServer()), ClassNameUtils.getClassName(Hello.class), 80, 100))
                .build();
        arg.afterPropertiesSet();
        return arg;
    }

    public ZkRegistry getZkRegistry() {
        ZkRegistry registry = new ZkRegistry();
        registry.setConnectString(zkconnStr);
        registry.setSessionTimeout(100);
        registry.setConnectionTimeout(1000);
        registry.initConnect();
        return registry;
    }

    @Test
    public void test() throws InterruptedException {
        ZkRegistry registry = getZkRegistry();
        ServerArgs arg = getServerArgs(appKey, "127.0.0.1", 8000);
        registry.registry(arg.getRegistryConfig());
        TimeUnit.MILLISECONDS.sleep(5);

        ZkMonitor zkMonitor = new ZkMonitor();
        zkMonitor.setConnectString(zkconnStr);
        Set<TrpcServiceNode> nodes = ServiceFactory.getServiceKeys(new ServiceKey(appKey, ClassNameUtils.getClassName(Hello.class)), zkMonitor);
        assertThat(nodes.size()).isEqualTo(1);
        nodes.forEach(System.out::println);

        //test add one
        ServerArgs arg2 = getServerArgs(appKey, "127.0.0.1", 8001);
        registry.registry(arg2.getRegistryConfig());
        TimeUnit.MILLISECONDS.sleep(10);

        nodes = ServiceFactory.getServiceKeys(new ServiceKey(appKey, ClassNameUtils.getClassName(Hello.class)), zkMonitor);
        assertThat(nodes.size()).isEqualTo(2);
        nodes.forEach(System.out::println);

        //test update
        arg2.setWeight(70);
        registry.modify(arg2.getRegistryConfig());
        TimeUnit.MILLISECONDS.sleep(10);
        nodes = ServiceFactory.getServiceKeys(new ServiceKey(appKey, ClassNameUtils.getClassName(Hello.class)), zkMonitor);
        assertThat(nodes.size()).isEqualTo(2);
        nodes.forEach(System.out::println);
        assertThat(Lists.newArrayList(nodes).get(1).getWeight()).isEqualTo(70);

        //test delete
        registry.unRegistry(appKey, arg2.getRegistryConfig().getServerInfo());
        TimeUnit.MILLISECONDS.sleep(10);
        nodes = ServiceFactory.getServiceKeys(new ServiceKey(appKey, ClassNameUtils.getClassName(Hello.class)), zkMonitor);
        assertThat(nodes.size()).isEqualTo(1);
        nodes.forEach(System.out::println);
    }

}
