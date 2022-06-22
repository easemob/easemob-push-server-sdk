package com.easemob.push.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DnsConfigResponse {
    @JsonProperty("deploy_name")
    private String name;
    @JsonProperty("file_version")
    private String version;
    @JsonProperty("rest")
    private Service service;

    @JsonCreator
    public DnsConfigResponse(@JsonProperty("deploy_name") String name,
            @JsonProperty("file_version") String version,
            @JsonProperty("rest") Service service) {
        this.name = name;
        this.version = version;
        this.service = service;
    }

    public List<Endpoint> endpoints() {
        return this.service.endpoints();
    }

    public static class Service {
        @JsonProperty("hosts")
        private List<Endpoint> endpoints;

        public Service(@JsonProperty("hosts") List<Endpoint> endpoints) {
            this.endpoints = endpoints;
        }

        public List<Endpoint> endpoints() {
            return this.endpoints;
        }
    }


    public static class Endpoint {
        @JsonProperty("protocol")
        private String protocol;
        @JsonProperty("domain")
        private String domainName;
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("port")
        private int port;

        public Endpoint(@JsonProperty("protocol") String protocol,
                @JsonProperty("domain") String domainName,
                @JsonProperty("ip") String ip,
                @JsonProperty("port") int port) {
            this.protocol = protocol;
            this.domainName = domainName;
            this.ip = ip;
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getDomainName() {
            return domainName;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public String getUri() {
            if (port > 0) {
                return String.format("%s://%s:%d", protocol, domainName == null ? ip : domainName,
                        port);
            } else {
                return String.format("%s://%s", protocol, domainName == null ? ip : domainName);
            }
        }

        @Override
        public String toString() {
            return this.getUri();
        }
    }
}
