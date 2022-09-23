package com.easemob.push.model;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EMPushHttpResponse {
    private final int status;
    private final String desc;
    private final String version;
    private final Map<String, Object> headers;
    private final byte[] bytes;

    public EMPushHttpResponse(int status, String desc, String version,
            Map<String, Object> headers, byte[] bytes) {
        this.status = status;
        this.desc = desc;
        this.version = version;
        this.headers = headers;
        this.bytes = bytes;
    }

    public String prettyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.version)
                .append(" ")
                .append(this.status)
                .append(" ")
                .append(this.desc)
                .append("\r\n");
        if (this.headers != null) {
            this.headers.forEach((k, v) -> {
                stringBuilder.append(k)
                        .append(":")
                        .append(v.toString())
                        .append("\r\n");
            });
        }
        stringBuilder.append("\r\n");
        if (bytes != null) {
            stringBuilder.append(new String(bytes, StandardCharsets.UTF_8));
        }
        return stringBuilder.toString();
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
