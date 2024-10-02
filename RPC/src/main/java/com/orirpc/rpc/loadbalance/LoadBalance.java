package com.orirpc.rpc.loadbalance;

import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpccommon.extension.SPI;

import java.util.List;


/**
 * 负载均衡策略
 */
@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceUrlList, RPCRequest rpcRequest);
}
