package com.easemob.push.server.model.sender;

import java.util.List;
import java.util.Map;

public class DevicePushRequest {

    private final boolean async;
    private final List<String> deviceToken;
    private final String notifierName;
    private final Map<String, Object> pushMessage;

    public DevicePushRequest(boolean async, List<String> deviceToken, String notifierName,
            Map<String, Object> pushMessage) {
        this.async = async;
        this.deviceToken = deviceToken;
        this.notifierName = notifierName;
        this.pushMessage = pushMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isAsync() {
        return async;
    }

    public List<String> getDeviceToken() {
        return deviceToken;
    }

    public String getNotifierName() {
        return notifierName;
    }

    public Map<String, Object> getPushMessage() {
        return pushMessage;
    }

    public static class Builder {
        private boolean async = true;
        private List<String> deviceToken;
        private String notifierName;
        private Map<String, Object> pushMessage;

        public Builder setAsync(boolean async) {
            this.async = async;
            return this;
        }

        public Builder setDeviceToken(List<String> deviceToken) {
            this.deviceToken = deviceToken;
            return this;
        }

        public Builder setNotifierName(String notifierName) {
            this.notifierName = notifierName;
            return this;
        }

        public Builder setPushMessage(Map<String, Object> pushMessage) {
            this.pushMessage = pushMessage;
            return this;
        }

        public DevicePushRequest build() {
            return new DevicePushRequest(async, deviceToken, notifierName, pushMessage);
        }
    }
}
