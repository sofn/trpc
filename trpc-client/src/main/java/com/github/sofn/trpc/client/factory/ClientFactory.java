package com.github.sofn.trpc.client.factory;

import com.github.sofn.trpc.client.client.AysncTrpcClient;
import com.github.sofn.trpc.client.client.BlockTrpcClient;
import com.github.sofn.trpc.client.config.ClientArgs;
import com.github.sofn.trpc.client.config.ServiceKey;
import com.github.sofn.trpc.client.config.TrpcServiceNode;
import com.github.sofn.trpc.client.pool.impl.AsyncTrpcClientPoolImpl;
import com.github.sofn.trpc.core.config.ThriftServerInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.TAsyncClient;

/**
 * @author sofn
 * @version 1.0 Created at: 2016-09-28 17:11
 */
public class ClientFactory {

    public static Pair<ThriftServerInfo, BlockTrpcClient> getBlockClient(ClientArgs args) {
        //从负载均衡器里获取一个节点
        ServiceKey serviceKey = new ServiceKey(args.getRemoteAppKey(), args.getServiceInterface());
        TrpcServiceNode node = args.getLoadBalance().getNode(serviceKey, args);
        ThriftServerInfo serverInfo = node.toThriftServerInfo();
        BlockTrpcClient trpcClient = (BlockTrpcClient) args.getPoolProvider().getConnection(serverInfo);
        return Pair.of(serverInfo, trpcClient);
    }

    public static Pair<ThriftServerInfo, AysncTrpcClient> getAsyncClient(ClientArgs args) {
        //从负载均衡器里获取一个节点
        TrpcServiceNode node = args.getLoadBalance().getNode(new ServiceKey(args.getRemoteAppKey(), args.getServiceInterface()), args);
        AsyncTrpcClientPoolImpl clientPool = AsyncTrpcClientPoolImpl.getInstance();
        ThriftServerInfo serverInfo = node.toThriftServerInfo();
        AysncTrpcClient trpcClient = clientPool.getConnection(serverInfo);
        return Pair.of(serverInfo, trpcClient);
    }
}
