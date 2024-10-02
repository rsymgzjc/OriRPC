package com.orirpc.rpc.remoting.transport.socket;


import com.orirpc.rpc.config.CustomShutdownHook;
import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.provider.ServiceProvider;
import com.orirpc.rpc.provider.impl.ZkServiceProviderImpl;
import com.orirpc.rpccommon.factory.SingletonFactory;
import com.orirpc.rpccommon.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static com.orirpc.rpc.remoting.transport.netty.server.NettyRPCServer.PORT;

@Slf4j
public class SocketRPCServer {
    private final ExecutorService threadPool;

    private final ServiceProvider serviceProvider;

    public SocketRPCServer(){
        threadPool= ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public void registerService(RPCServiceConfig rpcServiceConfig){
        serviceProvider.publishService(rpcServiceConfig);
    }
    public void start(){
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRPCRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }
}
