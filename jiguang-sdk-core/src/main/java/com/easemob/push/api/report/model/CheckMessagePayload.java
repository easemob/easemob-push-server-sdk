package com.easemob.push.api.report.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.easemob.push.common.utils.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.easemob.push.api.schedule.model.IModel;

public class CheckMessagePayload implements IModel {

    public final static String MSG_ID = "msg_id";
    public final static String REGISTRATION_IDS = "registration_ids";
    public final static String DATE = "date";

    private long msgId = -1L;
    private List<String> registrationIds;
    private String date;
    private Gson gson = new Gson();

    public CheckMessagePayload(long msgId, List<String> rids, String date) {
        this.msgId = msgId;
        this.registrationIds = rids;
        this.date = date;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static boolean isDayFormat(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();
        if (msgId != -1L) {
            jsonObject.addProperty(MSG_ID, msgId);
        }
        if (null != registrationIds) {
            JsonArray jsonArray = new JsonArray();
            for (String rid : registrationIds) {
                jsonArray.add(new JsonPrimitive(rid));
            }
            jsonObject.add(REGISTRATION_IDS, jsonArray);
        }
        if (null != date) {
            jsonObject.addProperty(DATE, date);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }


    public static class Builder {
        private long msgId;
        private List<String> registrationIds = new ArrayList<String>();
        private String date;

        public Builder setMsgId(long msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder setRegistrationIds(String[] rids) {
            Preconditions
                    .checkArgument(rids != null && rids.length > 0, "Registration ids is empty");
            Collections.addAll(registrationIds, rids);
            return this;
        }

        public Builder setRegistrationsIds(List<String> rids) {
            Preconditions
                    .checkArgument(rids != null && rids.size() > 0, "Registration ids is empty");
            registrationIds = rids;
            return this;
        }

        public Builder addRegistrationIds(String... rids) {
            if (null == rids) {
                return this;
            }
            Collections.addAll(registrationIds, rids);
            return this;
        }

        public Builder addRegistrationIds(List<String> rids) {
            registrationIds.addAll(rids);
            return this;
        }

        public Builder setDate(String date) {
            Preconditions.checkArgument(isDayFormat(date), "Date format is invalid");
            this.date = date;
            return this;
        }

        public CheckMessagePayload build() {
            return new CheckMessagePayload(msgId, registrationIds, date);
        }
    }
}
