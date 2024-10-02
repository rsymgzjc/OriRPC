package com.orirpc.rpc.remoting.transport.netty.codec;

import com.orirpc.rpc.compress.Compress;
import com.orirpc.rpc.remoting.constants.RPCConstants;
import com.orirpc.rpc.remoting.dto.RPCMessage;
import com.orirpc.rpc.serialize.Serializer;
import com.orirpc.rpccommon.enums.CompressTypeEnum;
import com.orirpc.rpccommon.enums.SerializationTypeEnum;
import com.orirpc.rpccommon.extension.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * custom protocol decoder
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 */

@Slf4j
public class RPCMessageEncoder extends MessageToByteEncoder<RPCMessage> {

    private static final AtomicInteger ATOMIC_INTEGER=new AtomicInteger(0);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RPCMessage rpcMessage, ByteBuf byteBuf) {
        try {
            byteBuf.writeBytes(RPCConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RPCConstants.VERSION);
            byteBuf.writerIndex(byteBuf.writerIndex()+4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode());
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());

            //设置full length
            byte[] bodyBytes = null;
            int fullLength = RPCConstants.HEAD_LENGTH;
            if (messageType != RPCConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RPCConstants.HEARTBEAT_RESPONSE_TYPE) {

                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // compress the bytes
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (bodyBytes!=null){
                byteBuf.writeBytes(bodyBytes);
            }
            int writeIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(writeIndex - fullLength + RPCConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        }catch (Exception e) {
            log.error("Encode request error!", e);
        }
    }
}
