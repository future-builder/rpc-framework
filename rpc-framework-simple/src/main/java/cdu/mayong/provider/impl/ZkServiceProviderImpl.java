package cdu.mayong.provider.impl;


import cdu.mayong.config.RpcServiceConfig;
import cdu.mayong.enums.RpcErrorMessageEnum;
import cdu.mayong.exception.RpcException;
import cdu.mayong.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shuang.kou
 * @createTime 2020年05月13日 11:23:00
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    /**
     * key: rpc service name(interface name + version + group)
     * value: service object
     */
    private final Map<String, Object> serviceMap;   //存储服务 ：服务名，客户端target对象
    private final Set<String> registeredService;    //已注册服务名集合

    public ZkServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        log.info("serviceMap中加入服务：【{}】", rpcServiceConfig.getService());
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        log.error(String.valueOf(serviceMap));

        Object service = serviceMap.get(rpcServiceName);

        if (null == service) {
            log.error("没有找到服务：【{}】", rpcServiceName);
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }


    //todo zookeeper服务发布
    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {

    }


}
