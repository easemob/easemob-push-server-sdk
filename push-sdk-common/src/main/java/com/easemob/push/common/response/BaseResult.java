package com.easemob.push.common.response;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseResult implements IRateLimiting, Serializable {
    public static final int ERROR_CODE_NONE = -1;
    public static final int ERROR_CODE_OK = 0;
    public static final String ERROR_MESSAGE_NONE = "None error message.";

    protected static final int RESPONSE_OK = 200;
    private static final long serialVersionUID = 4810924314887130678L;
    protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private ResponseWrapper responseWrapper;

    public static <T extends BaseResult> T fromResponse(
            ResponseWrapper responseWrapper, Class<T> clazz) {
        T result = null;

        if (responseWrapper.isServerResponse()) {
            result = gson.fromJson(responseWrapper.responseContent, clazz);
        } else {
            try {
                result = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        result.setResponseWrapper(responseWrapper);

        return result;
    }

    public void setResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    public String getOriginalContent() {
        if (null != responseWrapper) {
            return responseWrapper.responseContent;
        }
        return null;
    }

    public int getResponseCode() {
        if (null != responseWrapper) {
            return responseWrapper.responseCode;
        }
        return -1;
    }

    public boolean isResultOK() {
        if (null != responseWrapper) {
            return (responseWrapper.responseCode / 200) == 1;
        }
        return false;
    }

    public int getRateLimitQuota() {
        if (null != responseWrapper) {
            return responseWrapper.rateLimitQuota;
        }
        return 0;
    }

    public int getRateLimitRemaining() {
        if (null != responseWrapper) {
            return responseWrapper.rateLimitRemaining;
        }
        return 0;
    }

    public int getRateLimitReset() {
        if (null != responseWrapper) {
            return responseWrapper.rateLimitReset;
        }
        return 0;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
