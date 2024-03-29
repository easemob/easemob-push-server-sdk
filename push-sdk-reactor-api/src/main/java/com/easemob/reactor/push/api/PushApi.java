package com.easemob.reactor.push.api;

import com.easemob.common.Constant;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.model.PushRequest;
import com.easemob.push.model.SinglePushRequest;
import com.easemob.push.model.SyncPushRequest;
import com.easemob.reactor.push.EMPushContext;
import com.easemob.reactor.push.utils.ByteBufUtil;
import com.easemob.reactor.push.utils.ResponseUtil;
import reactor.core.publisher.Mono;

public class PushApi {

    private final EMPushContext context;

    public PushApi(EMPushContext context) {
        this.context = context;
    }

    /**
     * 同步推送，返回推送结果
     *
     * @param pushRequest
     * @param target
     * @return
     */
    public Mono<EMPushHttpResponse> sync(SyncPushRequest pushRequest, String target) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.SYNC_PUSH_URI + "/" + target)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 列表推送，不创建任务
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> single(SinglePushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.SINGLE_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 列表推送，创建任务
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> list(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.LIST_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())));
    }

    /**
     * 标签推送，创建任务, 多个标签为运算操作
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> label(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.LABEL_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())));
    }
}
