package com.paulognr.cursor.api;

import java.util.List;
import java.util.stream.Collectors;

public final class SortUtils {

    private SortUtils() {
    }

    public static Sort fromQueryParam(List<String> sort) {
        if (sort != null && !sort.isEmpty()) {
            return Sort.by(sort.stream()
                    .filter(field -> field != null && !field.trim().isEmpty())
                    .map(field -> {
                        if (field.startsWith("-")) {
                            return Sort.Order.desc(field.substring(1));
                        } else {
                            return Sort.Order.asc(field);
                        }
                    })
                    .collect(Collectors.toList()));
        }
        return null;
    }
}
