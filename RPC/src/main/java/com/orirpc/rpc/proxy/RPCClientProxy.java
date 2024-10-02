package com.orirpc.rpc.proxy;

import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpc.remoting.dto.RPCResponse;
import com.orirpc.rpc.remoting.transport.RPCRequestTransport;
import com.orirpc.rpc.remoting.transport.netty.client.NettyRPCClient;
import com.orirpc.rpc.remoting.transport.socket.SocketRPCClient;
import com.orirpc.rpccommon.enums.RPCErrorMessageEnum;
import com.orirpc.rpccommon.enums.RPCResponseCodeEnum;
import com.orirpc.rpccommon.exception.RPCException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class RPCClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME="interfaceName";

    private final RPCRequestTransport rpcRequestTransport;

    private final RPCServiceConfig rpcServiceConfig;

    public RPCClientProxy(RPCRequestTransport rpcRequestTransport, RPCServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public RPCClientProxy(RPCRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RPCServiceConfig();
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        RPCRequest rpcRequest = RPCRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RPCResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRPCClient) {
            CompletableFuture<RPCResponse<Object>> completableFuture = (CompletableFuture<RPCResponse<Object>>) rpcRequestTransport.sendRPCRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        if (rpcRequestTransport instanceof SocketRPCClient) {
            rpcResponse = (RPCResponse<Object>) rpcRequestTransport.sendRPCRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RPCResponse<Object> rpcResponse, RPCRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RPCException(RPCErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RPCException(RPCErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RPCResponseCodeEnum.SUCCESS.getCode())) {
            throw new RPCException(RPCErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
