package com.easemob.push.api.push.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.easemob.push.common.utils.Preconditions;
import com.easemob.push.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.easemob.push.api.push.model.audience.Audience;
import com.easemob.push.api.push.model.notification.AndroidNotification;
import com.easemob.push.api.push.model.notification.IosNotification;
import com.easemob.push.api.push.model.notification.Notification;
import com.easemob.push.api.push.model.notification.PlatformNotification;

/**
 * The object you should build for sending a push.
 * <p>
 * Basically start with newBuilder() method to build a PushPayload object.
 * <p>
 * alertAll() is a shortcut for quickly build payload of alert to all platform and all audience;
 * mesageAll() is a shortcut for quickly build payload of message to all platform and all audience.
 */
public class PushPayload implements PushModel {

    private static final String PLATFORM = "platform";
    private static final String AUDIENCE = "audience";
    private static final String NOTIFICATION = "notification";
    private static final String INAPPMESSAGE = "inapp_message";
    private static final String MESSAGE = "message";
    private static final String OPTIONS = "options";
    private static final String SMS = "sms_message";
    private static final String NOTIFICATION3RD = "notification_3rd";
    private static final String CID = "cid";
    private static final String TARGET = "target";

    /**
     * Definition acording to JPush Docs
     */
    private static final int MAX_GLOBAL_ENTITY_LENGTH = 4000;

    /**
     * Definition acording to JPush Docs
     */
    private static final int MAX_IOS_PAYLOAD_LENGTH = 2000;

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private final Platform platform;
    private final Audience audience;
    private final Notification notification;
    private final InappMessage inappMessage;
    private final Message message;
    private final Notification3rd notification3rd;
    protected Map<String, JsonObject> custom;
    private Options options;
    private SMS sms;
    private String cid;
    private String target;

    private PushPayload(Platform platform, Audience audience,
            Notification notification, InappMessage inappMessage, Message message, Options options,
            SMS sms, Notification3rd notification3rd, String cid, String target,
            Map<String, JsonObject> custom) {
        this.platform = platform;
        this.audience = audience;
        this.notification = notification;
        this.inappMessage = inappMessage;
        this.message = message;
        this.options = options;
        this.sms = sms;
        this.notification3rd = notification3rd;
        this.cid = cid;
        this.target = target;
        this.custom = custom;
    }

    /**
     * The entrance for building a PushPayload object.
     *
     * @return PushPayload builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * The shortcut of building a simple alert notification object to all platforms and all audiences
     *
     * @param alert The alert message.
     * @return PushPayload
     */
    public static PushPayload alertAll(String alert) {
        return new Builder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setNotification(Notification.alert(alert)).build();
    }

    public static PushPayload alertAll(String alert, SMS sms) {
        return new Builder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setNotification(Notification.alert(alert))
                .setSMS(sms)
                .build();
    }

    /**
     * The shortcut of building a simple message object to all platforms and all audiences
     *
     * @param msgContent The message content.
     * @return PushPayload
     */
    public static PushPayload messageAll(String msgContent) {
        return new Builder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setMessage(Message.content(msgContent)).build();
    }

    public static PushPayload messageAll(String msgContent, SMS sms) {
        return new Builder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setMessage(Message.content(msgContent))
                .setSMS(sms)
                .build();
    }

