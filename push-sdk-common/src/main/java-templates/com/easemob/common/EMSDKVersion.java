package com.easemob.common;

public class EMSDKVersion {

    public static String version = "${project.version}";

    public static String getVersion() {
        return EMSDKVersion.version;
    }
}
