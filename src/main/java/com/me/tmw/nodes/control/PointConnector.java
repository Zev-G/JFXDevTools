package com.me.tmw.nodes.control;

import javafx.scene.shape.Line;

public class PointConnector extends Line {

    private final Point beginning;
    private final Point end;

    public PointConnector(Point beginning, Point end) {
        getStyleClass().add("point-connector");

        this.beginning = beginning;
        this.end = end;

        beginning.getConnectors().add(this);
        end.getConnectors().add(this);

        startXProperty().bind(beginning.layoutXProperty().add(beginning.contentWidthProperty().divide(2)));
        startYProperty().bind(beginning.layoutYProperty().add(beginning.contentHeightProperty().divide(2)));

        endXProperty().bind(end.layoutXProperty().add(end.contentWidthProperty().divide(2)));
        endYProperty().bind(end.layoutYProperty().add(end.contentHeightProperty().divide(2)));
    }

    public void dispose() {
        startXProperty().unbind();
        startYProperty().unbind();
        endXProperty().unbind();
        endYProperty().unbind();
        beginning.getConnectors().remove(this);
        end.getConnectors().remove(this);
    }

    public Point getBeginning() {
        return beginning;
    }
    public Point getEnd() {
        return end;
    }

}
