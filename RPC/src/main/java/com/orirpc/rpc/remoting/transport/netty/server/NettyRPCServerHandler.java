package com.orirpc.rpc.remoting.transport.netty.server;


import com.orirpc.rpc.remoting.constants.RPCConstants;
import com.orirpc.rpc.remoting.dto.RPCMessage;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpc.remoting.dto.RPCResponse;
import com.orirpc.rpc.remoting.handler.RPCRequestHandler;
import com.orirpc.rpccommon.enums.CompressTypeEnum;
import com.orirpc.rpccommon.enums.RPCResponseCodeEnum;
import com.orirpc.rpccommon.enums.SerializationTypeEnum;
import com.orirpc.rpccommon.factory.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRPCServerHandler extends ChannelInboundHandlerAdapter {
    private final RPCRequestHandler rpcRequestHandler;

    public NettyRPCServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RPCRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RPCMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RPCMessage) msg).getMessageType();
                RPCMessage rpcMessage = new RPCMessage();
                rpcMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                if (messageType == RPCConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RPCConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RPCConstants.PONG);
                } else {
                    RPCRequest rpcRequest = (RPCRequest) ((RPCMessage) msg).getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RPCConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RPCResponse<Object> rpcResponse = RPCResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RPCResponse<Object> rpcResponse = RPCResponse.fail(RPCResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }
}
