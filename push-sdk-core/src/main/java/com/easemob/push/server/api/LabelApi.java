package com.easemob.push.server.api;

import com.easemob.push.server.EMPushContext;
import com.easemob.push.server.model.EMPushHttpResponse;
import com.easemob.push.server.model.LabelRequest;
import com.easemob.push.server.model.LabelUserRequest;
import com.easemob.push.server.utils.ByteBufUtil;
import com.easemob.push.server.utils.Constant;
import io.netty.handler.codec.http.QueryStringEncoder;
import reactor.core.publisher.Mono;

import static com.easemob.push.server.utils.Constant.*;

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
    public Mono<EMPushHttpResponse> create(LabelRequest labelRequest) {
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(LABEL_URI)
                        .send(Mono.just(ByteBufUtil.encode(labelRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 查询标签信息
     *
     * @param labelName 标签请名称
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> get(String labelName) {
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(LABEL_URI + "/" + labelName)
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 分页查询标签信息
     *
     * @param cursor 分页游标信息
     * @param limit  分页查询数量
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> get(String cursor, int limit) {

        QueryStringEncoder encoder = new QueryStringEncoder(LABEL_URI);
        if (cursor != null && cursor.length() > 0) {
            encoder.addParam("cursor", cursor);
        }
        encoder.addParam("limit", String.valueOf(limit));

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(encoder.toString())
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 删除标签信息
     *
     * @param labelName 标签名称
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> delete(String labelName) {
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .delete()
                        .uri(LABEL_URI + "/" + labelName)
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 分页查询标签用户信息
     *
     * @param labelName 标签名称
     * @param cursor 分页游标
     * @param limit 分页大小
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> getUser(String labelName, String cursor, int limit) {

        QueryStringEncoder encoder =
                new QueryStringEncoder(String.format(LABEL_USER_URI_PATTERN, labelName));
        if (cursor != null && cursor.length() > 0) {
            encoder.addParam("cursor", cursor);
        }
        encoder.addParam("limit", String.valueOf(limit));

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(encoder.toString())
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 查询标签用户信息
     *
     * @param labelName     标签名称
     * @param labelUserName 标签用户名称
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> getUser(String labelName, String labelUserName) {

        String format = String.format(LABEL_USER_URI_PATTERN, labelName);

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(format + "/" + labelUserName)
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 添加标签用户
     *
     * @param labelName        标签名称
     * @param labelUserRequest 标签用户请求
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> addUser(String labelName, LabelUserRequest labelUserRequest) {
        String format = String.format(LABEL_USER_URI_PATTERN, labelName);
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(format)
                        .send(Mono.just(ByteBufUtil.encode(labelUserRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 移除标签用户
     *
     * @param labelName        标签名称
     * @param labelUserRequest 标签用户请求
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> deleteUser(String labelName, LabelUserRequest labelUserRequest) {
        String format = String.format(LABEL_USER_URI_PATTERN, labelName);
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .delete()
                        .uri(format)
                        .send(Mono.just(ByteBufUtil.encode(labelUserRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }
}
