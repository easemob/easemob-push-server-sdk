package com.easemob.pushapi.push.model.notification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easemob.push.common.utils.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.easemob.pushapi.push.model.PushModel;

public abstract class PlatformNotification implements PushModel {
    public static final String ALERT = "alert";
    protected static final Logger LOG = LoggerFactory.getLogger(PlatformNotification.class);
    private static final String EXTRAS = "extras";
    private final Map<String, String> extras;
    private final Map<String, Number> numberExtras;
    private final Map<String, Boolean> booleanExtras;
    private final Map<String, JsonObject> jsonExtras;
    private final Map<String, JsonPrimitive> customData;
    private Object alert;

    public PlatformNotification(Object alert, Map<String, String> extras,
            Map<String, Number> numberExtras,
            Map<String, Boolean> booleanExtras,
            Map<String, JsonObject> jsonExtras) {
        this.alert = alert;
        this.extras = extras;
        this.numberExtras = numberExtras;
        this.booleanExtras = booleanExtras;
        this.jsonExtras = jsonExtras;
        customData = new LinkedHashMap<String, JsonPrimitive>();
    }

    public PlatformNotification(Object alert, Map<String, String> extras,
            Map<String, Number> numberExtras,
            Map<String, Boolean> booleanExtras,
            Map<String, JsonObject> jsonExtras,
            Map<String, JsonPrimitive> customData) {
        this.alert = alert;
        this.extras = extras;
        this.numberExtras = numberExtras;
        this.booleanExtras = booleanExtras;
        this.jsonExtras = jsonExtras;
        this.customData = customData;
    }

    @Override
    public JsonElement toJSON() {
        JsonObject json = new JsonObject();

        if (null != alert) {
            if (alert instanceof JsonObject) {
                json.add(ALERT, (JsonObject) alert);
            } else if (alert instanceof IosAlert) {
                json.add(ALERT, ((IosAlert) alert).toJSON());
            } else {
                json.add(ALERT, new JsonPrimitive(alert.toString()));
            }
        }

        JsonObject extrasObject = null;
        if (null != extras || null != numberExtras || null != booleanExtras || null != jsonExtras) {
            extrasObject = new JsonObject();
        }

        if (null != extras) {
            String value = null;
            for (String key : extras.keySet()) {
                value = extras.get(key);
                if (null != value) {
                    extrasObject.add(key, new JsonPrimitive(value));
                }
            }
        }
        if (null != numberExtras) {
            Number value = null;
            for (String key : numberExtras.keySet()) {
                value = numberExtras.get(key);
                if (null != value) {
                    extrasObject.add(key, new JsonPrimitive(value));
                }
            }
        }
        if (null != booleanExtras) {
            Boolean value = null;
            for (String key : booleanExtras.keySet()) {
                value = booleanExtras.get(key);
                if (null != value) {
                    extrasObject.add(key, new JsonPrimitive(value));
                }
            }
        }
        if (null != jsonExtras) {
            JsonObject value = null;
            for (String key : jsonExtras.keySet()) {
                value = jsonExtras.get(key);
                if (null != value) {
                    extrasObject.add(key, value);
                }
            }
        }

        if (null != extras || null != numberExtras || null != booleanExtras || null != jsonExtras) {
            json.add(EXTRAS, extrasObject);
        }

        if (null != customData) {
            for (Map.Entry<String, JsonPrimitive> entry : customData.entrySet()) {
                json.add(entry.getKey(), entry.getValue());
            }
        }

        return json;
    }

    protected Object getAlert() {
        return this.alert;
    }

    protected void setAlert(Object alert) {
        this.alert = alert;
    }

    protected abstract String getPlatform();

    protected abstract static class Builder<T extends PlatformNotification, B extends Builder<T, B>> {
        protected Object alert;
        protected Map<String, String> extrasBuilder;
        protected Map<String, Number> numberExtrasBuilder;
        protected Map<String, Boolean> booleanExtrasBuilder;
        protected Map<String, JsonObject> jsonExtrasBuilder;
        protected Map<String, JsonPrimitive> customData;
        private B theBuilder;

        public Builder() {
            customData = new LinkedHashMap<String, JsonPrimitive>();
            theBuilder = getThis();
        }

        protected abstract B getThis();

        public abstract B setAlert(Object alert);

        public B addExtra(String key, String value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            if (null == value) {
                LOG.debug("Extra value is null, throw away it.");
                return theBuilder;
            }
            if (null == extrasBuilder) {
                extrasBuilder = new HashMap<String, String>();
            }
            extrasBuilder.put(key, value);
            return theBuilder;
        }

        public B addExtras(Map<String, String> extras) {
            if (null == extras) {
                LOG.warn("Null extras param. Throw away it.");
                return theBuilder;
            }

            if (null == extrasBuilder) {
                extrasBuilder = new HashMap<String, String>();
            }
            for (String key : extras.keySet()) {
                extrasBuilder.put(key, extras.get(key));
            }
            return theBuilder;
        }

        public B addExtra(String key, Number value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            if (null == value) {
                LOG.debug("Extra value is null, throw away it.");
                return theBuilder;
            }
            if (null == numberExtrasBuilder) {
                numberExtrasBuilder = new HashMap<String, Number>();
            }
            numberExtrasBuilder.put(key, value);
            return theBuilder;
        }

        public B addExtra(String key, Boolean value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            if (null == value) {
                LOG.debug("Extra value is null, throw away it.");
                return theBuilder;
            }
            if (null == booleanExtrasBuilder) {
                booleanExtrasBuilder = new HashMap<String, Boolean>();
            }
            booleanExtrasBuilder.put(key, value);
            return theBuilder;
        }

        public B addExtra(String key, JsonObject value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            if (null == value) {
                LOG.debug("Extra value is null, throw away it.");
                return theBuilder;
            }
            if (null == jsonExtrasBuilder) {
                jsonExtrasBuilder = new HashMap<String, JsonObject>();
            }
            jsonExtrasBuilder.put(key, value);
            return theBuilder;
        }

        public B addCustom(Map<String, String> extras) {
            for (Map.Entry<String, String> entry : extras.entrySet()) {
                customData.put(entry.getKey(), new JsonPrimitive(entry.getValue()));
            }
            return theBuilder;
        }

        public B addCustom(String key, Number value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            customData.put(key, new JsonPrimitive(value));
            return theBuilder;
        }

        public B addCustom(String key, String value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            customData.put(key, new JsonPrimitive(value));
            return theBuilder;
        }

        public B addCustom(String key, Boolean value) {
            Preconditions.checkArgument(!(null == key), "Key should not be null.");
            customData.put(key, new JsonPrimitive(value));
            return theBuilder;
        }

        public abstract T build();
    }

}
