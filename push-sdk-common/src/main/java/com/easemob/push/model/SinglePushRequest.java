package com.easemob.push.model;

import com.easemob.common.exception.EMException;

import java.util.List;

public class SinglePushRequest {

    private final List<String> targets;
    private final int strategy;
    private final PushMessage pushMessage;

    public SinglePushRequest(List<String> targets, int strategy, PushMessage pushMessage) {
        this.targets = targets;
        this.strategy = strategy;
        this.pushMessage = pushMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getTargets() {
        return targets;
    }

    public int getStrategy() {
        return strategy;
    }

    public PushMessage getPushMessage() {
        return pushMessage;
    }

    public static class Builder {
        /**
         * 推送目标列表
         */
        private List<String> targets;
        /**
         * 推送策略 {@link PushStrategy}
         */
        private PushStrategy strategy = PushStrategy.VENDOR_CHANNEL;
        /**
         * 推送配置
         */
        private PushMessage pushMessage;

        public Builder setTargets(List<String> targets) {
            this.targets = targets;
            return this;
        }

        public Builder setStrategy(PushStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder setPushMessage(PushMessage pushMessage) {
            this.pushMessage = pushMessage;
            return this;
        }

        public SinglePushRequest build() {
            if (targets == null || targets.isEmpty()) {
                throw new EMException("targets can not null or empty");
            }
            if (pushMessage == null) {
                throw new EMException("pushMessage can not null or empty");
            }
            return new SinglePushRequest(targets, strategy.getValue(), pushMessage);
        }
    }
}
