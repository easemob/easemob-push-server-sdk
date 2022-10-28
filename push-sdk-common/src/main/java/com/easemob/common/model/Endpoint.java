package com.easemob.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Endpoint {
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

    public String getUri() {
        if (port > 0) {
            return String.format("%s://%s:%d", protocol,
                    (ip == null || ip.length() == 0) ? domainName : ip, port);
        } else {
            return String.format("%s://%s", protocol,
                    (ip == null || ip.length() == 0) ? domainName : ip);
        }
    }

    @Override
    public String toString() {
        return this.getUri();
    }
}
