package cdu.mayong.remoting.transport.netty.client;

import cdu.mayong.remoting.dto.RpcResponse;
import com.sun.xml.internal.ws.util.CompletedFuture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {
    public static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> completedFuture){
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, completedFuture);
    }

    public void complete(RpcResponse<Object> response){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if(null != future){
            future.complete(response);
        }else{
            throw new IllegalStateException();
        }
    }
}
