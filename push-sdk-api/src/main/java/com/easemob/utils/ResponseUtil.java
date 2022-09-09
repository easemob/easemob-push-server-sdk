package com.easemob.utils;

import com.easemob.push.model.EMPushHttpResponse;
import io.netty.buffer.ByteBuf;
import reactor.netty.http.client.HttpClientResponse;

import java.util.HashMap;
import java.util.Map;

public final class ResponseUtil {

    public static EMPushHttpResponse of(HttpClientResponse httpResponse, ByteBuf byteBuf) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> entry : httpResponse.responseHeaders()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return new EMPushHttpResponse(httpResponse.status().code(),
                httpResponse.status().reasonPhrase(),
                httpResponse.version().toString(),
                map,
                io.netty.buffer.ByteBufUtil.getBytes(byteBuf, byteBuf.readerIndex(),
                        byteBuf.readableBytes(), true)
        );
    }
}
