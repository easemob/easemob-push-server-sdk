package com.easemob.push.common.connection;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.response.ResponseWrapper;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseHandler.class);
    private int status;
    private NettyHttpClient.BaseCallback callback;
    private CountDownLatch latch;
    private ResponseWrapper wrapper = new ResponseWrapper();

    public HttpResponseHandler(NettyHttpClient.BaseCallback callback, CountDownLatch latch) {
        this.callback = callback;
        this.latch = latch;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            status = response.status().code();
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            log.info(content.content().toString());

            String responseContent = content.content().toString(CharsetUtil.UTF_8);
            wrapper.responseCode = status;
            wrapper.responseContent = responseContent;
            if (null != latch) {
                latch.countDown();
            }
            if (null != callback) {
                callback.onSucceed(wrapper);
            }

            if (content instanceof LastHttpContent) {
                log.info("closing connection");
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("error:", cause);
        try {
            ctx.close();
            if (null != latch) {
                latch.countDown();
            }
        } catch (Exception ex) {
            log.error("close error:", ex);
        }
    }

    public void resetLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public ResponseWrapper getResponse() {
        return wrapper;
    }
}
