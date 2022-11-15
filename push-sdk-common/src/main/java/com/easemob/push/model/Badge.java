package com.easemob.push.model;

public class Badge {
    /**
     * 自增
     */
    private Integer addNum;
    /**
     * 覆盖，
     */
    private Integer setNum;
    /**
     * 应用入口类，华为推送角标必须
     */
    private String activity;

    public Integer getAddNum() {
        return addNum;
    }

    public void setAddNum(Integer addNum) {
        this.addNum = addNum;
    }

    public Integer getSetNum() {
        return setNum;
    }

    public void setSetNum(Integer setNum) {
        this.setNum = setNum;
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
        private Integer addNum;
        private Integer setNum;
        private String activity;

        public Builder addNum(Integer addNum) {
            this.addNum = addNum;
            return this;
        }

        public Builder setNum(Integer setNum) {
            this.setNum = setNum;
            return this;
        }

        public Builder activity(String activity) {
            this.activity = activity;
            return this;
        }

        public Badge build() {
            Badge badge = new Badge();
            badge.setAddNum(addNum);
            badge.setSetNum(setNum);
            badge.setActivity(activity);
            return badge;
        }
    }
}
