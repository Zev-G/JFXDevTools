package com.me.tmw.debug.devtools.nodeinfo.css;

import javafx.scene.Cursor;

public class CursorCssValue extends EnumCssValue<CursorCssValue.CursorEnum> {

    CursorCssValue(Cursor initialValue, Runnable update) {
        super(CursorEnum.class, CursorEnum.fromCursor(initialValue), update);
    }

    protected enum CursorEnum {
        INHERIT(null),
        CROSSHAIR(Cursor.CROSSHAIR),
        DEFAULT(Cursor.DEFAULT),
        HAND(Cursor.HAND),
        MOVE(Cursor.MOVE),
        E_RESIZE(Cursor.E_RESIZE),
        NE_RESIZE(Cursor.NE_RESIZE),
        NW_RESIZE(Cursor.NW_RESIZE),
        N_RESIZE(Cursor.N_RESIZE),
        SE_RESIZE(Cursor.SE_RESIZE),
        W_RESIZE(Cursor.W_RESIZE),
        V_RESIZE(Cursor.V_RESIZE),
        TEXT(Cursor.TEXT),
        WAIT(Cursor.WAIT);

        final Cursor cursor;

        CursorEnum(Cursor cursor) {
            this.cursor = cursor;
        }

        public Cursor getCursor() {
            return cursor;
        }

        @Override
        public String toString() {
            return cursor == null ? "inherit" : cursor.toString().replaceAll("_", "-").toLowerCase();
        }

        protected static CursorEnum fromCursor(Cursor cursor) {
            if (cursor == null) return INHERIT;

            for (CursorEnum cursorEnum : values()) {
                if (cursorEnum.getCursor() == cursor) {
                    return cursorEnum;
                }
            }

            return INHERIT;
        }

    }

}
