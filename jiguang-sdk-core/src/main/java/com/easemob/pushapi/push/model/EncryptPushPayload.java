package com.easemob.pushapi.push.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.easemob.pushapi.push.model.audience.Audience;

public class EncryptPushPayload {

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private String payload;

    private Audience audience;

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
