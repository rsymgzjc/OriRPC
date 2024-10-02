package com.orirpc.rpc.remoting.transport;

import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpccommon.extension.SPI;

/**
 * 发送RPC请求
 */

@SPI
public interface RPCRequestTransport {

    Object sendRPCRequest(RPCRequest rpcRequest);
}
