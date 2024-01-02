package com.easemob.http.client;

import com.easemob.common.exception.EMException;
import com.easemob.http.AbstractHttpExecuter;
import com.easemob.http.HttpRequest;
import com.easemob.push.model.EMPushHttpResponse;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NativeHttpClient extends AbstractHttpExecuter {

    private final Map<String, String> defaultHeaders = new HashMap<>();
    private final Map<String, Supplier<String>> headersWhen = new HashMap<>();

    private Proxy proxy;

    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;

    private int connectTimeout = 60000;
    private int readTimeout = 60000;

    private boolean useCache = false;

    public Proxy getProxy() {
        return proxy;
    }

    public NativeHttpClient setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public NativeHttpClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public NativeHttpClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public NativeHttpClient setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public NativeHttpClient setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public NativeHttpClient setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    @Override
    public void defaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
    }

    @Override
    public void headerWhen(String key, Supplier<String> stringSupplier) {
        this.headersWhen.put(key, stringSupplier);
    }

    public HttpURLConnection getUrlConnection(URL url) throws IOException {
        URLConnection connection =
                proxy == null ? url.openConnection() : url.openConnection(proxy);

        if (connection instanceof HttpsURLConnection) {
            if (sslSocketFactory != null) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            }
            if (hostnameVerifier != null) {
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
            }
        }

        if (!defaultHeaders.isEmpty()) {
            defaultHeaders.forEach(connection::setRequestProperty);
        }

        if (!headersWhen.isEmpty()) {
            headersWhen.forEach((k, v) -> connection.setRequestProperty(k, v.get()));
        }

        return (HttpURLConnection) connection;
    }

    @Override
    public EMPushHttpResponse execute(HttpRequest httpRequest) {
        try {
            return doRequest(httpRequest.getUrl(), httpRequest.getMethod().name(),
                    httpRequest.getHeaders(), httpRequest.getBody());
        } catch (Exception e) {
            throw new EMException("request execute error", e);
        }
    }

    private EMPushHttpResponse doRequest(String url, String requestMethod,
            Map<String, String> headers,
            byte[] body) throws IOException {

        HttpURLConnection connection = getUrlConnection(new URL(url));
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setUseCaches(useCache);
        connection.setDoInput(true);
        connection.setRequestMethod(requestMethod);

        if (headers != null) {
            headers.forEach(connection::setRequestProperty);
        }

        if (body != null && body.length > 0) {
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(body.length);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body);
            outputStream.flush();
        }

        int responseCode = -1;
        String desc = "";
        String version = "";

        // HTTP/1.1 404 Not Found
        // HTTP/1.0 200
        String statusLine = connection.getHeaderField(0);

        if (statusLine.startsWith("HTTP/1.")) {

            int codeIndex = statusLine.indexOf(' ');

            version = statusLine.substring(0, codeIndex);

            if (codeIndex > 0) {
                int phraseIndex = statusLine.indexOf(' ', codeIndex + 1);
                if (phraseIndex > 0) {
                    desc = statusLine.substring(phraseIndex + 1);
                }

                if (phraseIndex < 0)
                    phraseIndex = statusLine.length();
                try {
                    responseCode =
                            Integer.parseInt(statusLine.substring(codeIndex + 1, phraseIndex));
                } catch (NumberFormatException ignore) {
                }
            }
        }

        if (responseCode == -1) {
            responseCode = connection.getResponseCode();
        }

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        Map<String, Object> headerMap = new HashMap<>();
        for (Map.Entry<String, List<String>> stringListEntry : headerFields.entrySet()) {
            String key = stringListEntry.getKey();
            if (key != null) {
                headerMap.put(key, stringListEntry.getValue().get(0));
            }
        }

        // status 401 while no input
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (Exception e) {
            inputStream = connection.getErrorStream();
        }

        byte[] read = null;

        if (inputStream != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            for (int len = 0; (len = inputStream.read(bytes)) != -1; ) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            read = byteArrayOutputStream.toByteArray();
        }
        return new EMPushHttpResponse(responseCode, desc, version, headerMap, read);
    }
}
