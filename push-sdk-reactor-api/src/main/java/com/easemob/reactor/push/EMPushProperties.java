package com.easemob.reactor.push;

import com.easemob.common.exception.EMInvalidArgumentException;
import com.easemob.common.model.Credentials;
import com.easemob.common.model.Endpoint;
import com.easemob.reactor.push.EMPushProxy;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class EMPushProperties {

    private final List<Endpoint> hosts;
    private final EMPushProxy emProxy;
    private final String appKey;
    private final Integer httpConnectionPoolSize;
    private final Long httpConnectionMaxIdleTime;
    private final Protocol protocol;
    private final Credentials credentials;

    public EMPushProperties(List<Endpoint> hosts, EMPushProxy emProxy, String appKey,
            Integer httpConnectionPoolSize, Long httpConnectionMaxIdleTime,
            Protocol protocol, Credentials credentials) {
        this.hosts = hosts;
        this.emProxy = emProxy;
        this.appKey = appKey;
        this.httpConnectionPoolSize = httpConnectionPoolSize;
        this.httpConnectionMaxIdleTime = httpConnectionMaxIdleTime;
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
        return emProxy;
    }

    public String getAppKey() {
        return appKey;
    }

    public Integer getHttpConnectionPoolSize() {
        return httpConnectionPoolSize;
    }

    public Long getHttpConnectionMaxIdleTime() {
        return httpConnectionMaxIdleTime;
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
        private EMPushProxy emProxy;
        private String appKey;
        private Integer httpConnectionPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        private Long httpConnectionMaxIdleTime = 10 * 1000L;
        private Protocol protocol = Protocol.HTTP;
        private Credentials credentials;

        public Builder setHosts(List<Endpoint> hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder setEmProxy(EMPushProxy emProxy) {
            this.emProxy = emProxy;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setHttpConnectionPoolSize(Integer httpConnectionPoolSize) {
            this.httpConnectionPoolSize = httpConnectionPoolSize;
            return this;
        }

        public Builder setHttpConnectionMaxIdleTime(Long httpConnectionMaxIdleTime) {
            this.httpConnectionMaxIdleTime = httpConnectionMaxIdleTime;
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
            return new EMPushProperties(this.hosts, this.emProxy, this.appKey,
                    this.httpConnectionPoolSize, this.httpConnectionMaxIdleTime, this.protocol,
                    this.credentials);
        }
    }
}
