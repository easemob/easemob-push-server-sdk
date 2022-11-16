package com.easemob.reactor.push;

import com.easemob.reactor.push.service.DomainProvider;
import com.easemob.reactor.push.service.TokenProvider;
import com.easemob.reactor.push.service.impl.EMDomainProvider;
import com.easemob.reactor.push.service.impl.EMTokenProvider;
import com.easemob.reactor.push.utils.EMPushHttpClientFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class EMPushContext {

    private static final String AUTHORIZATION = "Authorization";

    private final EMPushProperties emPushProperties;
    private final DomainProvider emDomainProvider;
    private final TokenProvider emTokenProvider;

    private HttpClient httpClient;

    public EMPushContext(EMPushProperties emPushProperties) {

        this.emPushProperties = emPushProperties;

        this.emDomainProvider = new EMDomainProvider(emPushProperties);
        this.emTokenProvider = new EMTokenProvider(emPushProperties, this.emDomainProvider);

        this.httpClient = EMPushHttpClientFactory.create(this.emPushProperties)
                .headersWhen(headers -> this.emTokenProvider.getToken()
                        .map(token -> headers.set(AUTHORIZATION, token))
                );
    }

    public Mono<HttpClient> getHttpClient() {
        return this.emDomainProvider.getEndpoint()
                .map(endpoint -> this.httpClient
                        .baseUrl(endpoint.getUri() + "/" + this.emPushProperties.getAppKeyUrlEncoded())
                );
    }
}
