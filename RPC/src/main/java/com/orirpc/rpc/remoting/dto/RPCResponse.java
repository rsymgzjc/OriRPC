package com.orirpc.rpc.remoting.dto;


import com.orirpc.rpccommon.enums.RPCResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RPCResponse<T> implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;
    /**
     * response code
     */
    private Integer code;
    /**
     * response message
     */
    private String message;
    /**
     * response body
     */
    private T data;

    public static <T> RPCResponse<T> success(T data, String requestId) {
        RPCResponse<T> response = new RPCResponse<>();
        response.setCode(RPCResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RPCResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RPCResponse<T> fail(RPCResponseCodeEnum rpcResponseCodeEnum) {
        RPCResponse<T> response = new RPCResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
