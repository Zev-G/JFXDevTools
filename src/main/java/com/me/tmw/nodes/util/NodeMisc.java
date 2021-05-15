package com.me.tmw.nodes.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;

public final class NodeMisc {

    public static final SnapshotParameters TRANSPARENT_SNAPSHOT_PARAMETERS = new SnapshotParameters();

    static {
        TRANSPARENT_SNAPSHOT_PARAMETERS.setFill(Color.TRANSPARENT);
    }

}
