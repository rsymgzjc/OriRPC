package com.orirpc.rpc.remoting.dto;


import lombok.*;

import java.io.Serializable;

@ToString
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RPCRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRPCServiceName(){
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
