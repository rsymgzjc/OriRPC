package com.orirpc.client;


import com.orirpc.rpc.annotation.RPCScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RPCScan(basePackage = {"com.orirpc"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
