package com.easemob.push.api.push.model;

import java.util.HashMap;
import java.util.Map;

import com.easemob.push.common.utils.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * sms_message 用于设置短信推送内容以及短信发送的延迟时间。
 */
public class SMS implements PushModel {

    private final String content;
    private final int delay_time;
    private final long temp_id;
    private final Map<String, String> extras;
    private final Map<String, Number> numberExtras;
    private final Map<String, Boolean> booleanExtras;
    private final Map<String, JsonObject> jsonExtras;
    // default is true
    private boolean active_filter;
    // this flag is used to indicate if the active_filter being set
    private boolean is_set_active_filter = false;

    private SMS(String content, int delay_time, long temp_id, boolean active_filter,
            Map<String, String> extras,
            Map<String, Number> numberExtras,
            Map<String, Boolean> booleanExtras,
            Map<String, JsonObject> jsonExtras) {
        this.content = content;
        this.delay_time = delay_time;
        this.temp_id = temp_id;
        this.active_filter = active_filter;
        this.extras = extras;
        this.numberExtras = numberExtras;
        this.booleanExtras = booleanExtras;
        this.jsonExtras = jsonExtras;

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * This will be removed in the future. Please use content(long tempId, int delayTime)  this constructor.
     * Create a SMS content with a delay time.
     * JPush will send a SMS if the message doesn't received within the delay time. If the delay time is 0, the SMS will be sent immediately.
     * Please note the delay time only works on Android.
     * If you are pushing to iOS, the SMS will be sent immediately, whether or not the delay time is 0.
     *
     * @param content   The SMS content.
     * @param delayTime The seconds you want to delay, should be greater than or equal to 0.
     * @return SMS payload.
     */
    @Deprecated
    public static SMS content(String content, int delayTime) {
        return new Builder()
                .setContent(content)
                .setDelayTime(delayTime)
                .build();
    }

    public static SMS content(long tempId, int delayTime) {
        return new Builder()
                .setTempID(tempId)
                .setDelayTime(delayTime)
                .build();
    }

    @Override
    public JsonElement toJSON() {
        JsonObject json = new JsonObject();

        json.addProperty("delay_time", delay_time);

        if (temp_id > 0) {
            json.addProperty("temp_id", temp_id);
        }

        if (null != content) {
            json.addProperty("content", content);
        }

        json.addProperty("active_filter", active_filter);

        JsonObject extrasObject = null;
        if (null != extras || null != numberExtras || null != booleanExtras || null != jsonExtras) {
            extrasObject = new JsonObject();
        }

        if (null != extras) {
            for (String key : extras.keySet()) {
                if (extras.get(key) != null) {
                    extrasObject.add(key, new JsonPrimitive(extras.get(key)));
                } else {
                    extrasObject.add(key, JsonNull.INSTANCE);
                }
            }
        }
        if (null != numberExtras) {
            for (String key : numberExtras.keySet()) {
                extrasObject.add(key, new JsonPrimitive(numberExtras.get(key)));
            }
        }
        if (null != booleanExtras) {
            for (String key : booleanExtras.keySet()) {
                extrasObject.add(key, new JsonPrimitive(booleanExtras.get(key)));
            }
        }
        if (null != jsonExtras) {
            for (String key : jsonExtras.keySet()) {
                extrasObject.add(key, jsonExtras.get(key));
            }
        }

        if (null != extras || null != numberExtras || null != booleanExtras || null != jsonExtras) {
            json.add("temp_para", extrasObject);
        }
        return json;
    }

    public static class Builder {
        protected Map<String, JsonObject> jsonExtrasBuilder;
        private String content;
        private int delay_time;
        private long temp_id;
        private boolean active_filter;
        private boolean is_set_active_filter;
        private Map<String, String> extrasBuilder;
        private Map<String, Number> numberExtrasBuilder;
        private Map<String, Boolean> booleanExtrasBuilder;

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setDelayTime(int delayTime) {
            this.delay_time = delayTime;
            return this;
        }

        public Builder setTempID(long tempID) {
            this.temp_id = tempID;
            return this;
        }

        public Builder setActiveFilter(boolean activeFilter) {
            this.active_filter = activeFilter;
            this.is_set_active_filter = true;
            return this;
        }

        public Builder addPara(String key, String value) {
            Preconditions.checkArgument(!(null == key || null == value),
                    "Key/Value should not be null.");
            if (null == extrasBuilder) {
                extrasBuilder = new HashMap<String, String>();
            }
            extrasBuilder.put(key, value);
            return this;
        }

        public Builder addParas(Map<String, String> extras) {
            Preconditions.checkArgument(!(null == extras), "extras should not be null.");
            if (null == extrasBuilder) {
                extrasBuilder = new HashMap<String, String>();
            }
            for (String key : extras.keySet()) {
                extrasBuilder.put(key, extras.get(key));
            }
            return this;
        }

        public Builder addPara(String key, Number value) {
            Preconditions.checkArgument(!(null == key || null == value),
                    "Key/Value should not be null.");
            if (null == numberExtrasBuilder) {
                numberExtrasBuilder = new HashMap<String, Number>();
            }
            numberExtrasBuilder.put(key, value);
            return this;
        }

        public Builder addPara(String key, Boolean value) {
            Preconditions.checkArgument(!(null == key || null == value),
                    "Key/Value should not be null.");
            if (null == booleanExtrasBuilder) {
                booleanExtrasBuilder = new HashMap<String, Boolean>();
            }
            booleanExtrasBuilder.put(key, value);
            return this;
        }

        public Builder addPara(String key, JsonObject value) {
            Preconditions.checkArgument(!(null == key || null == value),
                    "Key/Value should not be null.");
            if (null == jsonExtrasBuilder) {
                jsonExtrasBuilder = new HashMap<String, JsonObject>();
            }
            jsonExtrasBuilder.put(key, value);
            return this;
        }

        public SMS build() {
            Preconditions.checkArgument(delay_time >= 0,
                    "The delay time must be greater than or equal to 0");

            // if active filter not being set, will default set it to true.
            if (is_set_active_filter == false) {
                active_filter = true;
            }

            return new SMS(content, delay_time, temp_id, active_filter,
                    extrasBuilder, numberExtrasBuilder, booleanExtrasBuilder, jsonExtrasBuilder);
        }

    }
}
