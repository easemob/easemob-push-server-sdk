package com.easemob.push.common.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.StringUtils;

/**
 * Apache HttpClient 实现的版本，提供了连接池来实现高并发网络请求。
 */
public class ApacheHttpClient implements IHttpClient {

    private static final  Object syncLock = new Object();
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpClient.class);
    private static CloseableHttpClient httpClient = null;
    private PoolingHttpClientConnectionManager connectionManager;
    private final int connectionTimeout;
    private final int connectionRequestTimeout;
    private final int socketTimeout;
    private final int maxRetryTimes;
    private final String encryptType;
    private final String authCode;
    private HttpHost proxyHttpHost;
    /* 最大连接数 */
    private int maxConnectionCount = 200;
    /* 每个路由的最大连接数 */
    private int maxConnectionPerRoute = 40;
    /* 目标主机的最大连接数 */
    private int maxRoute = 100;

    public ApacheHttpClient(String authCode, HttpProxy proxy, ClientConfig config) {
        maxRetryTimes = config.getMaxRetryTimes();
        connectionTimeout = config.getConnectionTimeout();
        connectionRequestTimeout = config.getConnectionRequestTimeout();
        socketTimeout = config.getSocketTimeout();
        this.authCode = authCode;
        encryptType = config.getEncryptType();
        if (proxy != null) {
            proxyHttpHost = new HttpHost(proxy.getHost(), proxy.getPort());
        }
    }

    private static String getFirstHeader(CloseableHttpResponse response, String name) {
        Header header = response.getFirstHeader(name);
        return header == null ? null : header.getValue();
    }

    private void configHttpRequest(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig;
        if (proxyHttpHost != null) {
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setProxy(proxyHttpHost)
                    .build();
        } else {
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();
        }

        httpRequestBase.setConfig(requestConfig);
    }

    public CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient =
                            createHttpClient(maxConnectionCount, maxConnectionPerRoute, maxRoute,
                                    hostname, port);
                }
            }
        }
        return httpClient;

    }

    /**
     * 设置最大连接数
     *
     * @param count 连接数
     */
    public void setMaxConnectionCount(int count) {
        this.maxConnectionCount = count;
    }

    /**
     * 设置每个路由最大连接数
     *
     * @param count 连接数
     */
    public void setMaxConnectionPerRoute(int count) {
        this.maxConnectionPerRoute = count;
    }

    /**
     * 设置目标主机最大连接数
     *
     * @param count 连接数
     */
    public void setMaxHostConnection(int count) {
        this.maxRoute = count;
    }

    public CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute,
            String hostname, int port) {
        ConnectionSocketFactory plainConnectionSocketFactory = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslConnectionSocketFactory = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainConnectionSocketFactory)
                .register("https", sslConnectionSocketFactory).build();
        connectionManager = new PoolingHttpClientConnectionManager(registry);
        /* 将最大连接数增加 */
        connectionManager.setMaxTotal(maxTotal);
        /* 将每个路由基础的连接增加 */
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        /* 将目标主机的最大连接数增加 */
        connectionManager.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        /* 请求重试处理 */
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= maxRetryTimes) {
                    return false;
                }
                /* 如果服务器丢掉了连接，那么就重试 */
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                /* 不要重试SSL握手异常 */
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                /* 超时 */
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                /* 目标服务器不可达 */
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                /* 连接被拒绝 */
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                /* SSL握手异常 */
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                /* 如果请求是幂等的，就再次尝试 */
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setRetryHandler(httpRequestRetryHandler).build();

    }

    @Override
    public ResponseWrapper sendGet(String url) throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            if (!StringUtils.isEmpty(encryptType)) {
                httpGet.setHeader("X-Encrypt-Type", encryptType);
            }
            configHttpRequest(httpGet);
            response = getHttpClient(url).execute(httpGet, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpGet.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;

    }

    public ResponseWrapper sendGet(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            if (!StringUtils.isEmpty(encryptType)) {
                httpGet.setHeader("X-Encrypt-Type", encryptType);
            }
            httpGet.setHeader("Content-Type", NativeHttpClient.CONTENT_TYPE_JSON);
            configHttpRequest(httpGet);
            response = getHttpClient(url).execute(httpGet, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpGet.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendDelete(String url)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpDelete httpDelete = new HttpDelete(url);
        try {
            httpDelete.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            configHttpRequest(httpDelete);
            response = getHttpClient(url).execute(httpDelete, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpDelete.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    public ResponseWrapper sendDelete(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        try {
            httpDelete.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            httpDelete.setHeader("Content-Type", "application/json");
            configHttpRequest(httpDelete);
            StringEntity params = new StringEntity(StringUtils.notNull(content), CHARSET);
            httpDelete.setEntity(params);
            response = getHttpClient(url).execute(httpDelete, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpDelete.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendPost(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            if (!StringUtils.isEmpty(encryptType)) {
                httpPost.setHeader("X-Encrypt-Type", encryptType);
            }
            httpPost.setHeader("Content-Type", "application/json");
            configHttpRequest(httpPost);
            StringEntity params = new StringEntity(StringUtils.notNull(content), CHARSET);
            httpPost.setEntity(params);
            response = getHttpClient(url).execute(httpPost, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpPost.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    @Override
    public ResponseWrapper sendPut(String url, String content)
            throws APIConnectionException, APIRequestException {
        ResponseWrapper wrapper = new ResponseWrapper();
        CloseableHttpResponse response = null;
        HttpPut httpPut = new HttpPut(url);
        try {
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            if (!StringUtils.isEmpty(encryptType)) {
                httpPut.setHeader("X-Encrypt-Type", encryptType);
            }
            httpPut.setHeader("Content-Type", "application/json");
            configHttpRequest(httpPut);
            StringEntity params = new StringEntity(StringUtils.notNull(content), CHARSET);
            httpPut.setEntity(params);
            response = getHttpClient(url).execute(httpPut, HttpClientContext.create());
            processResponse(response, wrapper);
        } catch (IOException e) {
            httpPut.abort();
            log.debug(IO_ERROR_MESSAGE, e);
            throw new APIConnectionException(READ_TIMED_OUT_MESSAGE, e, true);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    public void processResponse(CloseableHttpResponse response, ResponseWrapper wrapper)
            throws APIConnectionException, APIRequestException, IOException {
        HttpEntity entity = response.getEntity();
        log.debug("Response {}", response);
        int status = response.getStatusLine().getStatusCode();
        String responseContent = "";
        if (entity != null) {
            responseContent = EntityUtils.toString(entity, "utf-8");
        }
        wrapper.responseCode = status;
        wrapper.responseContent = responseContent;
        String quota = getFirstHeader(response, RATE_LIMIT_QUOTA);
        String remaining = getFirstHeader(response, RATE_LIMIT_Remaining);
        String reset = getFirstHeader(response, RATE_LIMIT_Reset);
        wrapper.setRateLimit(quota, remaining, reset);

        log.debug(wrapper.responseContent);
        EntityUtils.consume(entity);
        if (status >= 200 && status < 300) {
            log.debug("Succeed to get response OK - responseCode: {}", status);
            log.debug("Response Content - {}", responseContent);
        } else if (status >= 300 && status < 400) {
            log.warn(
                    "Normal response but unexpected - responseCode: {} , responseContent: {}",
                    status, responseContent);
        } else {
            log.warn("Got error response - responseCode: {} , responseContent: {}",
                    status, responseContent);
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

            throw new APIRequestException(wrapper);
        }
    }

    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            if (connectionManager != null) {
                connectionManager.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


