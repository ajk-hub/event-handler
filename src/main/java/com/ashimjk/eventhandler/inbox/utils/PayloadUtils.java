package com.ashimjk.eventhandler.inbox.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class PayloadUtils {

    public <T> T toValue(ObjectMapper objectMapper, String value, Class<T> clazz) throws JsonProcessingException {
        if (value == null) {
            throw new IllegalArgumentException("can not parse null");
        } else {
            return objectMapper.readValue(value, clazz);
        }
    }

    public Optional<String> toString(ObjectMapper objectMapper, Object value) {
        try {
            return value == null ? Optional.empty() : Optional.of(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

}
