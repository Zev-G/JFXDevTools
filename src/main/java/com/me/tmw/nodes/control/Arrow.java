package com.me.tmw.nodes.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Arrow extends HBox {

    private final ArrowHead head = new ArrowHead();
    private final Line line = new Line();

    private final DoubleProperty strokeWidth = new SimpleDoubleProperty(this, "strokeWidth");
    private final DoubleProperty length = new SimpleDoubleProperty(this, "length", 100);

    public Arrow() {
        this(1);
    }

    public Arrow(double headSize) {
        this(headSize, 1);
    }

    public Arrow(double headSize, double initialStrokeWidth) {
        strokeWidth.set(initialStrokeWidth);
        head.setSize(headSize);

        head.setRotate(-90);
        setAlignment(Pos.CENTER);

        setSpacing(-1);

        head.strokeWidthProperty().bind(strokeWidth);
        line.strokeWidthProperty().bind(strokeWidth);

        line.setStroke(Color.BLACK);
        line.startYProperty().bind(Bindings.divide(heightProperty(), 2));
        line.endYProperty().bind(line.startYProperty());
        line.setStartX(0);
        line.endXProperty().bind(lengthProperty());

        getChildren().addAll(line, head);
    }

    public double getStrokeWidth() {
        return strokeWidth.get();
    }

    public DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    public double getLength() {
        return length.get();
    }

    public DoubleProperty lengthProperty() {
        return length;
    }

    public void setLength(double length) {
        this.length.set(length);
    }

    public Line getLine() {
        return line;
    }

    public ArrowHead getHead() {
        return head;
    }
}
