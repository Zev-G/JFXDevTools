package com.me.tmw.nodes.control;

import javafx.scene.shape.Line;

public class PointConnector extends Line {

    private final Point beginning;
    private final Point end;

    public PointConnector(Point beginning, Point end) {
        this.beginning = beginning;
        this.end = end;

        beginning.getConnectors().add(this);
        end.getConnectors().add(this);

        startXProperty().bind(beginning.layoutXProperty());
        startYProperty().bind(beginning.layoutYProperty());
        endXProperty().bind(end.layoutXProperty());
        endYProperty().bind(end.layoutYProperty());
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
