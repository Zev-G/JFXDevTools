package com.me.tmw.nodes.control;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.*;

public class ArrowHead extends Path {

    private final DoubleProperty width = new SimpleDoubleProperty(this, "width", 5);
    private final DoubleProperty height = new SimpleDoubleProperty(this, "height", 5);
    private final DoubleProperty curve = new SimpleDoubleProperty(this, "curve", 0.005);

    public ArrowHead() {
        MoveTo moveTo = new MoveTo();
        moveTo.setX(0.0f);
        moveTo.setY(0.0f);


        /*       B
               /  \
             /     \
           /        \
         A --------- C     */

        DoubleProperty[] calculatedPoints = calculate(width, height);

        LineTo toB = new LineTo();
        toB.xProperty().bind(calculatedPoints[0]);
        toB.yProperty().bind(calculatedPoints[1]);

        LineTo toC = new LineTo();
        toC.xProperty().bind(calculatedPoints[2]);
        toC.yProperty().bind(calculatedPoints[3]);

        LineTo toA = new LineTo();
        toA.setX(0);
        toA.setY(0);

        getElements().addAll(
                moveTo,
                toB,
                toC,
                toA
        );

    }

    public void setSize(double size) {
        setWidth(size);
        setHeight(size);
    }

    public double getWidth() {
        return width.get();
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double width) {
        this.width.set(width);
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    private static DoubleProperty[] calculate(DoubleProperty width, DoubleProperty height) {
        DoubleProperty pointX = new SimpleDoubleProperty();
        DoubleProperty pointY = new SimpleDoubleProperty();
        pointX.bind(Bindings.divide(width, 2));
        pointY.bind(height);

        DoubleProperty endX = new SimpleDoubleProperty();
        DoubleProperty endY = new SimpleDoubleProperty(0);
        endX.bind(width);

        return new DoubleProperty[] {
                pointX, pointY,
                endX, endY
        };
    }

}
