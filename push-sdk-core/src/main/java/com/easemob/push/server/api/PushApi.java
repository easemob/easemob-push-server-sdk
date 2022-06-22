package com.easemob.push.server.api;

import com.easemob.push.server.EMPushContext;
import com.easemob.push.server.model.sender.DevicePushRequest;
import com.easemob.push.server.utils.ByteBufUtil;
import com.easemob.push.server.utils.Constant;
import com.easemob.push.server.utils.HttpResponseChecker;
import reactor.core.publisher.Mono;

public class PushApi {

    private final EMPushContext context;

    public PushApi(EMPushContext context) {
        this.context = context;
    }

    public Mono<String> device(DevicePushRequest devicePushRequest) {
        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient.headers(header -> header.add(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON))
                        .post().uri("/push/device")
                        .send(Mono.create(byteBufMonoSink -> byteBufMonoSink.success(
                                ByteBufUtil.encode(devicePushRequest))))
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> {
                            HttpResponseChecker.check(tuple2.getT1(), tuple2.getT2());
                            return ByteBufUtil.decode(tuple2.getT2());
                        }));
    }
}
