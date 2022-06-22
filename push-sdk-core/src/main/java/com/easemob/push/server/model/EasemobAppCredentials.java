package com.easemob.push.server.model;

import com.easemob.push.server.exception.EMException;
import com.easemob.push.server.utils.Utilities;
import org.apache.logging.log4j.util.Strings;

public class EasemobAppCredentials implements Credentials {
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

    @Override
    public String toString() {
        return "EasemobAppCredentials{" +
                "clientId='" + Utilities.mask(clientId) + '\'' +
                ", clientSecret='" + Utilities.mask(clientSecret) + '\'' +
                '}';
    }

}
