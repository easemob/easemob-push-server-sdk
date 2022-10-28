package com.easemob.common.util;

import com.easemob.common.exception.EMException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class BytesUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static byte[] encode(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new EMException(String.format("could not encode object: %s", e.getMessage()), e);
        }
    }

    public static <T> T decode(byte[] bytes, Class<T> tClass) {
        return decode(bytes, 0, bytes.length, tClass);
    }

    public static <T> T decode(byte[] bytes, int offset, int len, Class<T> tClass) {
        try {
            return objectMapper.readValue(bytes, offset, len, tClass);
        } catch (IOException e) {
            throw new EMException(String.format("could not decode class %s: %s", tClass.getName(),
                    e.getMessage()), e);
        }
    }
}
