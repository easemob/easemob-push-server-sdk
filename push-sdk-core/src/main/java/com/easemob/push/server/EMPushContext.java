package com.easemob.push.server;

import com.easemob.push.server.service.EMDomainProvider;
import com.easemob.push.server.service.EMTokenProvider;
import com.easemob.push.server.utils.EMPushHttpClientFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class EMPushContext {

    private static final String AUTHORIZATION = "Authorization";

    private final EMProperties emProperties;
    private final EMDomainProvider emDomainProvider;
    private final EMTokenProvider emTokenProvider;

    private HttpClient httpClient;

    public EMPushContext(EMProperties emProperties) {

        this.emProperties = emProperties;

        this.emDomainProvider = new EMDomainProvider(emProperties);
        this.emTokenProvider = new EMTokenProvider(emProperties, this.emDomainProvider);

        this.httpClient = EMPushHttpClientFactory.create(this.emProperties)
                .headersWhen(headers -> this.emTokenProvider.getToken()
                        .map(token -> headers.set(AUTHORIZATION, token))
                );
    }

    public Mono<HttpClient> getHttpClient() {
        return this.emDomainProvider.getEndpoint()
                .map(endpoint -> this.httpClient
                        .baseUrl(endpoint.getUri() + "/" + this.emProperties.getAppKeyUrlEncoded())
                );
    }
}
