package com.easemob.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppTokenRequest {
    @JsonProperty("grant_type")
    private final String grantType = "client_credentials";

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    private AppTokenRequest(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @JsonCreator
    public static AppTokenRequest of(@JsonProperty("client_id") String clientId,
            @JsonProperty("client_secret") String clientSecret) {
        return new AppTokenRequest(clientId, clientSecret);
    }

}
