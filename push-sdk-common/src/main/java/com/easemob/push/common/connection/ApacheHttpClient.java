package com.easemob.push.common.connection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.resp.APIConnectionException;
import com.easemob.push.common.resp.APIRequestException;
import com.easemob.push.common.resp.ResponseWrapper;
import com.easemob.push.common.utils.StringUtils;

/**
 * Apache HttpClient 实现的版本，提供了连接池来实现高并发网络请求。
 */
public class ApacheHttpClient implements IHttpClient {

    private final static Object syncLock = new Object();
    private static Logger log = LoggerFactory.getLogger(ApacheHttpClient.class);
    private static CloseableHttpClient httpClient = null;
    private static PoolingHttpClientConnectionManager cm;
    private final int connectionTimeout;
    private final int connectionRequestTimeout;
    private final int socketTimeout;
    private final int maxRetryTimes;
    private final String encryptType;
    private String authCode;
    private HttpHost proxyHttpHost;
    // 最大连接数
    private int maxConnectionCount = 200;
    // 每个路由的最大连接数
    private int maxConnectionPerRoute = 40;
    // 目标主机的最大连接数
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
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= maxRetryTimes) {
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        return HttpClients.custom()
                .setConnectionManager(cm)
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
        //        return doRequest(url, content, RequestMethod.PUT);
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

    public ResponseWrapper uploadFile(String url, String path, String fileType)
            throws APIConnectionException, APIRequestException {
        log.info("Upload file: " + url + "filePath：" + path);
        ResponseWrapper wrapper = new ResponseWrapper();
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            log.error("File not exist!");
            wrapper.setErrorObject();
            return wrapper;
        }
        String boundary = "---------------------------" + new Date().getTime();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, authCode);
            FileInputStream fis = new FileInputStream(file);
            File tempFile =
                    File.createTempFile(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()),
                            null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write((boundary + "\r\n").getBytes());
            fos.write(
                    ("Content-Disposition: form-data; name=\"" + fileType + "\"; filename=\"" + file
                            .getName() + "\"\r\n").getBytes());
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] buff = new byte[8096];
            int len = 0;
            while ((len = bis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
            fos.write(("\r\n--" + boundary + "--\r\n").getBytes());
            FileEntity entity = new FileEntity(tempFile, ContentType.MULTIPART_FORM_DATA);
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            response = getHttpClient(url).execute(httpPost);
            processResponse(response, wrapper);
        } catch (IOException e) {
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
        log.debug("Response", response.toString());
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
            log.debug("Succeed to get response OK - responseCode:" + status);
            log.debug("Response Content - " + responseContent);

        } else if (status >= 300 && status < 400) {
            log.warn(
                    "Normal response but unexpected - responseCode:" + status + ", responseContent:"
                            + responseContent);

        } else {
            log.warn("Got error response - responseCode:" + status + ", responseContent:"
                    + responseContent);

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
            if (cm != null) {
                cm.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}
