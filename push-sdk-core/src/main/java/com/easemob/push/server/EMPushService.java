package com.easemob.push.server;

import com.easemob.push.server.api.PushApi;

public class EMPushService {

    private final EMProperties emProperties;
    private final EMPushContext context;
    private final PushApi pushApi;

    public EMPushService(EMProperties emProperties) {

        this.emProperties = emProperties;

        this.context = new EMPushContext(this.emProperties);

        this.pushApi = new PushApi(this.context);
    }

    /**
     * 推送 API.<br>
     * 推送方式：<br>
     * - 设备推送<br>
     * <p>
     * 推送通道包含：EASEMOB、APNs、FCM、华为，小米，VIVO、OPPO、魅族
     *
     * @return {@code PushApi}
     */
    public PushApi push() {
        return this.pushApi;
    }

}
