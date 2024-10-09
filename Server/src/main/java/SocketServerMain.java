import com.orirpc.helloserviceapi.HelloService;
import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.remoting.transport.socket.SocketRPCServer;
import com.orirpc.server.serviceimpl.HelloServiceImpl;

public class SocketServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRPCServer socketRpcServer = new SocketRPCServer();
        RPCServiceConfig rpcServiceConfig = new RPCServiceConfig();
        rpcServiceConfig.setService(helloService);
        socketRpcServer.registerService(rpcServiceConfig);
        socketRpcServer.start();
    }
}
