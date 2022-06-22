package com.easemob.push.server.model;

import com.easemob.push.server.exception.EMException;
import com.easemob.push.server.utils.Utilities;
import org.apache.logging.log4j.util.Strings;

public class AgoraAppCredentials implements Credentials {
    private final String appId;
    private final String appCert;

    public AgoraAppCredentials(String appId, String appCert) {
        if (Strings.isBlank(appId) || Strings.isBlank(appCert)) {
            throw new EMException("appId/appCert cannot be blank");
        }
        this.appId = appId;
        this.appCert = appCert;
    }

    @Override
    public String getId() {
        return appId;
    }

    @Override
    public String getSecret() {
        return appCert;
    }

    @Override public String toString() {
        return "AgoraAppCredentials{" +
                "appId='" + Utilities.mask(appId) + '\'' +
                ", appCert='" + Utilities.mask(appCert) + '\'' +
                '}';
    }
}
