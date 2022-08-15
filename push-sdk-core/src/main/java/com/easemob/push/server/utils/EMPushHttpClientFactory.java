package com.easemob.push.server.utils;

import com.easemob.push.server.EMProperties;
import com.easemob.push.server.EMProxy;
import com.easemob.push.server.EMPushVersion;
import io.netty.handler.logging.LogLevel;
import org.apache.logging.log4j.util.Strings;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.net.InetSocketAddress;
import java.time.Duration;

import static reactor.netty.resources.ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT;

public class EMPushHttpClientFactory {

    private static final String CLIENT_NAME = "easemob-push-sdk";
    private static final String CATEGORY = "com.easemob.push.http";
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_FORMAT = "EasemobPushServerSDK/%s";

    public static HttpClient create(EMProperties properties) {
        return create(properties, CLIENT_NAME);
    }

    public static HttpClient create(EMProperties properties, String name) {

        ConnectionProvider connectionProvider = ConnectionProvider.builder(name)
                .maxConnections(properties.getHttpConnectionPoolSize())
                .maxIdleTime(Duration.ofMillis(properties.getHttpConnectionMaxIdleTime()))
                .pendingAcquireTimeout(Duration.ofMillis(DEFAULT_POOL_ACQUIRE_TIMEOUT))
                .lifo()
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .headers(headers -> headers.add(USER_AGENT,
                        String.format(USER_AGENT_FORMAT, EMPushVersion.getVersion())))
                .wiretap(CATEGORY, LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        EMProxy proxyInfo = properties.getEmProxy();

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
