package cdu.mayong.remoting.transport.netty.server;

import cdu.mayong.factory.SingletonFactory;
import cdu.mayong.provider.ServiceProvider;
import cdu.mayong.provider.impl.ZkServiceProviderImpl;
import cdu.mayong.remoting.dto.RpcRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    //serviceProvider 是一个存放serviceImpl的map集合，从中拿到serviceImpl，
    // 反射执行方法需要：1.getMethod（方法名，参数列表）
    //               2. method.invoke(obj，方法参数) obj是一个具体的实现该接口的对象
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider =  SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    @SneakyThrows
    public Object handle(RpcRequest rpcRequest) {
        log.debug(rpcRequest.getRpcServiceName());
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        Object o = invokeTargetMethod(rpcRequest, service);
        return o;

    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        // 注意service是什么
        Object result = null;
        try {
            log.info("method name: [{}]", rpcRequest.getMethodName());
            // 反射获取对应接口的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            log.info("start invoke method:");
            // 反射执行方法
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
