package com.easemob.push.service.impl;

import com.easemob.common.Constant;
import com.easemob.common.exception.EMException;
import com.easemob.common.model.DnsConfigResponse;
import com.easemob.common.model.Endpoint;
import com.easemob.push.EMProperties;
import com.easemob.push.service.DomainProvider;
import com.easemob.push.utils.ByteBufUtil;
import com.easemob.push.utils.EMPushHttpClientFactory;
import com.easemob.push.utils.HttpResponseChecker;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EMDomainProvider implements DomainProvider {
    private final SecureRandom secureRandom = new SecureRandom();
    private static final String DNS_HTTP_CLIENT_NAME = "DNS";
    private final EMProperties emProperties;
    private final HttpClient httpClient;

    private Mono<List<Endpoint>> endpoints;

    public EMDomainProvider(EMProperties emProperties) {
        this.emProperties = emProperties;
        this.httpClient = EMPushHttpClientFactory.create(emProperties, DNS_HTTP_CLIENT_NAME);

        this.endpoints = getDnsEndpoints()
                .cache(cache -> Duration.ofSeconds(3600),
                        error -> Duration.ofSeconds(10),
                        () -> Duration.ofSeconds(10)
                );
    }

    private Mono<List<Endpoint>> getDnsEndpoints() {

        List<Endpoint> hosts = emProperties.getHosts();

        if (hosts != null && !hosts.isEmpty()) {
            return Mono.just(hosts);
        }

        return this.httpClient.baseUrl(Constant.DNS_BASE_URL).get()
                .uri(String.format(Constant.DNS_URI_PATTERN,
                        emProperties.getAppKeyUrlEncoded()))
                .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                .map(tuple2 -> {
                    HttpResponseChecker.check(tuple2.getT1(), tuple2.getT2());
                    return ByteBufUtil.decode(tuple2.getT2(), DnsConfigResponse.class);
                })
                .map(DnsConfigResponse::endpoints)
                .map(this::filterEndpoint);
    }

    @Override
    public Mono<Endpoint> getEndpoint() {
        return endpoints.map(list -> {
            if (list == null || list.isEmpty()) {
                throw new EMException("no endpoint for http/https");
            }
            return list.get(secureRandom.nextInt(list.size()));
        });
    }

    private List<Endpoint> filterEndpoint(List<Endpoint> list) {

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().filter(endpoint ->
                this.emProperties.getProtocol().name().equalsIgnoreCase(endpoint.getProtocol())
        ).collect(Collectors.toList());
    }
}
