package com.orirpc.rpc.spring;

import com.orirpc.rpc.annotation.RPCReference;
import com.orirpc.rpc.annotation.RPCService;
import com.orirpc.rpc.config.RPCServiceConfig;
import com.orirpc.rpc.provider.ServiceProvider;
import com.orirpc.rpc.provider.impl.ZkServiceProviderImpl;
import com.orirpc.rpc.proxy.RPCClientProxy;
import com.orirpc.rpc.remoting.transport.RPCRequestTransport;
import com.orirpc.rpccommon.enums.RPCRequestTransportEnum;
import com.orirpc.rpccommon.extension.ExtensionLoader;
import com.orirpc.rpccommon.factory.SingletonFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RPCRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RPCRequestTransport.class).getExtension(RPCRequestTransportEnum.NETTY.getName());
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RPCService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RPCService.class.getCanonicalName());
            // get RpcService annotation
            RPCService rpcService = bean.getClass().getAnnotation(RPCService.class);
            // build RpcServiceProperties
            RPCServiceConfig rpcServiceConfig = RPCServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RPCReference rpcReference = declaredField.getAnnotation(RPCReference.class);
            if (rpcReference != null) {
                RPCServiceConfig rpcServiceConfig = RPCServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
