package com.easemob.push.model;

public class ClickAction {
    /**
     * 浏览器 url
     */
    private String url;
    /**
     * 应用内页信息，不同厂商需要的内容不同，通用情况下，建议两个都配置
     */
    private String action;
    private String activity;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String url;
        private String action;
        private String activity;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder activity(String activity) {
            this.activity = activity;
            return this;
        }

        public ClickAction build() {
            ClickAction clickAction = new ClickAction();
            clickAction.setUrl(url);
            clickAction.setAction(action);
            clickAction.setActivity(activity);
            return clickAction;
        }
    }
}
