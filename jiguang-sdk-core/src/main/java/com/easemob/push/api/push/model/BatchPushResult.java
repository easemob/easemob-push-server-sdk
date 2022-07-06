package com.easemob.push.api.push.model;

import java.lang.reflect.Type;
import java.util.Map;

import com.easemob.push.common.response.BaseResult;
import com.easemob.push.common.response.ResponseWrapper;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class BatchPushResult extends BaseResult {

    private static final Type RESULT_TYPE = new TypeToken<Map<String, PushResult>>() {
    }.getType();

    private Map<String, PushResult> batchPushResult;

    public static BatchPushResult fromResponse(ResponseWrapper responseWrapper) {

        BatchPushResult result = new BatchPushResult();
        if (responseWrapper.isServerResponse()) {
            result.batchPushResult = gson.fromJson(responseWrapper.responseContent, RESULT_TYPE);
        }

        result.setResponseWrapper(responseWrapper);
        return result;
    }

    public Map<String, PushResult> getBatchPushResult() {
        return batchPushResult;
    }


    public class PushResult {
        @Expose public long msg_id;
        @Expose public Error error;
    }


    public class Error {
        @Expose String message;
        @Expose int code;

        public String getMessage() {
            return this.message;
        }

        public int getCode() {
            return this.code;
        }
    }
}
