package com.paulognr.cursor.api;

public interface Cursorable {
    int getSize();
    String getAfter();
    String getBefore();

    String getAfterField(String field);
    String getBeforeField(String field);

    Sort getSort();
}
