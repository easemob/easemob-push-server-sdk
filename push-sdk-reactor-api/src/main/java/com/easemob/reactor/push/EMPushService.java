package com.easemob.reactor.push;

import com.easemob.reactor.push.api.LabelApi;
import com.easemob.reactor.push.api.PushApi;

public class EMPushService {

    private final EMPushProperties emPushProperties;
    private final EMPushContext context;
    private final PushApi pushApi;
    private final LabelApi labelApi;

    public EMPushService(EMPushProperties emPushProperties) {

        this.emPushProperties = emPushProperties;

        this.context = new EMPushContext(this.emPushProperties);
        this.pushApi = new PushApi(this.context);
        this.labelApi = new LabelApi(this.context);
    }

    /**
     * 推送 API.<br>
     * <p>
     * 推送通道包含：EASEMOB、APNs、FCM、华为，小米，VIVO、OPPO、魅族
     *
     * @return {@code PushApi}
     */
    public PushApi push() {
        return this.pushApi;
    }

    /**
     * 标签 API.<br>
     *
     * @return {@code LabelApi}
     */
    public LabelApi label() {
        return this.labelApi;
    }
}
