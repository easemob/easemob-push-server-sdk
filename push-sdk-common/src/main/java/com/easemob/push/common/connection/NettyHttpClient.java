package com.easemob.push.common.connection;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.StringUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpClient implements IHttpClient {

    private static Logger log = LoggerFactory.getLogger(NettyHttpClient.class);
    private final String encryptType;
    private String authCode;
    private int maxRetryTimes;
    private int readTimeout;
    private Channel channel;
    private Bootstrap b;
    private EventLoopGroup workerGroup;
    private SslContext sslContext;

    public NettyHttpClient(String authCode, HttpProxy proxy, ClientConfig config) {
        maxRetryTimes = config.getMaxRetryTimes();
        readTimeout = config.getReadTimeout();
        String message = MessageFormat.format("Created instance with "
                        + "connectionTimeout {0}, readTimeout {1}, maxRetryTimes {2}, SSL Version {3}",
                config.getConnectionTimeout(), readTimeout, maxRetryTimes,
                config.getSSLVersion());
        log.debug(message);
        this.authCode = authCode;
        encryptType = config.getEncryptType();
        try {
            sslContext =
                    SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build();
            workerGroup = new NioEventLoopGroup();
            b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(HttpMethod method, String content, URI uri, BaseCallback callback) {
        FullHttpRequest request;
        b = new Bootstrap();
        if (b.group() == null) {
            b.group(workerGroup);
        }
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new NettyClientInitializer(sslContext, callback, null));
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }
        channel = b.connect(uri.getHost(), port).syncUninterruptibly().channel();
        if (null != content) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8));
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                    byteBuf);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, (long) byteBuf.readableBytes());
        } else {
            request = new DefaultFullHttpRequest(HTTP_1_1, method, uri.getRawPath());
        }
        if (!StringUtils.isEmpty(encryptType)) {
            request.headers().set("X-Encrypt-Type", encryptType);
        }
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.AUTHORIZATION, authCode);
        request.headers().set("Content-Type", "application/json;charset=utf-8");

        log.info("Sending request. {}", request);
        log.info("Send body: {}", content);
        channel.writeAndFlush(request);
        try {
            channel.closeFuture().sync();
            workerGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ResponseWrapper sendGet(String url) throws APIConnectionException, APIRequestException {
        return sendGet(url, null);
    }

    public ResponseWrapper sendGet(String url, String content)
            throws APIConnectionException, APIRequestException {
        return sendHttpRequest(HttpMethod.GET, url, content);
    }

    @Override
    public ResponseWrapper sendPut(String url, String content)
            throws APIConnectionException, APIRequestException {
        return sendHttpRequest(HttpMethod.PUT, url, content);
    }

    @Override
    public ResponseWrapper sendPost(String url, String content)
            throws APIConnectionException, APIRequestException {
        return sendHttpRequest(HttpMethod.POST, url, content);
    }

    @Override
    public ResponseWrapper sendDelete(String url)
            throws APIConnectionException, APIRequestException {
        return sendDelete(url, null);
    }

    public ResponseWrapper sendDelete(String url, String content)
            throws APIConnectionException, APIRequestException {
        return sendHttpRequest(HttpMethod.DELETE, url, content);
    }

    private ResponseWrapper sendHttpRequest(HttpMethod method, String url, String body)
            throws APIConnectionException,
            APIRequestException {
        CountDownLatch latch = new CountDownLatch(1);
        NettyClientInitializer initializer = new NettyClientInitializer(sslContext, null, latch);
        b.handler(initializer);
        ResponseWrapper wrapper = new ResponseWrapper();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            log.debug(IO_ERROR_MESSAGE, e1);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e1, true);
        }
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        try {
            ChannelFuture connect = b.connect(host, port);
            channel = connect.sync().channel();
            FullHttpRequest request;
            if (null != body) {
                ByteBuf byteBuf = Unpooled.copiedBuffer(body.getBytes(CharsetUtil.UTF_8));
                request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                        byteBuf);
                request.headers()
                        .set(HttpHeaderNames.CONTENT_LENGTH, (long) byteBuf.readableBytes());
            } else {
                request = new DefaultFullHttpRequest(HTTP_1_1, method, uri.getRawPath());
            }
            if (!StringUtils.isEmpty(encryptType)) {
                request.headers().set("X-Encrypt-Type", encryptType);
            }
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.AUTHORIZATION, authCode);
            request.headers().set("Content-Type", "application/json;charset=utf-8");
            connect.awaitUninterruptibly();
            log.info("Sending request. {}", request);
            log.info("Send body: {}", body);
            channel.writeAndFlush(request);
            latch.await();
            wrapper = initializer.getResponse();
            int status = wrapper.responseCode;
            String responseContent = wrapper.responseContent;
            if (status >= 200 && status < 300) {
                log.debug("Succeed to get response OK - responseCode: {}", status);
                log.debug("Response Content - {}", responseContent);

            } else if (status >= 300 && status < 400) {
                log.warn("Normal response but unexpected - responseCode: {}, responseContent: {}",
                        status, responseContent);

            } else {
                log.warn("Got error response - responseCode: {}, responseContent: {}",
                        status, responseContent);

                switch (status) {
                    case 400:
                        log.error(
                                "Your request params is invalid. Please check them according to error message.");
                        wrapper.setErrorObject();
                        break;
                    case 401:
                        log.error(
                                "Authentication failed! Please check authentication params according to docs.");
                        wrapper.setErrorObject();
                        break;
                    case 403:
                        log.error(
                                "Request is forbidden! Maybe your appkey is listed in blacklist or your params is invalid.");
                        wrapper.setErrorObject();
                        break;
                    case 404:
                        log.error("Request page is not found! Maybe your params is invalid.");
                        wrapper.setErrorObject();
                        break;
                    case 410:
                        log.error(
                                "Request resource is no longer in service. Please according to notice on official website.");
                        wrapper.setErrorObject();
                    case 429:
                        log.error("Too many requests! Please review your appkey's request quota.");
                        wrapper.setErrorObject();
                        break;
                    case 500:
                    case 502:
                    case 503:
                    case 504:
                        log.error(
                                "Seems encountered server error. Maybe EPush is in maintenance? Please retry later.");
                        break;
                    default:
                        log.error("Unexpected response.");
                }
                throw new APIRequestException(wrapper);
            }
        } catch (InterruptedException e) {
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        }
        return wrapper;
    }

    public void send(ByteBuf body, HttpMethod method, URI uri) {
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }
        channel = b.connect(host, port).syncUninterruptibly().channel();
        HttpRequest request;
        if (null != body) {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri.getRawPath(),
                    body);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, (long) body.readableBytes());
        } else {
            request = new DefaultFullHttpRequest(HTTP_1_1, method, uri.getRawPath());
        }
        request.headers().set(HttpHeaderNames.HOST, uri.getHost());
        request.headers().set(HttpHeaderNames.AUTHORIZATION, authCode);
        request.headers().set("Content-Type", "application/json;charset=utf-8");
        log.info("Sending request. {}", request);
        log.info("Send body: {}", body);
        channel.writeAndFlush(request);
    }

    public void close() {
        if (null != channel) {
            channel.closeFuture().syncUninterruptibly();
            workerGroup.shutdownGracefully();
            channel = null;
            workerGroup = null;
        }
        log.info("Finished request(s)");
    }

    public interface BaseCallback {
        public void onSucceed(ResponseWrapper wrapper);
    }
}
