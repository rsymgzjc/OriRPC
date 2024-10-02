package com.orirpc.rpc.registry.zk;

import com.orirpc.rpc.loadbalance.LoadBalance;
import com.orirpc.rpc.registry.ServiceDiscovery;
import com.orirpc.rpc.registry.zk.util.CuratorUtils;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpccommon.enums.LoadBalanceEnum;
import com.orirpc.rpccommon.enums.RPCErrorMessageEnum;
import com.orirpc.rpccommon.exception.RPCException;
import com.orirpc.rpccommon.extension.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;


@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(LoadBalanceEnum.LOADBALANCE.getName());
    }
    @Override
    public InetSocketAddress lookupService(RPCRequest rpcRequest) {
        String rpcServiceName=rpcRequest.getRPCServiceName();
        CuratorFramework zkClient= CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if(CollectionUtils.isEmpty(serviceUrlList)){
            throw new RPCException(RPCErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND,rpcServiceName);
        }
        //负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray=targetServiceUrl.split(":");
        String host=socketAddressArray[0];
        int port=Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);
    }
}
