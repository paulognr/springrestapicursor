package com.paulognr.cursor.api;

import java.util.ArrayList;

public class CursorArrayList<E> extends ArrayList<E> implements CursorList<E> {

    private boolean hasBefore = false;
    private String before;

    private boolean hasAfter = false;
    private String after;

    @Override
    public boolean hasBefore() {
        return this.hasBefore;
    }

    @Override
    public void setBefore(String before) {
        if (before != null && !before.trim().isEmpty()){
            this.before = before.trim();
            this.hasBefore = true;
        } else {
            this.hasBefore = false;
        }
    }

    @Override
    public String getBefore() {
        return this.before;
    }

    @Override
    public boolean hasAfter() {
        return this.hasAfter;
    }

    @Override
    public void setAfter(String after) {
        if (after != null && !after.trim().isEmpty()){
            this.after = after.trim();
            this.hasAfter = true;
        } else {
            this.hasAfter = false;
        }
    }

    @Override
    public String getAfter() {
        return this.after;
    }
}
