package com.orirpc.client;

import com.orirpc.helloserviceapi.Hello;
import com.orirpc.helloserviceapi.HelloService;
import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.proxy.RPCClientProxy;
import com.orirpc.rpc.remoting.transport.RPCRequestTransport;
import com.orirpc.rpc.remoting.transport.socket.SocketRPCClient;

public class SocketClientMain {
    public static void main(String[] args) {
        RPCRequestTransport rpcRequestTransport = new SocketRPCClient();
        RPCServiceConfig rpcServiceConfig = new RPCServiceConfig();
        RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
