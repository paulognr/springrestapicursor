package com.paulognr.cursor.api;

public interface Cursorable {

    String KEY_ID = "id";
    String KEY_SIZE = "size";
    String KEY_SORT = "sort";

    int getSize();
    String getAfter();
    String getBefore();

    String getAfterField(String field);
    String getBeforeField(String field);

    Sort getSort();
}
