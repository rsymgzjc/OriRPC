package com.orirpc.rpc.remoting.transport.socket;

import com.orirpc.rpc.registry.ServiceDiscovery;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpc.remoting.transport.RPCRequestTransport;
import com.orirpc.rpccommon.enums.ServiceDiscoveryEnum;
import com.orirpc.rpccommon.exception.RPCException;
import com.orirpc.rpccommon.extension.ExtensionLoader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


@Slf4j
@AllArgsConstructor
public class SocketRPCClient implements RPCRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRPCClient(){
        this.serviceDiscovery= ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(ServiceDiscoveryEnum.ZK.getName());
    }
    @Override
    public Object sendRPCRequest(RPCRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try(Socket socket=new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RPCException("调用服务失败",e);
        }
    }
}
