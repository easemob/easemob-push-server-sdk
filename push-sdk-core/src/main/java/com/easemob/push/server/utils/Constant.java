package com.easemob.push.server.utils;

public class Constant {

    public static final String DNS_BASE_URL = "http://rs.easemob.com";
    public static final String DNS_URI_PATTERN = "/easemob/server.json?app_key=%s";
    public static final String TOKEN_URI_PATTERN = "/%s/token";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";


    public static final String DEVICE_PUSH_URI = "/push/device";
    public static final String SINGLE_PUSH_URI = "/push/single";
    public static final String LIST_PUSH_URI = "/push/list";
    public static final String LABEL_PUSH_URI = "/push/list/label";


    public static final String TASKS_URI = "/push/tasks";
    public static final String TASK_URI = "/push/task";


    public static final String LABEL_URI = "/push/label";
    public static final String LABEL_USER_URI_PATTERN = "/push/label/%s/user";
}
