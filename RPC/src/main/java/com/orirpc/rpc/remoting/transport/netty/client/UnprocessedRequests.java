package com.orirpc.rpc.remoting.transport.netty.client;

import com.orirpc.rpc.remoting.dto.RPCResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 *未处理请求
 */
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RPCResponse<Object>>>UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RPCResponse<Object>> future){
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }
    public void complete(RPCResponse<Object> rpcResponse) {
        CompletableFuture<RPCResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
