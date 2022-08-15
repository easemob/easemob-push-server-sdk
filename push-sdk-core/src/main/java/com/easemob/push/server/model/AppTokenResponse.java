package com.easemob.push.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppTokenResponse {
    // easemob token
    @JsonProperty("access_token")
    private final String accessToken;

    // TTL in seconds
    @JsonProperty("expires_in")
    private final int expireInSeconds;

    private AppTokenResponse(String accessToken, int expireInSeconds) {
        this.accessToken = accessToken;
        this.expireInSeconds = expireInSeconds;
    }

    @JsonCreator
    public static AppTokenResponse of(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") int expireInSeconds) {
        return new AppTokenResponse(accessToken, expireInSeconds);
    }

    public EMAppToken as() {
        return EMAppToken.of(accessToken,
                System.currentTimeMillis() + expireInSeconds * 1000L
        );
    }
}
