package cdu.mayong.remoting;

import cdu.mayong.remoting.dto.RpcRequest;

// rpc传输通用接口
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest request);
}
