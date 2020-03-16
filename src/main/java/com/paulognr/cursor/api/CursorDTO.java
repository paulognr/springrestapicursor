package com.paulognr.cursor.api;

public class CursorDTO<E> {
    private CursorList<E> data;
    private Cursor cursor;

    public CursorDTO () {}

    public CursorDTO (CursorList<E> data) {
        super();
        this.setData(data);
    }

    public CursorList<E> getData() {
        return data;
    }

    public void setData(CursorList<E> data) {
        this.data = data;
        this.cursor = new Cursor(data);
    }

    public Cursor getCursor() {
        return cursor;
    }
}