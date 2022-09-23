package com.easemob.push.api;

import com.easemob.common.Constant;
import com.easemob.push.EMPushContext;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.model.PushRequest;
import com.easemob.push.utils.ByteBufUtil;
import com.easemob.push.utils.ResponseUtil;
import reactor.core.publisher.Mono;

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
    public EMPushHttpResponse single(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.SINGLE_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())))
                .block();
    }

    /**
     * 列表推送，创建任务
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse list(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.LIST_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())))
                .block();
    }

    /**
     * 标签推送，创建任务, 多个标签为运算操作
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse label(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(Constant.LABEL_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(pushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())))
                .block();
    }
}
