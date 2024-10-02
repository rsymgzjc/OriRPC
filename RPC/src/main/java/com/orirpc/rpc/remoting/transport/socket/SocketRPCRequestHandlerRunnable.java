package com.orirpc.rpc.remoting.transport.socket;

import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpc.remoting.dto.RPCResponse;
import com.orirpc.rpc.remoting.handler.RPCRequestHandler;
import com.orirpc.rpccommon.factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRPCRequestHandlerRunnable implements Runnable{

    private final Socket socket;
    private final RPCRequestHandler rpcRequestHandler;

    public SocketRPCRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RPCRequestHandler.class);
    }
    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RPCRequest rpcRequest = (RPCRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RPCResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }
}
