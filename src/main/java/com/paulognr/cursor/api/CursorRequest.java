package com.paulognr.cursor.api;

import java.util.Arrays;
import java.util.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CursorRequest implements Cursorable{

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private int size;
    private String after;
    private String before;

    private JsonNode afterJsonNode;
    private JsonNode beforeJsonNode;

    private Sort sort;

    private CursorRequest(int size) {
        this.size = size;
    }

    private CursorRequest(int size, Sort sort) {
        this.size = size;
        this.sort = sort;
    }

    public static CursorRequest of(int size, Sort sort) {
        return new CursorRequest(size, sort);
    }

    public static CursorRequest after(String after) {
        String decoded = new String(Base64.getDecoder().decode(after));
        CursorRequest cursorRequest = new CursorRequest(loadSize(decoded));
        cursorRequest.afterJsonNode = loadJsonNode(decoded);
        cursorRequest.sort = loadSort(cursorRequest.afterJsonNode);
        cursorRequest.after = decoded;
        return cursorRequest;
    }

    public static CursorRequest before(String before) {
        String decoded = new String(Base64.getDecoder().decode(before));
        CursorRequest cursorRequest = new CursorRequest(loadSize(decoded));
        cursorRequest.beforeJsonNode = loadJsonNode(decoded);
        cursorRequest.sort = loadSort(cursorRequest.beforeJsonNode);
        cursorRequest.before = decoded;
        return cursorRequest;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public String getAfter() {
        return this.after;
    }

    @Override
    public String getBefore() {
        return this.before;
    }

    @Override
    public String getAfterField(String field) {
        if (this.afterJsonNode != null) {
            return this.afterJsonNode.get(field).asText();
        }
        return null;
    }

    @Override
    public String getBeforeField(String field) {
        if (this.beforeJsonNode != null) {
            return this.beforeJsonNode.get(field).asText();
        }
        return null;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    private static int loadSize(String decoded) {
        JsonNode json = loadJsonNode(decoded);
        if (json != null) {
            return json.get(Cursorable.KEY_SIZE).asInt();
        }
        return 0;
    }

    private static Sort loadSort(JsonNode json) {
        if (json != null) {
            String sortText = json.get(Cursorable.KEY_SORT).asText();
            if (sortText != null) {
                return SortUtils.fromQueryParam(Arrays.asList(sortText.split(",")));
            }
        }
        return null;
    }

    private static JsonNode loadJsonNode(String decoded) {
        try {
            return objectMapper.reader().readTree(decoded);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
