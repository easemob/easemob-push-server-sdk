package com.easemob.push.api;

import com.easemob.common.Constant;
import com.easemob.push.EMPushContext;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.utils.ResponseUtil;
import io.netty.handler.codec.http.QueryStringEncoder;
import reactor.core.publisher.Mono;


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
    public EMPushHttpResponse getAllTaskByPage(int pageNum, int pageSize) {

        QueryStringEncoder encoder = new QueryStringEncoder(Constant.TASKS_URI);
        encoder.addParam("page", String.valueOf(pageNum));
        encoder.addParam("size", String.valueOf(pageSize));

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(encoder.toString())
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())))
                .block();
    }

    public EMPushHttpResponse getTaskById(long taskId) {

        return this.context.getHttpClient()
                .flatMap(httpClient -> httpClient
                        .get()
                        .uri(Constant.TASK_URI + "/" + taskId)
                        .responseSingle((rsp, buf) -> Mono.zip(Mono.just(rsp), buf))
                        .map(tuple2 -> ResponseUtil.of(tuple2.getT1(), tuple2.getT2())))
                .block();
    }
}
