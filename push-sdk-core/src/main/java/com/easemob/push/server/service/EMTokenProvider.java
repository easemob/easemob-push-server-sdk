package com.easemob.push.server.service;

import com.easemob.push.server.EMProperties;
import com.easemob.push.server.model.AppTokenRequest;
import com.easemob.push.server.model.AppTokenResponse;
import com.easemob.push.server.model.EMAppToken;
import com.easemob.push.server.utils.ByteBufUtil;
import com.easemob.push.server.utils.Constant;
import com.easemob.push.server.utils.EMPushHttpClientFactory;
import com.easemob.push.server.utils.HttpResponseChecker;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.Instant;


public class EMTokenProvider implements TokenProvider {

    private static final String TOKEN_HTTP_CLIENT_NAME = "TOKEN";
    private final EMProperties emProperties;
    private final AppTokenRequest appTokenRequest;
    private final EMDomainProvider emDomainProvider;
    private final HttpClient httpClient;

    private Mono<EMAppToken> appToken;

    public EMTokenProvider(EMProperties emProperties, EMDomainProvider emDomainProvider) {

        this.emProperties = emProperties;
        this.httpClient = EMPushHttpClientFactory.create(emProperties, TOKEN_HTTP_CLIENT_NAME);
        this.appTokenRequest = AppTokenRequest.of(this.emProperties.getCredentials().getId(),
                this.emProperties.getCredentials().getSecret());

        this.emDomainProvider = emDomainProvider;

        this.appToken = getAppToken()
                .cache(cache -> Duration.between(Instant.now(),
                                Instant.ofEpochMilli(cache.getExpireTimestamp())
                        ).dividedBy(2),
                        error -> Duration.ofSeconds(10),
                        () -> Duration.ofSeconds(10)
                );
    }

    private Mono<EMAppToken> getAppToken() {

        return emDomainProvider.getEndpoint()
                .flatMap(endpoint -> this.httpClient.baseUrl(endpoint.getUri()).post()
                        .uri(String.format(Constant.TOKEN_URI_PATTERN,
                                emProperties.getAppKeyUrlEncoded()))
                        .send(Mono.create(byteBufMonoSink -> {
                            byteBufMonoSink.success(ByteBufUtil.encode(appTokenRequest));
                        }))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> {
                            HttpResponseChecker.check(tuple2.getT1(), tuple2.getT2());
                            return ByteBufUtil.decode(tuple2.getT2(), AppTokenResponse.class);
                        })
                        .map(AppTokenResponse::as)
                );
    }

    @Override public Mono<String> getToken() {
        return this.appToken.map(EMAppToken::getAccessToken).map(this::bearer);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
