package com.easemob.push;

import com.easemob.http.AbstractHttpExecuter;
import com.easemob.push.service.DomainProvider;
import com.easemob.push.service.TokenProvider;
import com.easemob.push.service.impl.EMDomainProvider;
import com.easemob.push.service.impl.EMTokenProvider;
import com.easemob.push.utils.EMPushHttpClientFactory;

import static com.easemob.common.Constant.AUTHORIZATION;

public class EMPushContext {

    private final EMPushProperties emPushProperties;
    private final DomainProvider emDomainProvider;
    private final TokenProvider emTokenProvider;

    private AbstractHttpExecuter httpClient;

    public EMPushContext(EMPushProperties emPushProperties) {

        this.emPushProperties = emPushProperties;

        this.emDomainProvider = new EMDomainProvider(emPushProperties);
        this.emTokenProvider = new EMTokenProvider(emPushProperties, this.emDomainProvider);

        this.httpClient = EMPushHttpClientFactory.create(this.emPushProperties);
        this.httpClient.headerWhen(AUTHORIZATION, emTokenProvider::getToken);
    }

    public AbstractHttpExecuter getHttpClient() {
        return httpClient;
    }

    public String getBaseUrl() {
        return emDomainProvider.getEndpoint() + "/" + emPushProperties.getAppKeyUrlEncoded();
    }
}
