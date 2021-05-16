package com.me.tmw.nodes.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;

public final class NodeMisc {

    public static final SnapshotParameters TRANSPARENT_SNAPSHOT_PARAMETERS = new SnapshotParameters();

    static {
        TRANSPARENT_SNAPSHOT_PARAMETERS.setFill(Color.TRANSPARENT);
    }

    @SafeVarargs
    public static <T> boolean allEqual(T... vals) {
        if (vals.length <= 1) return true;
        T first = vals[0];
        for (int i = 1; i < vals.length; i++) {
            if (vals[i] != first) {
                return false;
            }
        }
        return true;
    }

}
