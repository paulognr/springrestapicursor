package com.paulognr.cursor.api;

import java.io.Serializable;

public final class Cursor implements Serializable {

    private String before;
    private boolean hasBefore;
    private String after;
    private boolean hasAfter;

    public Cursor() {}
    public Cursor(CursorList cursorList) {
        super();
        this.before = cursorList.getBefore();
        this.hasBefore = cursorList.hasBefore();
        this.after = cursorList.getAfter();
        this.hasAfter = cursorList.hasAfter();
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public boolean isHasBefore() {
        return hasBefore;
    }

    public void setHasBefore(boolean hasBefore) {
        this.hasBefore = hasBefore;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public boolean isHasAfter() {
        return hasAfter;
    }

    public void setHasAfter(boolean hasAfter) {
        this.hasAfter = hasAfter;
    }
}
