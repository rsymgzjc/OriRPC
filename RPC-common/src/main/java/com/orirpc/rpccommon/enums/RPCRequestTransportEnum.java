package com.orirpc.rpccommon.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RPCRequestTransportEnum {

    NETTY("netty"),
    SOCKET("socket");

    private final String name;

}
