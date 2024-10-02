package com.orirpc.rpc.config;


import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RPCServiceConfig {
    /**
     * service version
     */
    private String version = "";
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    public String getRPCServiceName(){
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
    public String getServiceName(){
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
