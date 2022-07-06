package com.easemob.push.api.schedule;

import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;

public class ScheduleListResult extends BaseResult {

    private static final long serialVersionUID = 86248096939746151L;
    @Expose int total_count;
    @Expose int total_pages;
    @Expose int page;
    @Expose List<ScheduleResult> schedules;

    public int getTotal_count() {
        return total_count;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getPage() {
        return page;
    }

    public List<ScheduleResult> getSchedules() {
        return schedules;
    }
}
