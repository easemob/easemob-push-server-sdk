package com.easemob.reactor.push.utils;

import com.easemob.common.util.BytesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class ByteBufUtil {

    public static ByteBuf encode(Object object) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(BytesUtil.encode(object));
        return buffer;
    }

    public static <T> T decode(ByteBuf buffer, Class<T> tClass) {

        byte[] array;
        final int offset;
        int len = buffer.readableBytes();
        if (buffer.hasArray()) {
            array = buffer.array();
            offset = buffer.arrayOffset() + buffer.readerIndex();
        } else {
            array = io.netty.buffer.ByteBufUtil.getBytes(buffer, buffer.readerIndex(), len, false);
            offset = 0;
        }

        return BytesUtil.decode(array, offset, len, tClass);
    }
}
