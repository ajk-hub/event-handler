package com.ashimjk.eventhandler.outbox.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class OutboxUtils {

    public static Optional<String> toJsonString(ObjectMapper objectMapper, Object object) {
        try {
            return object == null ? Optional.empty() : Optional.of(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public static String extractFromPayload(String key, String payload) {
        return JsonPath.read(payload, key);
    }

}
