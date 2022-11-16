package com.easemob.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String url;
    private final Method method;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(String url, Method method, Map<String, String> headers, byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return prettyString();
    }

    /**
     * http协议格式化的请求字符串
     *
     * @return http协议格式化
     */
    public String prettyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.method.name())
                .append(" ")
                .append(this.getUrl())
                .append(" ")
                .append("")
                .append("\r\n");
        if (this.headers != null) {
            this.headers.forEach((k, v) -> {
                stringBuilder.append(k)
                        .append(":")
                        .append(v)
                        .append("\r\n");
            });
        }

        stringBuilder.append("\r\n");
        if (body != null) {
            stringBuilder.append(new String(body, StandardCharsets.UTF_8));
        }
        return stringBuilder.toString();
    }

    public static class Builder {
        private String url;
        private Map<String, String> urlParam = new HashMap<>();
        private Method method;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body;

        /**
         * http url
         *
         * @param url string
         * @return {@link Builder}
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * http url params
         *
         * @param key   string
         * @param value string
         * @return {@link Builder}
         */
        public Builder urlParam(String key, String value) {
            this.urlParam.put(key, value);
            return this;
        }

        /**
         * http method
         *
         * @param method {@link Method}
         * @return {@link Builder}
         */
        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        /**
         * http body bytes
         *
         * @param body bytes array
         * @return {@link Builder}
         */
        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        /**
         * http headers
         *
         * @param headers headers map
         * @return {@link HttpRequest}
         */
        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        /**
         * http header
         *
         * @param key   string
         * @param value string
         * @return {@link HttpRequest}
         */
        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        /**
         * HTTP/1.1 header of Connection
         *
         * @param isKeepAlive boolean
         * @return {@link Builder}
         */
        public Builder keepAlive(boolean isKeepAlive) {
            return header("Connection", isKeepAlive ? "Keep-Alive" : "Close");
        }

        /**
         * build for {@link HttpRequest}
         *
         * @return {@link HttpRequest}
         */
        public HttpRequest build() {
            if (url == null) {
                throw new RuntimeException("url null");
            }
            if (method == null) {
                throw new RuntimeException("method null");
            }

            if (urlParam.isEmpty()) {
                return new HttpRequest(this.url, method, headers, body);
            }

            StringBuilder stringBuilder = new StringBuilder(url);
            stringBuilder.append("?");

            urlParam.forEach((k, v) -> {
                stringBuilder.append(k).append("=").append(v).append("&");
            });

            String substring = stringBuilder.substring(0, stringBuilder.length() - 1);

            return new HttpRequest(substring, method, headers, body);

        }
    }
}



