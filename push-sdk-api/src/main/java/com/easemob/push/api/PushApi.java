package com.easemob.push.api;

import com.easemob.Constant;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.model.PushRequest;
import com.easemob.push.EMPushContext;
import com.easemob.utils.ByteBufUtil;
import com.easemob.utils.ResponseUtil;
import reactor.core.publisher.Mono;

import static com.easemob.Constant.*;

public class PushApi {

    private final EMPushContext context;

    public PushApi(EMPushContext context) {
        this.context = context;
    }

    /**
     * 列表推送，不创建任务
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> single(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(SINGLE_PUSH_URI)
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
                        .uri(LIST_PUSH_URI)
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
                        .uri(LABEL_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())));
    }
}
