package com.easemob.push.server.client;

/**
 * EMPushClient
 *
 * @author MaoChang Wu
 * @date 2022/07/06 15:19
 */
public class EMPushClient {
    private final PushClient pushClient;

    public EMPushClient(PushClient pushClient) {
        this.pushClient = pushClient;
    }
}
