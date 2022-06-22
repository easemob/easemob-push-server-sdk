package com.easemob.push.server.service;

import com.easemob.push.server.DnsConfigResponse;
import com.easemob.push.server.EMProperties;
import com.easemob.push.server.utils.ByteBufUtil;
import com.easemob.push.server.utils.Constant;
import com.easemob.push.server.utils.EMPushHttpClientFactory;
import com.easemob.push.server.utils.HttpResponseChecker;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class EMDomainProvider {

    private static final String DNS_HTTP_CLIENT_NAME = "DNS";
    private final EMProperties emProperties;
    private final HttpClient httpClient;
    private Mono<List<DnsConfigResponse.Endpoint>> endpoints;

    public EMDomainProvider(EMProperties emProperties) {
        this.emProperties = emProperties;
        this.httpClient = EMPushHttpClientFactory.create(emProperties, DNS_HTTP_CLIENT_NAME);

        this.endpoints = getDnsEndpoints()
                .cache(cache -> Duration.ofSeconds(3600),
                        error -> Duration.ofSeconds(10),
                        () -> Duration.ofSeconds(10)
                );
    }

    private Mono<List<DnsConfigResponse.Endpoint>> getDnsEndpoints() {

        return this.httpClient.baseUrl(Constant.DNS_BASE_URL).get()
                .uri(String.format(Constant.DNS_URI_PATTERN,
                        emProperties.getAppKeyUrlEncoded()))
                .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                .map(tuple2 -> {
                    HttpResponseChecker.check(tuple2.getT1(), tuple2.getT2());
                    return ByteBufUtil.decode(tuple2.getT2(), DnsConfigResponse.class);
                })
                .map(DnsConfigResponse::endpoints)
                .map(endpoints -> endpoints.stream().filter(e ->
                                this.emProperties.getProtocol().name().equalsIgnoreCase(e.getProtocol())
                        ).collect(Collectors.toList())
                );
    }

    public Mono<DnsConfigResponse.Endpoint> getEndpoint() {
        return endpoints.map(endpoints1 -> endpoints1.get(0));
    }
}
