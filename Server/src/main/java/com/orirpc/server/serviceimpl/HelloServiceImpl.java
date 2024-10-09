package com.orirpc.server.serviceimpl;

import com.orirpc.helloserviceapi.Hello;
import com.orirpc.helloserviceapi.HelloService;
import com.orirpc.rpc.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RPCService(group = "test1",version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }
    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
