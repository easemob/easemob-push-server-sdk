package com.easemob.push.api.report;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.easemob.push.common.response.ResponseWrapper;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class MessageDetailResult extends BaseResult {

    private static final Type RECEIVED_TYPE = new TypeToken<List<Received>>() {
    }.getType();
    private static final long serialVersionUID = 156439166846147394L;

    @Expose
    public List<Received> received_list = new ArrayList<Received>();

    static MessageDetailResult fromResponse(ResponseWrapper responseWrapper) {
        MessageDetailResult result = new MessageDetailResult();
        if (responseWrapper.isServerResponse()) {
            result.received_list = gson.fromJson(responseWrapper.responseContent, RECEIVED_TYPE);
        }

        result.setResponseWrapper(responseWrapper);
        return result;
    }


    public static class Received {
        @Expose
        public long msg_id;
        @Expose
        public JpushDetail jpush;
        @Expose
        public JsonObject android_pns;
        @Expose
        public JsonObject details;
        @Expose
        public IosDetail ios;
        @Expose
        public WinphoeDetail winphone;
    }


    public static class JpushDetail {
        @Expose
        public long target;
        @Expose
        public int online_push;
        @Expose
        public int received;
        @Expose
        public int click;
        @Expose
        public int msg_click;
    }


    public static class WinphoeDetail {
        @Expose
        public long mpns_target;
        @Expose
        public int mpns_sent;
        @Expose
        public int click;
    }


    public static class IosDetail {
        @Expose
        public long apns_target;
        @Expose
        public int apns_sent;
        @Expose
        public int apns_received;
        @Expose
        public int apns_click;
        @Expose
        public int msg_target;
        @Expose
        public int msg_received;
    }

}
