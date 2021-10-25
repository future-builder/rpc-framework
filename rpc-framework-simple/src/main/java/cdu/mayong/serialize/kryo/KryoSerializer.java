package cdu.mayong.serialize.kryo;

import cdu.mayong.remoting.dto.RpcRequest;
import cdu.mayong.remoting.dto.RpcResponse;
import cdu.mayong.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // what will resister() method do?
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        //try(资源申请){}自动关闭资源的Try语句来关闭这些IO流
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            //object -serialize-> byte[]
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializationException("Serialization failed.");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {

            Kryo kryo = kryoThreadLocal.get();
            Object object = kryo.readObject(input, clazz);
            return clazz.cast(object);
        } catch (IOException e) {
            throw new SerializationException("Deserialization failed.");
        }
    }
}
