package com.orirpc.rpc.registry;


import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpccommon.extension.SPI;

import java.net.InetSocketAddress;


/**
 * 服务发现
 */
@SPI
public interface ServiceDiscovery {

    InetSocketAddress lookupService(RPCRequest rpcRequest);

}
