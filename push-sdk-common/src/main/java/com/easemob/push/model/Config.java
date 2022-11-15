package com.easemob.push.model;

public class Config {
    /**
     * 点击行为
     */
    private ClickAction clickAction;

    /**
     * 角标
     */
    private Badge badge;

    public ClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private ClickAction clickAction;
        private Badge badge;

        public Builder clickAction(ClickAction clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        public Builder badge(Badge badge) {
            this.badge = badge;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setClickAction(clickAction);
            config.setBadge(badge);
            return config;
        }
    }
}
