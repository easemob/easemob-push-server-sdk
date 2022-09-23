package com.easemob.common.model;

import com.easemob.common.exception.EMException;

import org.apache.logging.log4j.util.Strings;

public class EasemobAppCredentials extends Credentials {
    private final String clientId;
    private final String clientSecret;

    public EasemobAppCredentials(String clientId, String clientSecret) {
        if (Strings.isBlank(clientId) || Strings.isBlank(clientSecret)) {
            throw new EMException("clientId/clientSecret cannot be blank");
        }
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    public static EasemobAppCredentials of(String clientId, String clientSecret) {
        if (Strings.isBlank(clientId) || Strings.isBlank(clientSecret)) {
            throw new EMException("clientId/clientSecret cannot be blank");
        }
        return new EasemobAppCredentials(clientId, clientSecret);
    }

    @Override
    public String getId() {
        return this.clientId;
    }

    @Override
    public String getSecret() {
        return this.clientSecret;
    }
}
