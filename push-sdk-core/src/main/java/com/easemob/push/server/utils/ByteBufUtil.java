package com.easemob.push.server.utils;

import com.easemob.push.server.exception.EMException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

public final class ByteBufUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ByteBuf encode(Object object) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            byte[] bytes =objectMapper.writeValueAsBytes(object);
            buffer.writeBytes(bytes);
        } catch (JsonProcessingException e) {
            throw new EMException(String.format("could not encode object: %s", e.getMessage()), e);
        }

        return buffer;
    }

    public static  <T> T decode(ByteBuf buffer, Class<T> tClass) {

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

        try {
            return objectMapper.readValue(array, offset, len, tClass);
        } catch (IOException e) {
            throw new EMException(String.format("could not decode class %s: %s", tClass.getName(),
                    e.getMessage()), e);
        }
    }
}
