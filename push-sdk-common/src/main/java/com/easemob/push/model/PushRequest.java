package com.easemob.push.model;

import com.easemob.common.exception.EMException;

import java.util.List;

public class PushRequest {

    private final List<String> targets;
    private final int strategy;
    private final String startDate;
    private final PushMessage pushMessage;

    public PushRequest(List<String> targets, int strategy, String startDate,
            PushMessage pushMessage) {
        this.targets = targets;
        this.strategy = strategy;
        this.startDate = startDate;
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

    public String getStartDate() {
        return startDate;
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
         * 开始时间，定时任务
         */
        private String startDate;
        /**
         * 推送配置
         */
        private PushMessage pushMessage;

        public Builder setAsync(boolean async) {
            return this;
        }

        public Builder setTargets(List<String> targets) {
            this.targets = targets;
            return this;
        }

        public Builder setStrategy(PushStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setPushMessage(PushMessage pushMessage) {
            this.pushMessage = pushMessage;
            return this;
        }

        public PushRequest build() {
            if (targets == null || targets.isEmpty()) {
                throw new EMException("targets can not null or empty");
            }
            if (pushMessage == null) {
                throw new EMException("pushMessage can not null or empty");
            }
            return new PushRequest(targets, strategy.getValue(), startDate, pushMessage);
        }
    }
}
