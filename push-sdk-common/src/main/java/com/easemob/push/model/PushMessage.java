package com.easemob.push.model;

import com.easemob.common.exception.EMException;

import java.util.Map;

public class PushMessage {

    /**
     * 推送标题
     */
    private String title;
    /**
     * 推送子标题
     */
    private String subTitle;
    /**
     * 推送内容
     */
    private String content;
    /**
     * 推送扩展
     */
    private Map<String, Object> ext;
    /**
     * 推送配置
     */
    private Config config;

    //推送特性配置内容，详见：https://docs-im.easemob.com/push/apppush/pushkv

    /***
     *  apns 推送配置
     */
    private Map<String, Object> apns;

    /***
     *  vivo 推送配置
     */
    private Map<String, Object> vivo;

    /***
     *  fcm 推送配置
     */
    private Map<String, Object> fcm;

    /***
     *  xiaomi 推送配置
     */
    private Map<String, Object> xiaomi;

    /***
     *  huawei 推送配置
     */
    private Map<String, Object> huawei;

    /***
     *  oppo 推送配置
     */
    private Map<String, Object> oppo;

    /***
     *  meizu 推送配置
     */
    private Map<String, Object> meizu;

    /**
     * 环信通道
     */
    private Map<String, Object> easemob;

    /**
     * fcm v1 配置
     */
    private Map<String, Object> fcmV1;

    /**
     * honor 配置
     */
    private Map<String, Object> honor;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Map<String, Object> getApns() {
        return apns;
    }

    public void setApns(Map<String, Object> apns) {
        this.apns = apns;
    }

    public Map<String, Object> getVivo() {
        return vivo;
    }

    public void setVivo(Map<String, Object> vivo) {
        this.vivo = vivo;
    }

    public Map<String, Object> getFcm() {
        return fcm;
    }

    public void setFcm(Map<String, Object> fcm) {
        this.fcm = fcm;
    }

    public Map<String, Object> getXiaomi() {
        return xiaomi;
    }

    public void setXiaomi(Map<String, Object> xiaomi) {
        this.xiaomi = xiaomi;
    }

    public Map<String, Object> getHuawei() {
        return huawei;
    }

    public void setHuawei(Map<String, Object> huawei) {
        this.huawei = huawei;
    }

    public Map<String, Object> getOppo() {
        return oppo;
    }

    public void setOppo(Map<String, Object> oppo) {
        this.oppo = oppo;
    }

    public Map<String, Object> getMeizu() {
        return meizu;
    }

    public void setMeizu(Map<String, Object> meizu) {
        this.meizu = meizu;
    }

    public Map<String, Object> getEasemob() {
        return easemob;
    }

    public void setEasemob(Map<String, Object> easemob) {
        this.easemob = easemob;
    }

    public Map<String, Object> getFcmV1() {
        return fcmV1;
    }

    public void setFcmV1(Map<String, Object> fcmV1) {
        this.fcmV1 = fcmV1;
    }

    public Map<String, Object> getHonor() {
        return honor;
    }

    public void setHonor(Map<String, Object> honor) {
        this.honor = honor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String subTitle;
        private String content;
        private Map<String, Object> ext;
        private Config config;
        //推送特性配置内容，详见：https://docs-im.easemob.com/push/apppush/pushkv
        private Map<String, Object> apns;
        private Map<String, Object> vivo;
        private Map<String, Object> fcm;
        private Map<String, Object> xiaomi;
        private Map<String, Object> huawei;
        private Map<String, Object> oppo;
        private Map<String, Object> meizu;
        private Map<String, Object> easemob;
        private Map<String, Object> fcmV1;
        private Map<String, Object> honor;

        public PushMessage build() {
            PushMessage pushMessage = new PushMessage();
            if (title == null || title.length() == 0) {
                throw new EMException("title can not null or blank");
            }
            pushMessage.setTitle(title);

            if (subTitle != null && subTitle.length() == 0) {
                throw new EMException("subtitle can not blank");
            }
            pushMessage.setSubTitle(subTitle);

            if (content == null || content.length() == 0) {
                throw new EMException("content can not null or blank");
            }
            pushMessage.setContent(content);

            pushMessage.setExt(ext);
            pushMessage.setConfig(config);
            pushMessage.setApns(apns);
            pushMessage.setVivo(vivo);
            pushMessage.setFcm(fcm);
            pushMessage.setXiaomi(xiaomi);
            pushMessage.setHuawei(huawei);
            pushMessage.setOppo(oppo);
            pushMessage.setMeizu(meizu);
            pushMessage.setEasemob(easemob);
            pushMessage.setFcmV1(fcmV1);
            pushMessage.setHonor(honor);
            return pushMessage;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder ext(Map<String, Object> ext) {
            this.ext = ext;
            return this;
        }

        public Builder config(Config config) {
            this.config = config;
            return this;
        }

        public Builder apns(Map<String, Object> apns) {
            this.apns = apns;
            return this;
        }

        public Builder vivo(Map<String, Object> vivo) {
            this.vivo = vivo;
            return this;
        }

        public Builder fcm(Map<String, Object> fcm) {
            this.fcm = fcm;
            return this;
        }

        public Builder xiaomi(Map<String, Object> xiaomi) {
            this.xiaomi = xiaomi;
            return this;
        }

        public Builder huawei(Map<String, Object> huawei) {
            this.huawei = huawei;
            return this;
        }

        public Builder oppo(Map<String, Object> oppo) {
            this.oppo = oppo;
            return this;
        }

        public Builder meizu(Map<String, Object> meizu) {
            this.meizu = meizu;
            return this;
        }

        public Builder easemob(Map<String, Object> easemob) {
            this.easemob = easemob;
            return this;
        }

        public Builder fcmV1(Map<String, Object> fcmV1) {
            this.fcmV1 = fcmV1;
            return this;
        }

        public Builder honor(Map<String, Object> honor) {
            this.honor = honor;
            return this;
        }
    }
}
