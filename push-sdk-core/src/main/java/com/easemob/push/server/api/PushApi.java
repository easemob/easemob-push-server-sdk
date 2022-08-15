package com.easemob.push.server.api;

import com.easemob.push.server.EMPushContext;
import com.easemob.push.server.model.EMPushHttpResponse;
import com.easemob.push.server.model.sender.DevicePushRequest;
import com.easemob.push.server.model.sender.PushRequest;
import com.easemob.push.server.utils.ByteBufUtil;
import com.easemob.push.server.utils.Constant;
import reactor.core.publisher.Mono;

import static com.easemob.push.server.utils.Constant.*;

public class PushApi {

    private final EMPushContext context;

    public PushApi(EMPushContext context) {
        this.context = context;
    }

    /**
     * 根据设备deviceToken 和 证书名称 进行推送
     *
     * @param devicePushRequest 设备推送请求体
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> device(DevicePushRequest devicePushRequest) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(
                                header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post()
                        .uri(DEVICE_PUSH_URI)
                        .send(Mono.just(ByteBufUtil.encode(devicePushRequest)))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
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
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
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
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
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
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }
}
