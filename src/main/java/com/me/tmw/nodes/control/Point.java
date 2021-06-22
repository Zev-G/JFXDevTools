package com.me.tmw.nodes.control;

import com.me.tmw.nodes.util.DragData;
import com.me.tmw.nodes.util.NodeMisc;
import com.me.tmw.properties.NodeProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Stack;

public class Point {

    private final BooleanProperty proportional = new SimpleBooleanProperty(this, "proportional");
    private final BooleanProperty xLocked = new SimpleBooleanProperty(this, "xLocked");
    private final BooleanProperty yLocked = new SimpleBooleanProperty(this, "yLocked");
    private final BooleanProperty movable = new SimpleBooleanProperty(this, "movable", true);
    private final BooleanProperty clamped = new SimpleBooleanProperty(this, "clamped", true);
    private final BooleanProperty centered = new SimpleBooleanProperty(this, "centered");
    private final DoubleProperty x = new SimpleDoubleProperty(this, "x");
    private final DoubleProperty y = new SimpleDoubleProperty(this, "y");

    private final Stack<PointConnector> connectors = new Stack<>();

    private final NodeProperty display = new NodeProperty(this, "display");
    private final ReadOnlyObjectWrapper<PointsEditor> editor = new ReadOnlyObjectWrapper<>(this, "editor");

    private final DragData drag = new DragData();

    private final Region content = new Region() {
        {
            NodeMisc.runAndAddListener(display, observable -> {
                if (display.get() == null) getChildren().clear();
                else if (!getChildren().contains(display.get())) getChildren().setAll(display.get());
            });

            setOnMousePressed(event -> {
                drag.setStartXPos(getLayoutX());
                drag.setStartYPos(getLayoutY());
                drag.setStartScreenX(event.getScreenX());
                drag.setStartScreenY(event.getScreenY());
            });

            setOnMouseDragged(event -> {
                if (isMovable()) {
                    double x = drag.calculateX(event.getScreenX());
                    double y = drag.calculateY(event.getScreenY());
                    if (isProportional()) {
                        if (!isXLocked()) setX(x / getEditor().getWidth());
                        if (!isYLocked()) setY(y / getEditor().getHeight());
                    } else {
                        if (!isXLocked()) setX(x);
                        if (!isYLocked()) setY(y);
                    }
                }
            });
        }
    };

    public Point() {
        this(0, 0, true, false, false, defaultDisplay());
    }
    public Point(Node node) {
        this(0, 0, true, false, false, node);
    }
    public Point(boolean proportional) {
        this (0, 0, proportional, false, false, defaultDisplay());
    }
    public Point(boolean proportional, Node node) {
        this (0, 0, proportional, false, false, node);
    }
    public Point(double x, double y) {
        this(x, y, true, false, false, defaultDisplay());
    }
    public Point(double x, double y, Node node) {
        this(x, y, true, false, false, node);
    }
    public Point(double x, double y, boolean proportional) {
        this(x, y, proportional, false, false, defaultDisplay());
    }
    public Point(double x, double y, boolean proportional, Node node) {
        this(x, y, proportional, false, false, node);
    }
    public Point(double x, double y, boolean proportional, boolean xLocked, boolean yLocked, Node node) {
        setX(x);
        setY(y);
        setXLocked(xLocked);
        setYLocked(yLocked);
        setProportional(proportional);
        setDisplay(node);
        InvalidationListener reloadLayout = observable -> {
            if (getEditor() != null) {
                layout(getEditor().getWidth(), getEditor().getHeight());
            }
        };
        this.proportional.addListener(reloadLayout);
        this.x.addListener(reloadLayout);
        this.y.addListener(reloadLayout);
        this.clamped.addListener(reloadLayout);
        this.centered.addListener(reloadLayout);
    }

    private static Node defaultDisplay() {
        return new Circle(5, Color.DARKGRAY);
    }

    private double[] clampInEditor(double calculateX, double calculateY) {
        if (!isClamped()) return new double[]{ calculateX, calculateY };
        if (getEditor() == null) return null;
        calculateX = Math.min(Math.max(calculateX, 0), getEditor().getWidth());
        calculateY = Math.min(Math.max(calculateY, 0), getEditor().getHeight());
        return new double[]{ calculateX, calculateY };
    }

    /* **********************************************
     *
     *  Package-private methods
     *
     * **********************************************/

    void layout(double width, double height) {
        double x;
        double y;
        if (isProportional()) {
            x = getX() * width;
            y = getY() * height;
        } else {
            x = getX();
            y = getY();
        }
        if (isCentered()) {
            x -= content.getWidth()  / 2;
            y -= content.getHeight() / 2;
        }
        if (isClamped() && getEditor() != null) {
            double[] clamped = clampInEditor(x, y);
            assert clamped != null; // Clamped will only be null if getEditor() == null.
            x = clamped[0];
            y = clamped[1];
        }
        content.relocate(x, y);
    }

    Parent getContent() {
        return content;
    }
    Stack<PointConnector> getConnectors() {
        return connectors;
    }

    void setEditor(PointsEditor editor) {
        this.editor.set(editor);
    }

    /* **********************************************
     *
     *  Public methods
     *
     * **********************************************/

    public DoubleProperty layoutXProperty() {
        return content.layoutXProperty();
    }
    public double getLayoutX() {
        return content.getLayoutX();
    }

    public DoubleProperty layoutYProperty() {
        return content.layoutYProperty();
    }
    public double getLayoutY() {
        return content.getLayoutY();
    }

    public PointsEditor getEditor() {
        return editor.get();
    }
    public ReadOnlyObjectProperty<PointsEditor> editorProperty() {
        return editor.getReadOnlyProperty();
    }

    public boolean isClamped() {
        return clamped.get();
    }
    public BooleanProperty clampedProperty() {
        return clamped;
    }
    public void setClamped(boolean clamped) {
        this.clamped.set(clamped);
    }

    public boolean isCentered() {
        return centered.get();
    }
    public BooleanProperty centeredProperty() {
        return centered;
    }
    public void setCentered(boolean centered) {
        this.centered.set(centered);
    }

    public boolean isMovable() {
        return movable.get();
    }
    public BooleanProperty movableProperty() {
        return movable;
    }
    public void setMovable(boolean movable) {
        this.movable.set(movable);
    }

    public boolean isProportional() {
        return proportional.get();
    }
    public BooleanProperty proportionalProperty() {
        return proportional;
    }
    public void setProportional(boolean proportional) {
        this.proportional.set(proportional);
    }

    public boolean isXLocked() {
        return xLocked.get();
    }
    public BooleanProperty xLockedProperty() {
        return xLocked;
    }
    public void setXLocked(boolean xLocked) {
        this.xLocked.set(xLocked);
    }

    public boolean isYLocked() {
        return yLocked.get();
    }
    public BooleanProperty yLockedProperty() {
        return yLocked;
    }
    public void setYLocked(boolean yLocked) {
        this.yLocked.set(yLocked);
    }

    public double getX() {
        return x.get();
    }
    public DoubleProperty xProperty() {
        return x;
    }
    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }
    public DoubleProperty yProperty() {
        return y;
    }
    public void setY(double y) {
        this.y.set(y);
    }

    public Node getDisplay() {
        return display.get();
    }
    public NodeProperty displayProperty() {
        return display;
    }
    public void setDisplay(Node display) {
        this.display.set(display);
    }

    public void clearConnectors() {
        for (int i = connectors.size() - 1; i >= 0; i--) {
            connectors.get(i).dispose();
        }
    }

}
