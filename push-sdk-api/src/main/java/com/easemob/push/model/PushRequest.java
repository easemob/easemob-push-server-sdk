package com.easemob.push.model;

import com.easemob.exception.EMException;

import java.util.List;
import java.util.Map;

public class PushRequest {

    private final boolean async;
    private final List<String> targets;
    private final int strategy;
    private final String startDate;
    private final Map<String, Object> pushMessage;

    public PushRequest(boolean async, List<String> targets, int strategy, String startDate,
            Map<String, Object> pushMessage) {
        this.async = async;
        this.targets = targets;
        this.strategy = strategy;
        this.startDate = startDate;
        this.pushMessage = pushMessage;
    }

    public boolean isAsync() {
        return async;
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

    public Map<String, Object> getPushMessage() {
        return pushMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        /**
         * 推送请求是否异步完成，默认：true （同步会返回通道推送结果，但性能较差）
         */
        private boolean async = true;
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
        private Map<String, Object> pushMessage;

        public Builder setAsync(boolean async) {
            this.async = async;
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

        public Builder setPushMessage(Map<String, Object> pushMessage) {
            this.pushMessage = pushMessage;
            return this;
        }

        public PushRequest build() {
            if (targets == null || targets.isEmpty()) {
                throw new EMException("targets can not null or empty");
            }
            if (pushMessage == null || pushMessage.isEmpty()) {
                throw new EMException("pushMessage can not null or empty");
            }
            if (!async && targets.size() > 1) {
                throw new EMException("sync push just for one targets");
            }
            return new PushRequest(async, targets, strategy.getValue(), startDate, pushMessage);
        }
    }
}
