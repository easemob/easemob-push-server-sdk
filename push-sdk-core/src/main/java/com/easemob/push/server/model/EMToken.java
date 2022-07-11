package com.easemob.push.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EMToken
 *
 * @author MaoChang Wu
 * @date 2022/07/11 14:35
 */
public class EMToken {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiredAt;


    public EMToken() {
    }

    public EMToken(String accessToken, Long expiredAt) {
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
    }


    public boolean isExpired() {
        return System.currentTimeMillis() > expiredAt;
    }

    public void setExpiredAt(long expiredAt){
        this.expiredAt = expiredAt   * 1000 + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return accessToken;
    }
}
