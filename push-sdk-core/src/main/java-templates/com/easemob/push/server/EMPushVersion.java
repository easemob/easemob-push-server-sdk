package com.easemob.push.server;

public class EMPushVersion {

    public static String version = "${project.version}";

    public static String getVersion() {
        return EMPushVersion.version;
    }
}
