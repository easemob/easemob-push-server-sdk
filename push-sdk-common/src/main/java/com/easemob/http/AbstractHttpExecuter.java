package com.easemob.http;

import com.easemob.push.model.EMPushHttpResponse;

import java.util.function.Supplier;

public abstract class AbstractHttpExecuter {

    /**
     * 默认请求头
     *
     * @param key   string
     * @param value string
     */
    public abstract void defaultHeader(String key, String value);

    /**
     * 填充请求头
     *
     * @param key            string
     * @param stringSupplier supplier for string
     */
    public abstract void headerWhen(String key, Supplier<String> stringSupplier);

    /**
     * 执行http 请求
     *
     * @param httpRequest http请求内容
     * @return EMPushHttpResponse
     */
    public abstract EMPushHttpResponse execute(HttpRequest httpRequest);
}
