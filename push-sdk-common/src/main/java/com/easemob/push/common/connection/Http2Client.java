package com.easemob.push.common.connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.StringUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Http2Client implements IHttpClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Logger log = LoggerFactory.getLogger(Http2Client.class);
    private static final String KEYWORDS_CONNECT_TIMED_OUT = "connect timed out";
    private static final String KEYWORDS_READ_TIMED_OUT = "Read timed out";
    private final int connectionTimeout;
    private final int readTimeout;
    private final int maxRetryTimes;
    private final String sslVer;
    private final String encryptType;
    private String authCode;
    private HttpProxy httpProxy;

    public Http2Client(String authCode, HttpProxy proxy, ClientConfig config) {
        maxRetryTimes = config.getMaxRetryTimes();
        connectionTimeout = config.getConnectionTimeout();
        readTimeout = config.getReadTimeout();
        sslVer = config.getSSLVersion();

        this.authCode = authCode;
        httpProxy = proxy;
        encryptType = config.getEncryptType();
        String message = MessageFormat.format("Created instance with "
                        + "connectionTimeout {0}, readTimeout {1}, maxRetryTimes {2}, SSL Version {3}",
                connectionTimeout, readTimeout, maxRetryTimes, sslVer);
        log.info(message);

        if (null != httpProxy && httpProxy.isAuthenticationNeeded()) {
            Authenticator.setDefault(new NativeHttpClient.SimpleProxyAuthenticator(
                    httpProxy.getUsername(), httpProxy.getPassword()));
        }
    }

    @Override
    public ResponseWrapper sendGet(String url) throws APIConnectionException, APIRequestException {
        return sendGet(url, null);
    }

    public ResponseWrapper sendGet(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        log.debug("Send request - Get  {}", url);
        if (null != content) {
            log.debug("Request Content - {}", content);
        }

        try {
            Request.Builder requestBuilder = new Request.Builder().url(url)
                    .header("User-Agent", EPUSH_USER_AGENT)
                    .addHeader("Accept-Charset", CHARSET)
                    .addHeader("Charset", CHARSET)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", authCode)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON);
            if (!StringUtils.isEmpty(encryptType)) {
                requestBuilder.addHeader("X-Encrypt-Type", encryptType);
            }
            Request request = requestBuilder.build();
            if (null != content) {
                byte[] data = content.getBytes(CHARSET);
                request.newBuilder().header("Content-Length", String.valueOf(data.length));
            }
            handleResponse(wrapper, request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    public void handleResponse(ResponseWrapper wrapper, Request request) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            wrapper.responseCode = 200;
            wrapper.responseContent = response.body().string();
            log.debug("Succeed to get response OK - response body: {}", wrapper.responseContent);
        } else {
            int status = response.code();
            wrapper.responseCode = status;
            wrapper.responseContent = response.body().string();
            if (status >= 300 && status < 400) {
                log.warn("Normal response but unexpected - responseCode: {}, responseContent: {}",
                        status, wrapper.responseContent);

            } else {
                log.warn("Got error response - responseCode: {}, responseContent: {}",
                        status, wrapper.responseContent);

                switch (status) {
                    case 400:
                        log.error(
                                "Your request params is invalid. Please check them according to error message.");
                        wrapper.setErrorObject();
                        break;
                    case 401:
                        log.error(
                                "Authentication failed! Please check authentication params according to docs.");
                        wrapper.setErrorObject();
                        break;
                    case 403:
                        log.error(
                                "Request is forbidden! Maybe your appkey is listed in blacklist or your params is invalid.");
                        wrapper.setErrorObject();
                        break;
                    case 404:
                        log.error("Request page is not found! Maybe your params is invalid.");
                        wrapper.setErrorObject();
                        break;
                    case 410:
                        log.error(
                                "Request resource is no longer in service. Please according to notice on official website.");
                        wrapper.setErrorObject();
                    case 429:
                        log.error("Too many requests! Please review your appkey's request quota.");
                        wrapper.setErrorObject();
                        break;
                    case 500:
                    case 502:
                    case 503:
                    case 504:
                        log.error(
                                "Seems encountered server error. Maybe EPush is in maintenance? Please retry later.");
                        break;
                    default:
                        log.error("Unexpected response.");
                }
            }
            log.warn("Got error response - response: {}", response.body().string());
            wrapper.setErrorObject();
        }
    }

    @Override
    public ResponseWrapper sendDelete(String url)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        log.debug("Send request - Delete url: {}", url);
        Request request;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url)
                    .header("User-Agent", EPUSH_USER_AGENT)
                    .addHeader("Accept-Charset", CHARSET)
                    .addHeader("Charset", CHARSET)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", authCode)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON)
                    .delete();
            if (!StringUtils.isEmpty(encryptType)) {
                requestBuilder.addHeader("X-Encrypt-Type", encryptType);
            }
            request = requestBuilder.build();
            handleResponse(wrapper, request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendDelete(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        log.debug("Send request - Delete url: {}, content: {}", url, content);
        Request request;
        try {
            RequestBody body = RequestBody.create(JSON, content);
            Request.Builder requestBuilder = new Request.Builder().url(url)
                    .header("User-Agent", EPUSH_USER_AGENT)
                    .addHeader("Accept-Charset", CHARSET)
                    .addHeader("Charset", CHARSET)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", authCode)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON)
                    .delete(body);
            if (!StringUtils.isEmpty(encryptType)) {
                requestBuilder.addHeader("X-Encrypt-Type", encryptType);
            }
            request = requestBuilder.build();
            handleResponse(wrapper, request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendPost(String url, String content)
            throws APIConnectionException, APIRequestException {
        log.debug("Send request - Post url: {}, content: {}", url, content);
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            RequestBody body = RequestBody.create(JSON, content);
            Request.Builder requestBuilder = new Request.Builder().url(url)
                    .header("User-Agent", EPUSH_USER_AGENT)
                    .addHeader("Accept-Charset", CHARSET)
                    .addHeader("Charset", CHARSET)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", authCode)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON)
                    .post(body);
            if (!StringUtils.isEmpty(encryptType)) {
                requestBuilder.addHeader("X-Encrypt-Type", encryptType);
            }
            Request request = requestBuilder.build();
            handleResponse(wrapper, request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendPut(String url, String content)
            throws APIConnectionException, APIRequestException {
        log.debug("Send request - Put url: {}, content: {}", url, content);
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            RequestBody body = RequestBody.create(JSON, content);
            Request.Builder requestBuilder = new Request.Builder().url(url)
                    .header("User-Agent", EPUSH_USER_AGENT)
                    .addHeader("Accept-Charset", CHARSET)
                    .addHeader("Charset", CHARSET)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Authorization", authCode)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON)
                    .put(body);
            if (!StringUtils.isEmpty(encryptType)) {
                requestBuilder.addHeader("X-Encrypt-Type", encryptType);
            }
            Request request = requestBuilder.build();
            handleResponse(wrapper, request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }
}
