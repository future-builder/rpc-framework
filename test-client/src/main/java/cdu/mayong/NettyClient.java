package cdu.mayong;


import cdu.mayong.config.RpcServiceConfig;
import cdu.mayong.proxy.RpcClientProxy;
import cdu.mayong.remoting.RpcRequestTransport;
import cdu.mayong.remoting.transport.netty.client.NettyRpcClient;
import cdu.mayong.service.Hello;
import cdu.mayong.service.HelloService;

public class NettyClient {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig("1.0", "1", null);

        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        String hello = proxy.sayHello(new Hello("aaa", "bbb"));
        System.out.println(hello);
    }
}
