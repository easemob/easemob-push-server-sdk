package com.easemob.push.common.connection;

import java.util.concurrent.CountDownLatch;

import com.easemob.push.common.response.ResponseWrapper;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContext;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx;
    private NettyHttpClient.BaseCallback callback;
    private CountDownLatch latch;
    private HttpResponseHandler handler;

    public NettyClientInitializer(SslContext sslContext, NettyHttpClient.BaseCallback callback,
            CountDownLatch latch) {
        this.sslCtx = sslContext;
        this.callback = callback;
        this.latch = latch;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        this.handler = new HttpResponseHandler(callback, latch);
        socketChannel.pipeline()
                .addLast(sslCtx.newHandler(socketChannel.alloc()), new HttpClientCodec(),
                        handler);
    }

    public void resetLatch(CountDownLatch latch) {
        handler.resetLatch(latch);
    }

    public ResponseWrapper getResponse() {
        return handler.getResponse();
    }
}
