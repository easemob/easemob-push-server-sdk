package com.easemob.push;

import com.easemob.push.api.LabelApi;
import com.easemob.push.api.PushApi;
import com.easemob.push.api.TaskApi;


public class EMPushService {

    private final EMProperties emProperties;
    private final EMPushContext context;
    private final PushApi pushApi;
    private final TaskApi taskApi;
    private final LabelApi labelApi;

    public EMPushService(EMProperties emProperties) {

        this.emProperties = emProperties;

        this.context = new EMPushContext(this.emProperties);
        this.pushApi = new PushApi(this.context);
        this.taskApi = new TaskApi(this.context);
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
     * 任务 API.<br>
     *
     * @return {@code TaskApi}
     */
    public TaskApi task() {
        return this.taskApi;
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
