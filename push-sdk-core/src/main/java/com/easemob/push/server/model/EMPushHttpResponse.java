package com.easemob.push.server.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import reactor.netty.http.client.HttpClientResponse;

import java.nio.charset.StandardCharsets;

public class EMPushHttpResponse {
    private final HttpResponseStatus status;
    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;
    private final HttpHeaders httpHeaders;
    private final byte[] bytes;

    public EMPushHttpResponse(HttpResponseStatus status,
            HttpMethod httpMethod, String uri, HttpVersion httpVersion,
            HttpHeaders httpHeaders, byte[] bytes) {
        this.status = status;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.httpHeaders = httpHeaders;
        this.bytes = bytes;
    }

    public static EMPushHttpResponse of(HttpClientResponse httpResponse, ByteBuf byteBuf) {
        return new EMPushHttpResponse(httpResponse.status(),
                httpResponse.method(),
                httpResponse.uri(),
                httpResponse.version(),
                httpResponse.responseHeaders(),
                io.netty.buffer.ByteBufUtil.getBytes(byteBuf, byteBuf.readerIndex(),
                        byteBuf.readableBytes(), true)
        );
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.httpMethod.toString() + " " + this.uri + "\r\n");
        stringBuilder.append(this.httpVersion.toString() + " " + this.status + "\r\n");
        if (this.httpHeaders != null) {
            this.httpHeaders.forEach(stringStringEntry -> {
                stringBuilder.append(
                        stringStringEntry.getKey() + ":" + stringStringEntry.getValue() + "\r\n");
            });
        }
        stringBuilder.append("\r\n");
        if (bytes != null) {
            stringBuilder.append(new String(bytes, StandardCharsets.UTF_8));
        }
        return stringBuilder.toString();
    }
}
