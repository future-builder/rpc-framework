package cdu.mayong.server;

import cdu.mayong.config.RpcServiceConfig;
import cdu.mayong.remoting.transport.netty.server.NettyRpcServer;
import cdu.mayong.service.HelloService;
import cdu.mayong.service.HelloServiceImpl;

public class NettyServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("1").version("1.0").service(helloService).build();
        NettyRpcServer nettyRpcServer = new NettyRpcServer();

        //添加服务
        nettyRpcServer.addService(rpcServiceConfig);

        //启动服务器
        nettyRpcServer.start();
    }



}
