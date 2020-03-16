package com.paulognr.cursor.api;

import java.util.List;

public interface CursorList<E> extends List<E> {
    boolean hasBefore();
    void setBefore(String before);
    String getBefore();

    boolean hasAfter();
    void setAfter(String after);
    String getAfter();

}
