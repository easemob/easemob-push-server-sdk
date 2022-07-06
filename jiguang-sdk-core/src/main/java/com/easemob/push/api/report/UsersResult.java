package com.easemob.push.api.report;

import java.util.ArrayList;
import java.util.List;

import com.easemob.push.common.TimeUnit;
import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsersResult extends BaseResult {

    private static final long serialVersionUID = -963296929272770550L;
    @Expose public TimeUnit time_unit;
    @Expose public String start;
    @Expose public int duration;
    @Expose public List<User> items = new ArrayList<User>();


    public static class User {
        @Expose public String time;
        @Expose public Android android;
        @Expose public Ios ios;
    }


    public static class Android {
        @SerializedName("new") @Expose public long add;
        @Expose public int online;
        @Expose public int active;
    }


    public static class Ios {
        @SerializedName("new") @Expose public long add;
        @Expose public int online;
        @Expose public int active;
    }

}


