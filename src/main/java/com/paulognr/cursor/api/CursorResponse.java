package com.paulognr.cursor.api;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CursorResponse {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> fields = new HashMap<>();
    private int size;

    private CursorResponse(int size) {
        this.size = size;
    }

    public static CursorResponse of (int size) {
        return new CursorResponse(size);
    }

    public CursorResponse add(String field, Object value) {
        this.fields.put(field, value);
        return this;
    }

    public String encode() {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("size", this.size);
        this.fields.forEach((key, value) -> json.putPOJO(key, value));
        try {
            String response = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return Base64.getEncoder().encodeToString(response.getBytes());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
