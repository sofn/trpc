package com.github.sofn.trpc.client.factory;

import com.github.sofn.trpc.client.client.AbstractTrpcClient;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-28 17:11
 */
public class ClientCluster {

    public static Pair<ThriftServerInfo, AbstractTrpcClient> getBlockClient(ClientArgs args) {
        //从负载均衡器里获取一个节点
        ServiceKey serviceKey = new ServiceKey(args.getRemoteAppKey(), args.getServiceInterface());
        TrpcServiceNode node = args.getLoadBalance().getNode(serviceKey, args);
        ThriftServerInfo serverInfo = node.toThriftServerInfo();
        AbstractTrpcClient trpcClient = args.getPoolProvider().getConnection(serverInfo);
        return Pair.of(serverInfo, trpcClient);
    }

}
