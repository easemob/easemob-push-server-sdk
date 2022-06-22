package com.easemob.push.server.utils;

import com.easemob.push.server.exception.EMException;
import io.netty.buffer.ByteBuf;
import reactor.netty.http.client.HttpClientResponse;

public class HttpResponseChecker {

    public static void check(HttpClientResponse rsp, ByteBuf byteBuf) {
        if (rsp.status().code() != 200) {
            throw new EMException("request error." + rsp + "data : " + ByteBufUtil.decode(byteBuf));
        }
    }
}
