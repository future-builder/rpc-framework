package cdu.mayong.serialize;

/**
 * 序列化接口，所有序列化类都要实现这个接口
 *
 * @author mayong
 */
public interface Serializer {
    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return byte[]字节数组
     */
    byte[] serialize(Object obj);


    /**
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 目标类
     * @param <T>   类的类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