    public static PushPayload fromJSON(String payloadString) {
        return gson.fromJson(payloadString, PushPayload.class);
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getTarget() {
        return target;
    }

    public String getCid() {
        return cid;
    }

    public PushPayload setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public void resetOptionsApnsProduction(boolean apnsProduction) {
        if (null == options) {
            options = Options.newBuilder().setApnsProduction(apnsProduction).build();
        } else {
            options.setApnsProduction(apnsProduction);
        }
    }

    public void resetOptionsTimeToLive(long timeToLive) {
        if (null == options) {
            options = Options.newBuilder().setTimeToLive(timeToLive).build();
        } else {
            options.setTimeToLive(timeToLive);
        }
    }

    public int getSendno() {
        if (null != options) {
            return options.getSendno();
        }
        return 0;
    }

    public Audience getAudience() {
        return audience;
    }

    public Options getOptions() {
        return options;
    }

    @Override
    public JsonElement toJSON() {
        JsonObject json = new JsonObject();
        if (null != platform) {
            json.add(PLATFORM, platform.toJSON());
        }
        if (null != audience) {
            json.add(AUDIENCE, audience.toJSON());
        }
        if (null != notification) {
            json.add(NOTIFICATION, notification.toJSON());
        }
        if (null != inappMessage) {
            json.add(INAPPMESSAGE, inappMessage.toJSON());
        }
        if (null != message) {
            json.add(MESSAGE, message.toJSON());
        }
        if (null != options) {
            json.add(OPTIONS, options.toJSON());
        }
        if (null != sms) {
            json.add(SMS, sms.toJSON());
        }
        if (null != notification3rd) {
            json.add(NOTIFICATION3RD, notification3rd.toJSON());
        }
        if (null != cid) {
            json.addProperty(CID, cid);
        }
        if (null != target) {
            json.addProperty(TARGET, target);
        }
        if (null != custom) {
            for (Map.Entry<String, JsonObject> entry : custom.entrySet()) {
                json.add(entry.getKey(), entry.getValue());
            }
        }

        return json;
    }

    public boolean isGlobalExceedLength() {
        int messageLength = 0;
        JsonObject payload = (JsonObject) this.toJSON();
        if (payload.has(MESSAGE)) {
            JsonObject message = payload.getAsJsonObject(MESSAGE);
            messageLength = message.toString().getBytes().length;
        }
        if (!payload.has(NOTIFICATION)) {
            // only mesage
            return messageLength > MAX_GLOBAL_ENTITY_LENGTH;
        } else {
            JsonObject notification = payload.getAsJsonObject(NOTIFICATION);
            if (notification.has(AndroidNotification.NOTIFICATION_ANDROID)) {
                JsonObject android =
                        notification.getAsJsonObject(AndroidNotification.NOTIFICATION_ANDROID);
                int androidLength = android.toString().getBytes().length;
                return (androidLength + messageLength) > MAX_GLOBAL_ENTITY_LENGTH;
            }
        }
        return false;
    }

    public boolean isIosExceedLength() {
        JsonObject payload = (JsonObject) this.toJSON();
        if (payload.has(NOTIFICATION)) {
            JsonObject notification = payload.getAsJsonObject(NOTIFICATION);
            if (notification.has(IosNotification.NOTIFICATION_IOS)) {
                JsonObject ios = notification.getAsJsonObject(IosNotification.NOTIFICATION_IOS);
                return ios.toString().getBytes().length > MAX_IOS_PAYLOAD_LENGTH;
            } else {
                if (notification.has(PlatformNotification.ALERT)) {
                    String alert = notification.get(PlatformNotification.ALERT).getAsString();
                    JsonObject ios = new JsonObject();
                    ios.add("alert", new JsonPrimitive(alert));
                    return ios.toString().getBytes().length > MAX_IOS_PAYLOAD_LENGTH;
                } else {
                    // No iOS Payload
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }

    public static class Builder {
        private Platform platform = null;
        private Audience audience = null;
        private Notification notification = null;
        private InappMessage inappMessage = null;
        private Message message = null;
        private Options options = null;
        private SMS sms = null;
        private Notification3rd notification3rd = null;
        private String cid;
        private String target;
        private Map<String, JsonObject> custom;

        public Builder() {
            this.custom = new LinkedHashMap<String, JsonObject>();
        }

        public Builder setTarget(String target) {
            this.target = target;
            return this;
        }

        public Builder setPlatform(Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder setAudience(Audience audience) {
            this.audience = audience;
            return this;
        }

        public Builder setNotification(Notification notification) {
            this.notification = notification;
            return this;
        }

        public Builder setInappMessage(InappMessage inappMessage) {
            this.inappMessage = inappMessage;
            return this;
        }

        public Builder setMessage(Message message) {
            this.message = message;
            return this;
        }

        public Builder setOptions(Options options) {
            this.options = options;
            return this;
        }

        public Builder setSMS(SMS sms) {
            this.sms = sms;
            return this;
        }

        public Builder setNotification3rd(Notification3rd notification3rd) {
            this.notification3rd = notification3rd;
            return this;
        }

        public Builder setCid(String cid) {
            this.cid = cid;
            return this;
        }

        public Builder addCustom(String field, JsonObject jsonObject) {
            this.custom.put(field, jsonObject);
            return this;
        }

        public PushPayload build() {

            if (StringUtils.isTrimedEmpty(target)) {
                Preconditions.checkArgument(!(null == audience || null == platform),
                        "audience and platform both should be set.");
            }
            if (!StringUtils.isTrimedEmpty(target)) {
                Preconditions.checkArgument(!StringUtils.isTrimedEmpty(target) && null != platform,
                        "target and platform should be set.");
            }

            Preconditions.checkArgument(!(null == notification && null == message),
                    "notification or message should be set at least one.");

            // if options is not set, a sendno will be generated for tracing easily
            if (null == options) {
                options = Options.sendno();
            }

            return new PushPayload(platform, audience, notification, inappMessage, message, options,
                    sms, notification3rd, cid, target, custom);
        }
    }
}

