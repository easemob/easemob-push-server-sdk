package com.easemob.push.api;

import com.easemob.common.Constant;
import com.easemob.common.util.BytesUtil;
import com.easemob.http.HttpRequest;
import com.easemob.http.Method;
import com.easemob.push.EMPushContext;
import com.easemob.push.model.EMPushHttpResponse;
import com.easemob.push.model.PushRequest;

import static com.easemob.common.Constant.*;

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
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + SINGLE_PUSH_URI)
                        .method(Method.POST)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(pushRequest))
                        .build()
                );
    }

    /**
     * 列表推送，创建任务
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse list(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + LIST_PUSH_URI)
                        .method(Method.POST)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(pushRequest))
                        .build()
                );
    }

    /**
     * 标签推送，创建任务, 多个标签为运算操作
     *
     * @param pushRequest 推送请求体
     * @return EMPushHttpResponse
     */
    public EMPushHttpResponse label(PushRequest pushRequest) {

        return this.context.getHttpClient()
                .execute(HttpRequest.Builder()
                        .url(this.context.getBaseUrl() + LABEL_PUSH_URI)
                        .method(Method.POST)
                        .header(Constant.CONTENT_TYPE, Constant.APPLICATION_JSON)
                        .body(BytesUtil.encode(pushRequest))
                        .build()
                );
    }
}
