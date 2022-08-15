package com.easemob.push.server.api;

import com.easemob.push.server.EMPushContext;
import com.easemob.push.server.model.EMPushHttpResponse;
import io.netty.handler.codec.http.QueryStringEncoder;
import reactor.core.publisher.Mono;

import static com.easemob.push.server.utils.Constant.*;

public class TaskApi {

    private final EMPushContext context;

    public TaskApi(EMPushContext context) {
        this.context = context;
    }

    /**
     * 分页查询任务列表
     *
     * @param pageNum  页码，从0开始
     * @param pageSize 页大小， 最大值 100
     * @return EMPushHttpResponse
     */
    public Mono<EMPushHttpResponse> getAllTaskByPage(int pageNum, int pageSize) {

        QueryStringEncoder encoder = new QueryStringEncoder(TASKS_URI);
        encoder.addParam("page", String.valueOf(pageNum));
        encoder.addParam("size", String.valueOf(pageSize));

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(encoder.toString())
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }

    public Mono<EMPushHttpResponse> getTaskById(long taskId) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(TASK_URI + "/" + taskId)
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> EMPushHttpResponse.of(tuple2.getT1(), tuple2.getT2())));
    }
}
