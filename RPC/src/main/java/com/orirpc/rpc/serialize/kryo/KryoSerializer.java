package com.orirpc.rpc.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.orirpc.rpc.remoting.dto.RPCRequest;
import com.orirpc.rpc.remoting.dto.RPCResponse;
import com.orirpc.rpc.serialize.Serializer;
import com.orirpc.rpccommon.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Slf4j
public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        kryo.register(RPCResponse.class);
        kryo.register(RPCRequest.class);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("Serialization failed", e);
            throw new SerializeException("Serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // byte->Object:从byte数组中反序列化出对对象
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            log.error("Deserialization failed", e);
            throw new SerializeException("Deserialization failed", e);
        }
    }
}
