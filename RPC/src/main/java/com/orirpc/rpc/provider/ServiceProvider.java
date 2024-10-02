package com.orirpc.rpc.provider;

import com.orirpc.rpc.config.RPCServiceConfig;

public interface ServiceProvider {
    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(RPCServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(RPCServiceConfig rpcServiceConfig);
}
