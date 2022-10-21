package com.easemob.push.service.impl;

import com.easemob.common.Constant;
import com.easemob.common.exception.EMException;
import com.easemob.common.model.DnsConfigResponse;
import com.easemob.common.model.Endpoint;
import com.easemob.common.util.BytesUtil;
import com.easemob.http.AbstractHttpExecuter;
import com.easemob.http.HttpRequest;
import com.easemob.http.Method;
import com.easemob.push.EMPushProperties;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.service.DomainProvider;
import com.easemob.push.utils.EMPushHttpClientFactory;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class EMDomainProvider implements DomainProvider {

    private final SecureRandom secureRandom = new SecureRandom();

    private final EMPushProperties emPushProperties;
    private final AbstractHttpExecuter httpClient;

    private List<Endpoint> endpoints;
    private long expireAt;

    public EMDomainProvider(EMPushProperties emPushProperties) {
        this.emPushProperties = emPushProperties;
        this.httpClient = EMPushHttpClientFactory.create(emPushProperties);
    }

    private void refreshDnsEndpoints() {

        HttpRequest httpRequest = HttpRequest.Builder()
                .url(Constant.DNS_BASE_URL)
                .urlParam("app_key", emPushProperties.getAppKey())
                .method(Method.GET)
                .build();

        EMPushHttpResponse execute = httpClient.execute(httpRequest);

        if (execute.getStatus() != 200) {
            throw new EMException("refresh endpoints failed " + execute.getBody());
        }

        DnsConfigResponse dnsConfigResponse =
                BytesUtil.decode(execute.getBytes(), DnsConfigResponse.class);

        this.endpoints = dnsConfigResponse.endpoints().stream()
                .filter(endpoint -> this.emPushProperties.getProtocol().name()
                        .equalsIgnoreCase(endpoint.getProtocol()))
                .collect(Collectors.toList());

        this.expireAt = System.currentTimeMillis() + 3600000;
    }

    @Override
    public Endpoint getEndpoint() {

        if (emPushProperties.getHosts() != null && !emPushProperties.getHosts().isEmpty()) {
            return emPushProperties.getHosts()
                    .get(secureRandom.nextInt(emPushProperties.getHosts().size()));
        }

        if (expireAt < System.currentTimeMillis()) {
            refreshDnsEndpoints();
        }

        if (endpoints == null || endpoints.isEmpty()) {
            throw new EMException("no endpoint for http/https");
        }

        return endpoints.get(secureRandom.nextInt(endpoints.size()));
    }

}
