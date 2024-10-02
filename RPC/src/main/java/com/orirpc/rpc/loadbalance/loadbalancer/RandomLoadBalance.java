package com.orirpc.rpc.loadbalance.loadbalancer;

import com.orirpc.rpc.loadbalance.AbstractLoadBalance;
import com.orirpc.rpc.remoting.dto.RPCRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RPCRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
