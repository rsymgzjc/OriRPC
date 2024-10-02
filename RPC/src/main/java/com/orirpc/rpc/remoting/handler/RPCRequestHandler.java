package com.orirpc.rpc.remoting.handler;


import com.orirpc.rpc.provider.ServiceProvider;
import com.orirpc.rpc.provider.impl.ZkServiceProviderImpl;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpccommon.exception.RPCException;
import com.orirpc.rpccommon.factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RPCRequestHandler {

    private final ServiceProvider serviceProvider;

    public RPCRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }
    public Object handle(RPCRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRPCServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod(RPCRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RPCException(e.getMessage(), e);
        }
        return result;
    }
}
