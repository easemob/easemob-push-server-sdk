package com.easemob.push.common.connection;

import com.easemob.push.common.resp.APIConnectionException;
import com.easemob.push.common.resp.APIRequestException;
import com.easemob.push.common.resp.ResponseWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface IHttpClient {

    public static final String CHARSET = "UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String RATE_LIMIT_QUOTA = "X-Rate-Limit-Limit";
    public static final String RATE_LIMIT_Remaining = "X-Rate-Limit-Remaining";
    public static final String RATE_LIMIT_Reset = "X-Rate-Limit-Reset";
    public static final String EPUSH_USER_AGENT = "EPush-API-Java-Client";

    public static final int RESPONSE_OK = 200;
    public static final String IO_ERROR_MESSAGE = "Connection IO error. \n"
            + "Can not connect to EPush Server. "
            + "Please ensure your internet connection is ok. \n"
            + "If the problem persists, please let us know at support@easemob.com.";
    public static final String CONNECT_TIMED_OUT_MESSAGE = "connect timed out. \n"
            + "Connect to EPush Server timed out, and already retried some times. \n"
            + "Please ensure your internet connection is ok. \n"
            + "If the problem persists, please let us know at support@easemob.com.";
    public static final String READ_TIMED_OUT_MESSAGE = "Read timed out. \n"
            + "Read response from EPush Server timed out. \n"
            + "If this is a Push action, you may not want to retry. \n"
            + "It may be due to slowly response from EPush server, or unstable connection. \n"
            + "If the problem persists, please let us know at support@easemob.com.";
    public static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    /* 设置连接超时时间 */
    public static final int DEFAULT_CONNECTION_TIMEOUT = (5 * 1000); // milliseconds
    /* 设置读取超时时间 */
    public static final int DEFAULT_READ_TIMEOUT = (30 * 1000); // milliseconds
    public static final int DEFAULT_MAX_RETRY_TIMES = 3;

    public ResponseWrapper sendGet(String url)
            throws APIConnectionException, APIRequestException;

    public ResponseWrapper sendGet(String url, String content)
            throws APIConnectionException, APIRequestException;

    public ResponseWrapper sendDelete(String url)
            throws APIConnectionException, APIRequestException;

    public ResponseWrapper sendDelete(String url, String content)
            throws APIConnectionException, APIRequestException;

    public ResponseWrapper sendPost(String url, String content)
            throws APIConnectionException, APIRequestException;

    public ResponseWrapper sendPut(String url, String content)
            throws APIConnectionException, APIRequestException;

    public enum RequestMethod {
        GET,
        POST,
        PUT,
        DELETE
    }
}
