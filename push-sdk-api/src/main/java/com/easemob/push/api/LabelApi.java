package com.easemob.push.api;

import com.easemob.common.Constant;
import com.easemob.common.util.BytesUtil;
import com.easemob.http.HttpRequest;
import com.easemob.http.Method;
import com.easemob.push.EMPushContext;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.model.LabelRequest;
import com.easemob.push.model.LabelUserRequest;

import static com.easemob.common.Constant.LABEL_URI;

public class LabelApi {

    private final EMPushContext context;

    public LabelApi(EMPushContext context) {
        this.context = context;
    }

    /**
     * 创建标签
     *
     * @param labelRequest 标签请求
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse create(LabelRequest labelRequest) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + LABEL_URI)
                        .method(Method.POST)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(labelRequest))
                        .build()
                );
    }

    /**
     * 查询标签信息
     *
     * @param labelName 标签请名称
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse get(String labelName) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + LABEL_URI + "/" + labelName)
                        .method(Method.GET)
                        .build()
                );
    }

    /**
     * 分页查询标签信息
     *
     * @param cursor 分页游标信息
     * @param limit  分页查询数量
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse get(String cursor, int limit) {

        HttpRequest.Builder builder = HttpRequest.Builder()
                .url(this.context.getBaseUrl() + LABEL_URI)
                .method(Method.GET);

        if (cursor != null && cursor.length() > 0) {
            builder.urlParam("cursor", cursor);
        }

        builder.urlParam("limit", String.valueOf(limit));

        return this.context.getHttpClient().execute(builder.build());
    }

    /**
     * 删除标签信息
     *
     * @param labelName 标签名称
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse delete(String labelName) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + LABEL_URI + "/" + labelName)
                        .method(Method.DELETE)
                        .build()
                );
    }

    /**
     * 分页查询标签用户信息
     *
     * @param labelName 标签名称
     * @param cursor    分页游标
     * @param limit     分页大小
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse getUser(String labelName, String cursor, int limit) {

        HttpRequest.Builder builder = HttpRequest.Builder()
                .url(this.context.getBaseUrl() + String.format(Constant.LABEL_USER_URI_PATTERN,
                        labelName))
                .method(Method.GET);

        if (cursor != null && cursor.length() > 0) {
            builder.urlParam("cursor", cursor);
        }

        builder.urlParam("limit", String.valueOf(limit));

        return this.context.getHttpClient().execute(builder.build());
    }

    /**
     * 查询标签用户信息
     *
     * @param labelName     标签名称
     * @param labelUserName 标签用户名称
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse getUser(String labelName, String labelUserName) {

        String format = String.format(Constant.LABEL_USER_URI_PATTERN, labelName);

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + format + "/" + labelUserName)
                        .method(Method.GET)
                        .build()
                );
    }

    /**
     * 添加标签用户
     *
     * @param labelName        标签名称
     * @param labelUserRequest 标签用户请求
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse addUser(String labelName, LabelUserRequest labelUserRequest) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + String.format(
                                Constant.LABEL_USER_URI_PATTERN, labelName))
                        .method(Method.POST)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(labelUserRequest))
                        .build()
                );
    }

    /**
     * 移除标签用户
     *
     * @param labelName        标签名称
     * @param labelUserRequest 标签用户请求
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse deleteUser(String labelName, LabelUserRequest labelUserRequest) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + String.format(
                                Constant.LABEL_USER_URI_PATTERN, labelName))
                        .method(Method.DELETE)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(labelUserRequest))
                        .build()
                );
    }
}
