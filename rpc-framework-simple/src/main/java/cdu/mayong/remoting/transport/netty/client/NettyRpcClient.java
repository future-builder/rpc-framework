package cdu.mayong.remoting.transport.netty.client;

import cdu.mayong.enums.SerializationTypeEnum;
import cdu.mayong.factory.SingletonFactory;
import cdu.mayong.remoting.RpcRequestTransport;
import cdu.mayong.remoting.constants.RpcConstants;
import cdu.mayong.remoting.dto.RpcMessage;
import cdu.mayong.remoting.dto.RpcRequest;
import cdu.mayong.remoting.dto.RpcResponse;
import cdu.mayong.remoting.transport.netty.codec.RpcMessageDecoder;
import cdu.mayong.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final UnprocessedRequests unprocessedRequests;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ChannelProvider channelProvider;
    // todo inetSocketAddress被写死了
    private static final InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.47.1", 9998);


    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        p.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });

        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        ChannelFuture future = bootstrap.connect(inetSocketAddress);
        future.addListener((ChannelFutureListener)(f) -> {
            if(f.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(f.channel());
            }else{
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest request) {
        // CompletableFuture 接受请求结果
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        //todo inetSocketAddress被写死了
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()){
            //unprocessedRequests 单例，存入未被server处理的请求，在channelRead中通过一一对应的requestId拿到结果
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(request)
                    .codec(SerializationTypeEnum.KYRO.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();

            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) f -> {
               if(f.isSuccess()) {
                   log.info("client send message: [{}]", rpcMessage);
               } else {
                   f.channel().close();
                   resultFuture.completeExceptionally(f.cause());
                   log.error("Send failed:", f.cause());
               }
            });

        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if(channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
