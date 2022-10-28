package com.easemob.push.utils;

import com.easemob.common.exception.EMException;
import io.netty.buffer.ByteBuf;
import reactor.netty.http.client.HttpClientResponse;

public class HttpResponseChecker {

    public static void check(HttpClientResponse rsp, ByteBuf byteBuf) {
        if (rsp.status().code() != 200) {
            byte[] bytes = io.netty.buffer.ByteBufUtil.getBytes(byteBuf, byteBuf.readerIndex(),
                    byteBuf.readableBytes(), true);
            throw new EMException("request error." + rsp + "data : " + new String(bytes));
        }
    }
}
