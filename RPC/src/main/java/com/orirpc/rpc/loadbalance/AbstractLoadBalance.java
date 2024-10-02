package com.orirpc.rpc.loadbalance;

import com.orirpc.rpc.remoting.dto.RPCRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RPCRequest rpcRequest){
        if(CollectionUtils.isEmpty(serviceAddresses)){
            return null;
        }
        if(serviceAddresses.size()==1){
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses,rpcRequest);
    }
    protected abstract String doSelect(List<String> serviceAddresses, RPCRequest rpcRequest);
}
