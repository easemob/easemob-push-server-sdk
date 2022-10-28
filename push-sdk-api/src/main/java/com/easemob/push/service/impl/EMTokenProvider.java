package com.easemob.push.service.impl;

import com.easemob.common.Constant;
import com.easemob.common.exception.EMException;
import com.easemob.common.model.AppTokenRequest;
import com.easemob.common.model.AppTokenResponse;
import com.easemob.common.model.EMAppToken;
import com.easemob.common.model.Endpoint;
import com.easemob.common.util.BytesUtil;
import com.easemob.http.AbstractHttpExecuter;
import com.easemob.http.HttpRequest;
import com.easemob.http.Method;
import com.easemob.push.EMPushProperties;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.service.DomainProvider;
import com.easemob.push.service.TokenProvider;
import com.easemob.push.utils.EMPushHttpClientFactory;

public class EMTokenProvider implements TokenProvider {

    private final EMPushProperties emPushProperties;
    private final AppTokenRequest appTokenRequest;
    private final DomainProvider emDomainProvider;
    private final AbstractHttpExecuter httpClient;

    private String tokenStr;
    private long expireAt;

    public EMTokenProvider(EMPushProperties emPushProperties, DomainProvider emDomainProvider) {

        this.emPushProperties = emPushProperties;
        this.httpClient = EMPushHttpClientFactory.create(emPushProperties);
        this.appTokenRequest = AppTokenRequest.of(this.emPushProperties.getCredentials().getId(),
                this.emPushProperties.getCredentials().getSecret());

        this.emDomainProvider = emDomainProvider;
    }

    public void refreshToken() {

        Endpoint endpoint = emDomainProvider.getEndpoint();

        String urlStr = endpoint.getUri() +
                String.format(Constant.TOKEN_URI_PATTERN, emPushProperties.getAppKeyUrlEncoded());

        HttpRequest httpRequest = HttpRequest.Builder()
                .url(urlStr)
                .method(Method.POST)
                .body(BytesUtil.encode(appTokenRequest))
                .build();

        EMPushHttpResponse execute = httpClient.execute(httpRequest);

        if (execute.getStatus() != 200) {
            throw new EMException("refresh token failed " + execute.getBody());
        }

        AppTokenResponse appTokenResponse =
                BytesUtil.decode(execute.getBytes(), AppTokenResponse.class);

        EMAppToken appToken = appTokenResponse.as();

        tokenStr = bearer(appToken.getAccessToken());
        expireAt = System.currentTimeMillis() + (appToken.getExpireTimestamp() / 2) * 1000;
    }

    @Override
    public String getToken() {

        if (expireAt < System.currentTimeMillis()) {
            refreshToken();
        }

        if (tokenStr == null) {
            throw new EMException("get token null");
        }

        return tokenStr;
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
