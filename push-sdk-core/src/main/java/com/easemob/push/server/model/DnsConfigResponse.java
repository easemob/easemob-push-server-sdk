package com.easemob.push.server.model;

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
}
