package com.easemob.push.api.schedule;

import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;

public class ScheduleMsgIdsResult extends BaseResult {

    private static final long serialVersionUID = 995450157929893257L;

    @Expose int count;

    @Expose List<String> msgids;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getMsgids() {
        return msgids;
    }

    public void setMsgids(List<String> msgids) {
        this.msgids = msgids;
    }

}
