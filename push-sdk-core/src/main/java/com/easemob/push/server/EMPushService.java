package com.easemob.push.server;

import com.easemob.push.server.api.LabelApi;
import com.easemob.push.server.api.PushApi;
import com.easemob.push.server.api.TaskApi;

public class EMPushService {

    private final EMProperties emProperties;
    private final EMPushContext context;
    private PushApi pushApi;
    private TaskApi taskApi;
    private LabelApi labelApi;

    public EMPushService(EMProperties emProperties) {

        this.emProperties = emProperties;

        this.context = new EMPushContext(this.emProperties);
    }

    /**
     * 推送 API.<br>
     * <p>
     * 推送通道包含：EASEMOB、APNs、FCM、华为，小米，VIVO、OPPO、魅族
     *
     * @return {@code PushApi}
     */
    public PushApi push() {
        if (pushApi == null) {
            synchronized (PushApi.class) {
                if (pushApi == null) {
                    this.pushApi = new PushApi(this.context);
                }
            }
        }
        return this.pushApi;
    }

    /**
     * 任务 API.<br>
     *
     * @return {@code TaskApi}
     */
    public TaskApi task() {
        if (taskApi == null) {
            synchronized (TaskApi.class) {
                if (taskApi == null) {
                    this.taskApi = new TaskApi(this.context);
                }
            }
        }
        return this.taskApi;
    }

    /**
     * 标签 API.<br>
     *
     * @return {@code LabelApi}
     */
    public LabelApi label() {
        if (labelApi == null) {
            synchronized (LabelApi.class) {
                if (labelApi == null) {
                    this.labelApi = new LabelApi(this.context);
                }
            }
        }
        return this.labelApi;
    }
}
