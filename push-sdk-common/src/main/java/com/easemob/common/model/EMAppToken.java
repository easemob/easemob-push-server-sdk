package com.easemob.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EMAppToken {

    private final String accessToken;
    private final Long expireTimestamp;

    public EMAppToken(String accessToken, Long expireTimestamp) {
        this.accessToken = accessToken;
        this.expireTimestamp = expireTimestamp;
    }

    @JsonCreator
    public static EMAppToken of(String accessToken, Long expireEpochMilli) {
        return new EMAppToken(accessToken, expireEpochMilli);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpireTimestamp() {
        return expireTimestamp;
    }
}
