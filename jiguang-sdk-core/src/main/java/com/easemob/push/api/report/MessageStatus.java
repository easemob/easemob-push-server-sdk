package com.easemob.push.api.report;

import java.io.Serializable;

public class MessageStatus implements Serializable {

    /**
     * 0  送达
     * 1  未送达
     * 2  用户不属于该 app
     * 3  用户属于该 app，但不属于该 msgid
     * 4  系统异常
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "status : " + status;
    }
}
