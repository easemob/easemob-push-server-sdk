package com.easemob.push.utils;

import com.easemob.common.EMSDKVersion;
import com.easemob.http.AbstractHttpExecuter;
import com.easemob.http.HttpProxy;
import com.easemob.http.client.NativeHttpClient;
import com.easemob.http.client.ProxyBasicAuthNativeHttpClient;
import com.easemob.push.EMPushProperties;
import com.easemob.push.EMPushProxy;

import java.net.InetSocketAddress;
import java.net.Proxy;

public final class EMPushHttpClientFactory {

    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_FORMAT = "EasemobPushServerSDK/%s";

    public static AbstractHttpExecuter create(EMPushProperties properties) {

        NativeHttpClient httpClient = new ProxyBasicAuthNativeHttpClient();
        httpClient.defaultHeader(USER_AGENT,
                String.format(USER_AGENT_FORMAT, EMSDKVersion.getVersion()));
        httpClient.setConnectTimeout(properties.getConnectTimeout());
        httpClient.setReadTimeout(properties.getReadTimeout());

        EMPushProxy proxyInfo = properties.getEmProxy();

        if (proxyInfo == null || proxyInfo.getIp() == null) {
            return httpClient;
        }

        final String username = proxyInfo.getUsername();
        final String password = proxyInfo.getPassword();
        final String ip = proxyInfo.getIp();
        final int port = proxyInfo.getPort();

        HttpProxy httpProxy =
                new HttpProxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port), username, password);
        httpClient.setProxy(httpProxy);

        return httpClient;
    }
}
