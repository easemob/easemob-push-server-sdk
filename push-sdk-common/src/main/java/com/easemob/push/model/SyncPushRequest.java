package com.easemob.push.model;

import com.easemob.common.exception.EMException;

public class SyncPushRequest {

    private final int strategy;
    private final PushMessage pushMessage;

    public SyncPushRequest(int strategy, PushMessage pushMessage) {
        this.strategy = strategy;
        this.pushMessage = pushMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getStrategy() {
        return strategy;
    }

    public PushMessage getPushMessage() {
        return pushMessage;
    }

    public static class Builder {
        /**
         * 推送策略 {@link PushStrategy}
         */
        private PushStrategy strategy = PushStrategy.VENDOR_CHANNEL;
        /**
         * 推送配置
         */
        private PushMessage pushMessage;

        public Builder setStrategy(PushStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder setPushMessage(PushMessage pushMessage) {
            this.pushMessage = pushMessage;
            return this;
        }

        public SyncPushRequest build() {
            if (pushMessage == null) {
                throw new EMException("pushMessage can not null or empty");
            }
            return new SyncPushRequest(strategy.getValue(), pushMessage);
        }
    }
}
