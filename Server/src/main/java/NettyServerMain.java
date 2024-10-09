import com.orirpc.helloserviceapi.HelloService;
import com.orirpc.rpc.annotation.RPCScan;
import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.remoting.transport.netty.server.NettyRPCServer;
import com.orirpc.server.serviceimpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RPCScan(basePackage = {"com.orirpc"})
public class NettyServerMain {
    public static void main(String[] args) {
        //注解注册服务
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRPCServer nettyRpcServer = (NettyRPCServer) annotationConfigApplicationContext.getBean("nettyRPCServer");
        //手动注册服务
        HelloService helloService2 = new HelloServiceImpl2();
        RPCServiceConfig rpcServiceConfig = RPCServiceConfig.builder()
                .group("test2").version("version2").service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
