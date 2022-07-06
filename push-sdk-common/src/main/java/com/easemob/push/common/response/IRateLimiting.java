package com.easemob.push.common.response;

public interface IRateLimiting {

    public int getRateLimitQuota();

    public int getRateLimitRemaining();

    public int getRateLimitReset();

}

