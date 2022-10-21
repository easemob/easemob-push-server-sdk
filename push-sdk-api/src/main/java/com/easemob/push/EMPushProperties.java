package com.easemob.push;

import com.easemob.common.exception.EMInvalidArgumentException;
import com.easemob.common.model.Credentials;
import com.easemob.common.model.Endpoint;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class EMPushProperties {

    private final List<Endpoint> hosts;
    private final EMPushProxy emPushProxy;
    private final String appKey;
    private final Integer connectTimeout;
    private final Integer readTimeout;
    private final Protocol protocol;
    private final Credentials credentials;

    public EMPushProperties(List<Endpoint> hosts, EMPushProxy emPushProxy, String appKey,
            Integer connectTimeout, Integer readTimeout, Protocol protocol,
            Credentials credentials) {
        this.hosts = hosts;
        this.emPushProxy = emPushProxy;
        this.appKey = appKey;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.protocol = protocol;
        this.credentials = credentials;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Endpoint> getHosts() {
        return hosts;
    }

    public EMPushProxy getEmProxy() {
        return emPushProxy;
    }

    public String getAppKey() {
        return appKey;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getAppKeyUrlEncoded() {
        return appKey.replace("#", "/");
    }

    public enum Protocol {
        HTTP, HTTPS
    }


    public static class Builder {
        private List<Endpoint> hosts;
        private EMPushProxy emPushProxy;
        private String appKey;
        private Integer connectTimeout = 20 * 1000;
        private Integer readTimeout = 10 * 1000;
        private Protocol protocol = Protocol.HTTP;
        private Credentials credentials;

        public Builder setHosts(List<Endpoint> hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder setEmProxy(EMPushProxy emPushProxy) {
            this.emPushProxy = emPushProxy;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setCredentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public EMPushProperties build() {
            if (Strings.isBlank(appKey)) {
                throw new EMInvalidArgumentException("appKey must not be null or blank");
            }
            if (this.credentials == null) {
                throw new EMInvalidArgumentException("credentials must not be null for auth");
            }
            return new EMPushProperties(this.hosts, this.emPushProxy, this.appKey,
                    this.connectTimeout,
                    this.readTimeout, this.protocol, this.credentials);
        }
    }
}
