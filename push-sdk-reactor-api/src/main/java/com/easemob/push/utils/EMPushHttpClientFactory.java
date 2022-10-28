package com.easemob.push.utils;

import com.easemob.common.EMSDKVersion;
import com.easemob.push.EMPushProperties;
import com.easemob.push.EMPushProxy;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.util.Strings;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;
import java.time.Duration;

public final class EMPushHttpClientFactory {

    private static final String CLIENT_NAME = "easemob-push-sdk";
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_FORMAT = "EasemobPushServerSDK/%s";

    public static HttpClient create(EMPushProperties properties) {
        return create(properties, CLIENT_NAME);
    }

    public static HttpClient create(EMPushProperties properties, String name) {

        ConnectionProvider connectionProvider = ConnectionProvider.builder(name)
                .maxConnections(properties.getHttpConnectionPoolSize())
                .maxIdleTime(Duration.ofMillis(properties.getHttpConnectionMaxIdleTime()))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .headers(headers -> headers.add(USER_AGENT,
                        String.format(USER_AGENT_FORMAT, EMSDKVersion.getVersion())))
                .runOn(new NioEventLoopGroup(properties.getHttpConnectionPoolSize() * 2 + 1));

        EMPushProxy proxyInfo = properties.getEmProxy();

        if (proxyInfo == null || proxyInfo.getIp() == null) {
            return httpClient;
        }

        final String username = proxyInfo.getUsername();
        final String password = proxyInfo.getPassword();
        final String ip = proxyInfo.getIp();
        final int port = proxyInfo.getPort();

        if (Strings.isNotBlank(username) && Strings.isNotBlank(password)) {
            return httpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                    .address(new InetSocketAddress(ip, port))
                    .username(username)
                    .password(p -> password)
            );
        }

        return httpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                .address(new InetSocketAddress(ip, port))
        );
    }
}
